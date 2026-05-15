import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditDTO, AuditLogDto } from '../models/dto.model';
import { AuditScope, AuditStatus } from '../models/enum.model';
import { API_URL, AUDIT_PATH } from '../elements/constants';

export interface PageResponse<T> {
	content: T[];
	totalElements: number;
	totalPages: number;
	size: number;
	number: number;
	last: boolean;
	first: boolean;
	empty: boolean;
}

@Injectable({
	providedIn: 'root'
})
export class AuditService {
	private http = inject(HttpClient);
	private readonly apiUrl = `${API_URL}${AUDIT_PATH}`;

	constructor() {
		console.log('AuditService initialized with apiUrl:', this.apiUrl);
	}

	getAuditLogs(page: number = 0, size: number = 10, sortBy: string = 'timestamp'): Observable<PageResponse<AuditLogDto>> {
		let params = new HttpParams()
			.set('page', page.toString())
			.set('size', size.toString())
			.set('sortBy', sortBy);

		return this.http.get<PageResponse<AuditLogDto>>(`${API_URL}api/logs/GetAllLogs`, { params });
	}

	/**
	 * Fetch summary statistics for the Auditor Home dashboard cards.
	 */
	getAuditStats(): Observable<any> {
		const url = `${this.apiUrl}/stats`;
		console.log('Fetching audit stats from:', url);
		return this.http.get(`${url}`);
	}

	/**
	 * Create a new audit record.
	 * This will be intercepted by authInterceptor to add the Bearer token.
	 */
	createAudit(auditDto: AuditDTO): Observable<AuditDTO> {
		return this.http.post<AuditDTO>(`${this.apiUrl}/create`, auditDto);
	}

	/**
	 * Fetch all audit records from the backend.
	 */
	getAllAudits(): Observable<AuditDTO[]> {
		const url = `${this.apiUrl}/all`;
		console.log('Fetching all audits from:', url);
		const token = localStorage.getItem('agrichain_token');
		console.log('Token exists:', !!token);
		return this.http.get<AuditDTO[]>(url);
	}

	/**
	 * Fetch a single audit by its ID.
	 */
	findAuditById(id: number): Observable<AuditDTO> {
		return this.http.get<AuditDTO>(`${this.apiUrl}/get/${id}`);
	}

	/**
	 * Update an existing audit record.
	 */
	// In audit.service.ts
	updateAudit(id: number, auditData: AuditDTO): Observable<AuditDTO> {
		// Remove the '/update' from the string
		return this.http.put<AuditDTO>(`${this.apiUrl}/${id}`, auditData);
	}

	/**
	 * Delete an audit record by ID.
	 */
	// In audit.service.ts
	deleteAudit(id: number): Observable<void> {
		// Remove the '/delete' part
		return this.http.delete<void>(`${this.apiUrl}/${id}`);
	}

	/**
	 * Helper methods for specific filtering
	 */
	getAuditsByOfficer(officerId: number): Observable<AuditDTO[]> {
		return this.http.get<AuditDTO[]>(`${this.apiUrl}/officer/${officerId}`);
	}

	getAuditsByScope(scope: AuditScope): Observable<AuditDTO[]> {
		return this.http.get<AuditDTO[]>(`${this.apiUrl}/scope/${scope}`);
	}

	getAuditsByStatus(status: AuditStatus): Observable<AuditDTO[]> {
		return this.http.get<AuditDTO[]>(`${this.apiUrl}/status/${status}`);
	}
}