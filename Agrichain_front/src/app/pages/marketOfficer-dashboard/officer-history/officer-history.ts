// import { Component } from '@angular/core';

// @Component({
//   selector: 'app-officer-history',
//   imports: [],
//   templateUrl: './officer-history.html',
//   styleUrl: './officer-history.css',
// })
// export class OfficerHistory {}


import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MarketService } from '../../../services/market';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-officer-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './officer-history.html',
  styleUrl: './officer-history.css'
})
export class OfficerHistory implements OnInit {

  // historyLogs = [
  //   { id: 1, action: 'APPROVED', target: 'Crop Listing', targetId: 101, reason: 'Quality standards met', time: new Date() },
  //   { id: 2, action: 'REJECTED', target: 'Document', targetId: 55, reason: 'Image blurry/unreadable', time: new Date(Date.now() - 3600000) },
  //   { id: 3, action: 'VERIFIED', target: 'Farmer Profile', targetId: 202, reason: 'Aadhar verified', time: new Date(Date.now() - 86400000) }
  // ];

  logs: any[] = [];
  isLoading: boolean = false; // Start as false

  constructor(
    private marketService: MarketService,
    private cdr: ChangeDetectorRef // 2. Inject this
  ) {}

  ngOnInit(): void {
    this.loadHistory();
  }
  // loadHistory() {
  //   this.isLoading = true;
  //   this.marketService.getAuditLogs().subscribe({
  //     next: (data) => {
  //       // Sort by timestamp (newest first) if backend doesn't do it
  //       this.logs = data.sort((a, b) => 
  //         new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
  //       );
  //       this.isLoading = false;
  //     },
  //     error: (err) => {
  //       console.error("Failed to load history", err);
  //       this.isLoading = false;
  //     }
  //   });
  // }

  loadHistory(): void {
    this.isLoading = true;
    
    // 3. Force Angular to notice the 'true' value before the HTTP call starts
    this.cdr.detectChanges(); 

    this.marketService.getAllLogs().subscribe({
      next: (data: any[]) => {
        this.logs = data.sort((a: any, b: any) => 
          new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
        );
        this.isLoading = false;
        this.cdr.detectChanges(); // Check again after data arrives
      },
      error: (err: any) => {
        console.error("Failed to load history", err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Helper to determine the status color/icon
  getLogIcon(action: string): string {
    if (action.includes('Approved')) return '✅';
    if (action.includes('Rejected')) return '❌';
    return '🕒';
  }

  getStatusClass(action: string): any {
    return {
      'status-approve': action.includes('APPROVED') || action.includes('VERIFIED'),
      'status-reject': action.includes('REJECTED')
    };
  }
}