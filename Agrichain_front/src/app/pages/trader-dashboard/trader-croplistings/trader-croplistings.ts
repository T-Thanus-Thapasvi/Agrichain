import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarketService } from '../../../services/market'; // Adjust path based on your folder structure
import { CropListingDTO } from '../../../models/dto.model';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../../../elements/constants';
import { Farmer } from '../../../models/user.model';
import { catchError, finalize, forkJoin, map, switchMap } from 'rxjs';
import { Loader } from '../../../common-components/loader/loader';

@Component({
  selector: 'trader-croplistings',
  standalone: true,
  imports: [CommonModule, Loader],
  templateUrl: './trader-croplistings.html',
  styleUrl: './trader-croplistings.css'
})
export class TraderCroplistings implements OnInit {
  private marketService = inject(MarketService);
  private http = inject(HttpClient);

  // Signal to hold the 50+ items we saw in your console
  listings = signal<CropListingDTO[]>([]);

  ngOnInit(): void {
    this.loadMarketCrops();
  }


  isLoading = signal<boolean>(false);
  cropLists = signal<CropListingDTO[]>([]);

  loadMarketCrops(): void {
    this.isLoading.set(true);

    this.marketService.getListingsByStatus('VALIDATED').pipe(
      switchMap((listings: any[]) => {
        if (!listings || listings.length === 0) return [[]];

        const requests = listings.map(d =>
          this.http.get<Farmer>(`${API_URL}farmers/${d.farmerId}`).pipe(
            map(farmerData => ({
              ...d,
              farmerNumber: farmerData.contactInfo
            })),
            catchError(() => {
              console.warn(`Could not fetch details for farmer ${d.farmerId}`);
              return [{ ...d, farmerNumber: 'N/A' }];
            })
          )
        );
        return forkJoin(requests);
      }),
      finalize(() => this.isLoading.set(false))
    ).subscribe({
      next: (enrichedCrops) => this.cropLists.set(enrichedCrops),
      error: (err) => console.error('Critical failure in stream:', err)
    });
  }
}