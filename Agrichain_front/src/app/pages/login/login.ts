import { Component, signal, inject, computed } from '@angular/core';
import { Router } from '@angular/router';
import { WebNameElement } from "../../elements/web-name";
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { ThemeService } from '../../services/theme';
import { ToastService } from '../../services/toast-service';
import { faExclamation, faCheck } from '@fortawesome/free-solid-svg-icons';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";

@Component({
	selector: 'app-login',
	imports: [WebNameElement, FormsModule, FaIconComponent],
	templateUrl: './login.html',
	styleUrl: './login.css'
})
export class LoginPage {
	private router = inject(Router);
	private authService = inject(AuthService);
	private toast = inject(ToastService);
	themeService = inject(ThemeService);

	faInfo = faExclamation;
	faCheck = faCheck;

	account_method = signal("login");
	constructor() {
		this.themeService.themeChange("FARMER");
	}
	role_selected = computed(() => this.themeService.themeRole());

	formData = {
		name: '',
		email: '',
		phone: '',
		password: '',
		confirmPassword: ''
	};

	validPass = signal(false);
	check1 = signal(false);
	check2 = signal(false);
	check3 = signal(false);
	check4 = signal(false);
	check5 = signal(false);

	validatePassword() {
		const password = this.formData.password;

		this.check1.set(password.length >= 8);
		this.check2.set(/[A-Z]/.test(password));
		this.check3.set(/[a-z]/.test(password));
		this.check4.set(/[0-9]/.test(password));
		this.check5.set(/[!@#$%^&*(),.?":{}|<>]/.test(password));

		const isValid = this.check1() && this.check2() && this.check3() && this.check4() && this.check5();

		this.validPass.set(isValid);
	}

	onSubmit() {
		const payload = {
			...this.formData,
			role: this.themeService.role_selected()
		};

		if (this.account_method() === 'login') {
			if(payload.email === "") {
				this.toast.show('Please enter Email', 'alert');
				return;
			}
			if(payload.password === "") {
				this.toast.show('Please enter password', 'alert');
				return;
			}

			this.authService.onLogin({
				email: payload.email,
				password: payload.password
			}).subscribe({
				next: () => {
					const currentUser = this.authService.currentUser();
					if(currentUser) {
						this.themeService.themeChange(currentUser.role);
					}
					this.router.navigate([`/dashboard/${currentUser?.role.toLocaleLowerCase()}`]);
				},
				error: (err) => {
					console.error("Login failed", err);
					if (err.status === 409) {
						this.toast.show('Incorrect password. Please try again.', 'alert');
					} else if (err.status === 404) {
						this.toast.show('User account not found', 'alert');
					} else {
						this.toast.show('An unexpected error occurred. Please try again later.', 'alert');
					}
				}
			});
		} else {
			if(payload.email === "") {
				this.toast.show('Please enter Email', 'alert');
				return;
			}
			if(payload.phone === "") {
				this.toast.show('Please enter Phone number', 'alert');
				return;
			}
			if(payload.password === "") {
				this.toast.show('Please enter password', 'alert');
				return;
			}
			if(!this.validPass()) {
				this.toast.show('this Password is not valid', 'alert');
				return;
			}

			if (this.formData.password !== this.formData.confirmPassword) {
				this.toast.show('Passwords do not match!', 'alert');
				return;
			}
			this.authService.onRegister(payload).subscribe({
				next: () => {
					if(this.themeService.role_selected() === "FARMER" ) {
						this.toast.show('Signup successful! login to fill register details.', 'success');
					} else {
						this.toast.show('Signup successful! Please login.', 'success');
					}
					this.onAccountMethodChange('login');
				},
				error: (err) => console.error("Signup failed", err)
			});
		}
	}

	onAccountMethodChange(newMethod: string) {
		this.account_method.set(newMethod);
		this.onRoleChange("FARMER");
	}

	onRoleChange(newRole: string) {
		this.themeService.themeChange(newRole);
	}
}