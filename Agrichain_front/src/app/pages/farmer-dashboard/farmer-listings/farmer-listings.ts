// import { Component, OnInit, inject, signal } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { CommonModule } from '@angular/common';

// @Component({
//   selector: 'app-farmer-listings',
//   imports: [CommonModule],
//   templateUrl: './farmer-listings.html',
//   styleUrl: './farmer-listings.css',
// })
// export class FarmerListings implements OnInit {
//   private http = inject(HttpClient);

//   // Replace this with the actual logged-in user ID from your AuthService
//   currentFarmerId = 9; 

//   myListings = signal<any[]>([]);

//   ngOnInit() {
//     this.fetchMyListings();
//   }

//   fetchMyListings() {
//     this.http.get<any[]>(`http://localhost:8090/market/listings/farmer/${this.currentFarmerId}`)
//       .subscribe(data => {
//         this.myListings.set(data);
//       });
//   }
// }

import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth-service';
import { VerifyPending } from '../../../common-components/verify-pending/verify-pending';
import { API_URL, FARMER_REGI, USER_INFO } from '../../../elements/constants';
import { Farmer, User } from '../../../models/user.model';

@Component({
  selector: 'app-farmer-listings',
  standalone: true,
  imports: [CommonModule, VerifyPending],
  templateUrl: './farmer-listings.html',
  styleUrl: './farmer-listings.css',
})
export class FarmerListings implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  isApproved = signal(false);

  myListings = signal<any[]>([]);

  ngOnInit() {
    this.fetchMyListings();
    const user: User = JSON.parse(localStorage.getItem(USER_INFO) || '{}');
    this.http.get<Farmer>(`${API_URL}farmers/get-by-userid/${user.id}`)
            .subscribe({
              next: (data) => {
                this.isApproved.set(data.status === "APPROVED");
              },
              error: (err) => {
                this.isApproved.set(false);
              }
            });
  }

  fetchMyListings() {
    const farmer = JSON.parse(localStorage.getItem(FARMER_REGI) || '{}');
    const currentFarmerId = farmer?.farmerId;

    if (currentFarmerId) {
      this.http.get<any[]>(`http://localhost:8090/market/listings/farmer/${currentFarmerId}`)
        .subscribe({
          next: (data) => {
            this.myListings.set(data);
          },
          error: (err) => {
            console.error("Failed to load listings:", err);
          }
        });
    } else {
      console.error("No logged-in user found in AuthService signals!");
    }
  }
}