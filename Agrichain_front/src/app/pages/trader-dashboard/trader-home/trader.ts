// import { Component, signal, inject, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { MarketService } from '../../../services/market';
// import { CropListingStatus } from '../../../models/enum.model';
// import { TraderApiService } from '../../../services/trader.service';

// @Component({
//   selector: 'app-trader-dashboard',
//   standalone: true,
//   imports: [CommonModule],
//   templateUrl: './trader.html',
//   styleUrl: './trader.css'
// })
// export class TraderPage implements OnInit {
//   private marketService = inject(MarketService);
//   private traderservice = inject(TraderApiService);
  
//   activeModule = 'dashboard';
//   currentTab = signal<'home' | 'listings'>('home');

//   listings = signal<any[]>([]);
//   myOrders = signal<any[]>([]);
//   myTransactions = signal<any[]>([]);
//   auditLogs = signal<any[]>([]);
  
//   // Metrics
//   traderId = 101;
//   pendingPaymentsCount: number = 0;

//   constructor() {
//     // Initial dummy data for visual testing before API responses arrive
//     this.myTransactions.set([
//       { transactionId: 101, orderId: 5001, transactionAmount: 15000, transactionDate: new Date(), transactionStatus: 'PENDING' },
//       { transactionId: 102, orderId: 5002, transactionAmount: 8500, transactionDate: new Date(), transactionStatus: 'COMPLETED' }
//     ]);

//     this.auditLogs.set([
//       { timestamp: new Date(), targetType: 'CROP', targetId: 201, action: 'APPROVED', reason: 'Quality standards met' },
//       { timestamp: new Date(), targetType: 'CROP', targetId: 202, action: 'REJECTED', reason: 'Incomplete documentation' }
//     ]);
//   }

//   // ngOnInit() {
//   //   this.loadInitialData();
//   // }

//   // loadInitialData() {
//   //   this.marketService.getListingsByStatus(CropListingStatus.VALIDATED).subscribe(data => {
//   //     this.listings.set(data);
//   //   });

//   //   this.marketService.getAllLogs().subscribe(logs => {
//   //     this.auditLogs.set(logs);
//   //   });
//   // }

//   ngOnInit(): void {
//     this.loadMarketCrops();
//   }

//   loadMarketCrops(): void {
//   // Only fetch items that the Officer has already approved
//   this.marketService.getListingsByStatus('VALIDATED').subscribe({
//     next: (data) => {
//       this.pendingListings = data; // This array will now only contain validated crops
//       console.log("Trader Market Data:", data);
//     },
//     error: (err) => {
//       console.error('Failed to load validated market data', err);
//     }
//   });
// }
// }

import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarketService } from '../../../services/market';
import { TraderApiService } from '../../../services/trader.service';

@Component({
  selector: 'app-trader-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trader.html',
  styleUrl: './trader.css'
})
export class TraderPage implements OnInit {
  private marketService = inject(MarketService);
  private traderservice = inject(TraderApiService);
  
  // Navigation & UI State
  activeModule = 'dashboard';
  currentTab = signal<'home' | 'listings'>('home');

  // DATA PROPERTIES
  // Fix: Declaring pendingListings to resolve TS2339 compiler error
  pendingListings: any[] = []; 
  
  // Signals for reactive data
  listings = signal<any[]>([]);
  myOrders = signal<any[]>([]);
  myTransactions = signal<any[]>([]);
  auditLogs = signal<any[]>([]);
  
  // Metrics
  traderId = 101;
  pendingPaymentsCount: number = 0;

  constructor() {
    // Initial dummy data for visual testing
    this.myTransactions.set([
      { transactionId: 101, orderId: 5001, transactionAmount: 15000, transactionDate: new Date(), transactionStatus: 'PENDING' },
      { transactionId: 102, orderId: 5002, transactionAmount: 8500, transactionDate: new Date(), transactionStatus: 'COMPLETED' }
    ]);

    this.auditLogs.set([
      { timestamp: new Date(), targetType: 'CROP', targetId: 201, action: 'APPROVED', reason: 'Quality standards met' },
      { timestamp: new Date(), targetType: 'CROP', targetId: 202, action: 'REJECTED', reason: 'Incomplete documentation' }
    ]);
  }

  ngOnInit(): void {
    this.loadMarketCrops();
    this.loadAuditLogs();
  }

  loadMarketCrops(): void {
  console.log("📡 Fetching validated crops...");
  this.marketService.getListingsByStatus('VALIDATED').subscribe({
    next: (data) => {
      // 1. Update the signal that the HTML summary cards use
      this.listings.set(data); 
      
      // 2. Keep this if your table uses the standard array
      this.pendingListings = data; 
      
      console.log("✅ Trader Market Data Loaded into Signals:", data);
    },
    error: (err) => {
      console.error('❌ Failed to load validated market data', err);
    }
  });
}

  /**
   * Fetches audit logs to show system activity
   */
  loadAuditLogs(): void {
    this.marketService.getAllLogs().subscribe({
      next: (logs) => {
        this.auditLogs.set(logs);
      },
      error: (err) => console.error('❌ Failed to load logs', err)
    });
  }

  /**
   * Helper to switch between dashboard modules
   */
  setModule(moduleName: string): void {
    this.activeModule = moduleName;
  }
}