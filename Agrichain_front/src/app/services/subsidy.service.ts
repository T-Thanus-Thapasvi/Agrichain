import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubsidyProgram, SubsidyProgramCreateRequest, SubsidyProgramUpdateRequest } from '../models/subsidy.model';
import { DisbursementDTO, Disbursement } from '../models/dto.model';
import { API_URL } from '../elements/constants';

@Injectable({ providedIn: 'root' })
export class SubsidyService {
  private http = inject(HttpClient);
  private apiUrl = `${API_URL}subsidy-programs`;
  private disbursementUrl = `${API_URL}disbursements`;

  /**
   * Fetch all subsidy programs
   */
  getAllPrograms(): Observable<SubsidyProgram[]> {
    return this.http.get<SubsidyProgram[]>(`${this.apiUrl}`);
  }

  /**
   * Create a new subsidy program
   */
  createProgram(program: SubsidyProgramCreateRequest): Observable<SubsidyProgram> {
    return this.http.post<SubsidyProgram>(`${this.apiUrl}`, program);
  }

  /**
   * Update an existing subsidy program
   */
  updateProgram(program: SubsidyProgramUpdateRequest): Observable<SubsidyProgram> {
    return this.http.put<SubsidyProgram>(`${this.apiUrl}/${program.programID}`, program);
  }

  /**
   * Delete a subsidy program
   */
  deleteProgram(programID: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${programID}`, { responseType: 'text' });
  }

  /**
   * Apply for a subsidy program (create disbursement)
   */
  applyForSubsidy(disbursement: DisbursementDTO): Observable<Disbursement> {
    return this.http.post<Disbursement>(`${this.disbursementUrl}`, disbursement);
  }

  /**
   * Get all disbursements for a specific farmer
   */
  getFarmerDisbursements(farmerId: number): Observable<Disbursement[]> {
    return this.http.get<Disbursement[]>(`${this.disbursementUrl}/farmer/${farmerId}`);
  }

  /**
   * Get a specific disbursement by ID
   */
  getDisbursement(disbursementId: number): Observable<Disbursement> {
    return this.http.get<Disbursement>(`${this.disbursementUrl}/${disbursementId}`);
  }
}
