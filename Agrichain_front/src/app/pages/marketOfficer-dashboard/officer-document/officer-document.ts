import { Component, OnInit } from '@angular/core';
import { MarketService } from '../../../services/market'; 
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-officer-documents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './officer-document.html',
  styleUrl: './officer-document.css'
})
export class OfficerDocument implements OnInit {
  listingsWithDocs: any[] = [];
  selectedListing: any = null;

  constructor(private marketService: MarketService) {}

  ngOnInit(): void {
  console.log("Component Initialized!");
  this.refreshQueue();
}

  refreshQueue() {
    this.marketService.getAllDocuments().subscribe({
      next: (data: any[]) => {
        console.log("Received Documents:", data);
        this.listingsWithDocs = data;
      },
      error: (err) => console.error("Fetch Error:", err)
    });
  }

  previewDoc(doc: any) {
    this.selectedListing = doc;
  }

  getFileUrl(doc: any): string {
    if (!doc || !doc.fileName) return '#';
    return this.marketService.getFileUrl(doc.fileName);
  }

  approve(docId: number) {
    this.marketService.verifyDocument(docId, 'VERIFIED').subscribe({
      next: () => {
        alert('Verified!');
        this.selectedListing = null;
        this.refreshQueue();
      }
    });
  }
}