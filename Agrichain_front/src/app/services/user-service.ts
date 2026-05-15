import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { API_URL, USER_PATH, USER_INFO } from '../elements/constants';
import { ThemeService } from './theme';
import { ToastService } from './toast-service';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
    constructor(private http: HttpClient) { }

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${API_URL}${USER_PATH}/all`);
    }
}