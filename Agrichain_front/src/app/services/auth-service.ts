import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Router } from '@angular/router';
import { firstValueFrom, Observable, tap } from 'rxjs';
import { Farmer, User } from '../models/user.model';
import { API_URL, TOKEN_KEY, USER_PATH, USER_INFO, FARMER_REGI, FARMER_DASHBOARD } from '../elements/constants';
import { ThemeService } from './theme';
import { ToastService } from './toast-service';
import { FarmerStatus, UserRole } from '../models/enum.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
	private http = inject(HttpClient);
	private router = inject(Router);
	private jwtHelper = new JwtHelperService();
	private themeService = inject(ThemeService);
	private toast = inject(ToastService);

	private _currentUser = signal<User | null>(null);
	readonly currentUser = this._currentUser.asReadonly();

	private _isFarmerApproved = signal(false);
	readonly isFarmerApproved = this._isFarmerApproved.asReadonly();

	onLogin(credentials: any) {
		return this.http.post<{ token: string, user?: any }>(`${API_URL}${USER_PATH}/login`, credentials).pipe(
			tap(async response => {
				this.saveToken(response.token);
				if (response.user) {
					this._currentUser.set(response.user);
					localStorage.setItem(USER_INFO, JSON.stringify(response.user));

					if (response.user.role === UserRole.FARMER) {
						const registered = await this.isFarmerRegistered(response.user.id);
						if (!registered) {
							this.toast.show('Registration Page', 'info');
							this.router.navigate([`${FARMER_DASHBOARD}register`]);
						}
					}
				}
			})
		);
	}

	async isFarmerRegistered(userId: number): Promise<boolean> {
		try {
			const farmer = await firstValueFrom(
				this.http.get<Farmer>(`${API_URL}farmers/get-by-userid/${userId}`)
			);
			if (farmer) {
				localStorage.setItem(FARMER_REGI, JSON.stringify(farmer));
			}
			if (farmer && farmer.address !== null && farmer.landDetails !== null && farmer.dob !== null) {
				return true;
			}
			return false;
		} catch (error) {
			console.error("Error checking farmer registration", error);
			return false;
		}
	}

	onRegister(userData: any) {
		return this.http.post(`${API_URL}${USER_PATH}/register`, userData);
	}

	updateUser(userData: any) {
		return this.http.patch(`${API_URL}${USER_PATH}/update/${userData.id}`, userData).pipe(
			tap(() => {
				localStorage.setItem(USER_INFO, JSON.stringify(userData));
				this._currentUser.set(userData);
			})
		);
	}

	private saveToken(token: string) {
		localStorage.setItem(TOKEN_KEY, token);
	}

	isLoggedIn(): boolean {
		const token = localStorage.getItem(TOKEN_KEY);
		return token ? !this.jwtHelper.isTokenExpired(token) : false;
	}

	logout(shouldRedirect: boolean = true) {
		localStorage.removeItem(TOKEN_KEY);
		localStorage.removeItem(USER_INFO);
		localStorage.removeItem(FARMER_REGI);

		this._currentUser.set(null);

		if (shouldRedirect)
			this.router.navigate(['/login']);
	}

	constructor() {
		const token = localStorage.getItem(TOKEN_KEY);
		const savedUserInfo = localStorage.getItem(USER_INFO);
		const savedFarmerInfo = localStorage.getItem(FARMER_REGI);

		if (token && savedUserInfo) {
			if (this.jwtHelper.isTokenExpired(token)) {
				this.toast.show('Session Expired! Login again', 'alert')
				this.logout();
			}
			try {
				this._currentUser.set(JSON.parse(savedUserInfo));
				const currentUser = this._currentUser();
				if (currentUser) {
					this.themeService.themeChange(currentUser.role);
				}

			} catch (e) {
				this.logout();
			}
		} else {
			this.logout(false);
		}
	}
}