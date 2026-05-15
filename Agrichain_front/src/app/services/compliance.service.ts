import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ComplianceDTO } from '../models/dto.model';
import { API_URL, COMPLIANCE_PATH } from '../elements/constants'; // Import your new constants

@Injectable({
  providedIn: 'root'
})
export class ComplianceService {
  private http = inject(HttpClient);
  
  // This combines http://localhost:8090/ + api/compliances
  // Result: http://localhost:8090/api/compliances (No double slash!)
  private readonly apiUrl = `${API_URL}${COMPLIANCE_PATH}`;

  addComplianceToAudit(auditId: number, complianceDto: ComplianceDTO): Observable<ComplianceDTO> {
    return this.http.post<ComplianceDTO>(`${this.apiUrl}/create/${auditId}`, complianceDto);
  }

  getAllCompliances(): Observable<ComplianceDTO[]> {
    return this.http.get<ComplianceDTO[]>(`${this.apiUrl}/all`);
  }

  getComplianceById(id: number): Observable<ComplianceDTO> {
    return this.http.get<ComplianceDTO>(`${this.apiUrl}/${id}`);
  }

  updateCompliance(id: number, complianceDto: ComplianceDTO): Observable<ComplianceDTO> {
    return this.http.put<ComplianceDTO>(`${this.apiUrl}/${id}`, complianceDto);
  }

  deleteCompliance(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}