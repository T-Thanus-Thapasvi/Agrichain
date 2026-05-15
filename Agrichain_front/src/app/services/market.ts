import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { OrderDTO } from '../models/dto.model';

@Injectable({
  providedIn: 'root'
})
export class MarketService {
  private gatewayUrl = 'http://localhost:8090';

  private baseUrl = 'http://localhost:8090/disbursements'; // Adjust port as needed
  // Add these to your existing MarketService class
  private transUrl = `${this.gatewayUrl}/transactions`; // Routed via Gateway


  placeOrder(orderDto: any): Observable<any> {
    return this.http.post<any>(`${this.gatewayUrl}/market/placeorder`, orderDto);
  }

  constructor(private http: HttpClient) { }

  // 1. Listings (Updated to accept status and reason to fix TS2554)
  getListingsByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.gatewayUrl}/market/listings/status/${status}`);
  }

  validateListing(id: number, status: string = 'APPROVED', comment: string = ''): Observable<any> {
    // This now accepts up to 3 arguments to satisfy your OfficerHome calls
    return this.http.put(`${this.gatewayUrl}/market/listings/validate/${id}`, { status, comment });
  }

  // 2. Documents (Added these back to fix OfficerHome errors)
  getAllDocuments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.gatewayUrl}/documents/all`);
  }

  getPendingDocuments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.gatewayUrl}/documents/all`); // or specific pending endpoint
  }


  verifyDocument(id: number, status: string): Observable<any> {
    // Ensure the URL matches your Backend @PutMapping or @PostMapping
    return this.http.patch(`${this.gatewayUrl}/documents/${id}/verify?status=${status}`, {});
  }

  getFileUrl(fileName: string): string {
    return `${this.gatewayUrl}/documents/files/${fileName}`;
  }

  getDocumentFileUrl(document: any): string {
    // Assuming documents are served by ID since they're stored as BLOBs
    const docId = document.documentId || document.id;
    return `${this.gatewayUrl}/documents/${docId}/file`;
  }

  // Make sure this method exists
  getAllFarmers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.gatewayUrl}/farmers`);
  }

  getFarmerById(id: number): Observable<any> {
    return this.http.get<any>(`${this.gatewayUrl}/farmers/${id}`);
  }


  // inside market.ts
  getAllLogs(): Observable<any[]> {
    // Replace with your actual backend URL later
    return this.http.get<any[]>(`${this.gatewayUrl}/market/audit-logs`);
  }

  // inside market.ts
  // inside market.ts
  approveCrop(id: number, status: string, reason: string): Observable<any> {
    // Use .patch to match your backend @PatchMapping
    return this.http.patch(`${this.gatewayUrl}/market/listings/validate/${id}`, null, {
      params: {
        status: status,
        reason: reason
      }
    });
  }

  getOrdersByTrader(traderId: number): Observable<OrderDTO[]> {
    return this.http.get<OrderDTO[]>(`${this.gatewayUrl}/market/orders/trader/${traderId}`);
  }

  getSubsidies(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  // Uses the exact Enum strings: 'VALIDATED' or 'REJECTED'
  updateSubsidyStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.baseUrl}/${id}/status?status=${status}`, {});
  }

  // This was missing! It must match the controller @PatchMapping("/{id}/review")
  reviewDisbursement(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.baseUrl}/${id}/review?status=${status}`, {});
  }

}