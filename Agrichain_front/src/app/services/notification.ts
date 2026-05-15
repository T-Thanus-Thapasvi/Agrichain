import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationServiceFarmer {
  constructor(private http: HttpClient) {}

  // Fetch notifications based on a listing ID
  getNotificationsForListing(listingId: number) {
    return this.http.get<any[]>(`http://localhost:8080/market/notifications/${listingId}`);
  }
}