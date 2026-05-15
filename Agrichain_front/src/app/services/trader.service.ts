import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TraderApiService {
  private http = inject(HttpClient);

  // Endpoint configuration
  private marketApi = 'http://localhost:8090/market';
  private transApi = 'http://localhost:8090/transactions';

  // --- MARKET SERVICE CALLS ---

  // src/app/services/market.service.ts
  getListingsByStatus(status: string): Observable<any[]> {
    // Path must match: /market/listings/status/{status}
    return this.http.get<any[]>(`${this.marketApi}/market/listings/status/${status}`);
  }


  getApprovedListings(): Observable<any[]> {
    return this.http.get<any[]>(`${this.marketApi}/listings/status/APPROVED`);
  }

  placeOrder(orderDto: any): Observable<any> {
    return this.http.post<any>(`${this.marketApi}/placeorder`, orderDto);
  }

  getOrdersByTrader(traderId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.marketApi}/orders/trader/${traderId}`);
  }

  /**
   * New: Fetch audit logs for the Intelligence Module
   * Maps to CropMarketController: @GetMapping("/audit-logs")
   */
  getAuditLogs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.marketApi}/audit-logs`);
  }

  // --- TRANSACTION SERVICE CALLS ---

  initiateTransaction(txRequest: any): Observable<any> {
    return this.http.post<any>(`${this.transApi}/initiate`, txRequest);
  }

  getTransactionsByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.transApi}/status/${status}`);
  }

  /**
   * New: Finalize a pending transaction
   * Maps to TransactionController: @PutMapping("/{id}/finalize")
   */
  finalizeTransaction(transactionId: number): Observable<any> {
    return this.http.put<any>(`${this.transApi}/${transactionId}/finalize`, {});
  }
}