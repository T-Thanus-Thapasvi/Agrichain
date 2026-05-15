
import { Component, inject, OnInit, ChangeDetectorRef, signal } from '@angular/core';
import { MarketService } from '../../../services/market';
import { CropListingDTO } from '../../../models/dto.model';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../services/toast-service';
import { faCheck, faClose, faEye, faTimes } from '@fortawesome/free-solid-svg-icons';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";
import { NotificationServiceFarmer } from '../../../services/notification';
import { forkJoin } from 'rxjs';

@Component({
	selector: 'app-officer-home',
	standalone: true,
	imports: [CommonModule, FaIconComponent],
	templateUrl: './officer-home.html',
	styleUrl: './officer-home.css'
})
export class OfficerHome implements OnInit {
	activeModule: 'dashboard' | 'crops' | 'documents' | 'subsidies' | 'farmers' = 'dashboard';

	faCheck = faCheck;
	faClose = faClose;
	faEye = faEye;
	faTimes = faTimes;

	pendingListings: CropListingDTO[] = [];
	pendingDocs: any[] = [];
	pendingSubsidies: any[] = [];

	allFarmers: any[] = [];
	listingsWithDocs: any[] = [];
	pendingFarmers: any[] = [];

	// Document popup
	showDocPopup = signal(false);
	selectedFarmerDocs: any[] = [];
	selectedFarmer: any = null;

	// Statistics Object
	stats = {
		pendingCrops: 0,
		awaitingDocs: 0,
		subsidyApps: 0,
		totalApprovedToday: 0,
		totalFarmersCount: 0
	};

	private toast = inject(ToastService);

	constructor(private marketService: MarketService, private notifService: NotificationServiceFarmer) { }

	ngOnInit(): void {
		console.log("🚀 Officer Portal Initialized!");
		this.refreshAllData();
	}

	refreshAllData() {
		console.log("📡 Syncing all backend data...");

		// 1. Fetch Crop Listings
		this.marketService.getListingsByStatus('PENDING').subscribe({
			next: (data) => {
				console.log("✅ Crop Listings Fetched:", data);
				this.pendingListings = data;
				this.stats.pendingCrops = data.length;
			},
			error: (err) => {
				// Changed 'error' to 'alert' to match your ToastService allowed types
				this.toast.show('Failed to load crop listings', 'alert');
			}
		});



		this.marketService.getAllDocuments().subscribe({
			next: (documents: any[]) => {
				console.log("🔍 FULL DOCUMENT DATA:", documents);
				console.log("📋 Document statuses:", documents.map(d => ({ docType: d.docType, verificationStatus: d.verificationStatus })));
				this.listingsWithDocs = documents;

				const pendingDocs = documents.filter(doc => {
					const status = doc.verificationStatus?.toUpperCase();
					return status !== 'VERIFIED' && status !== 'REJECTED';
				});

				const farmerIds = [...new Set(pendingDocs.map(doc => doc.farmerId).filter(id => id))];

				if (farmerIds.length === 0) {
					console.warn("⚠️ No farmers found with pending documents");
					this.pendingFarmers = [];
					this.stats.awaitingDocs = 0;
					return;
				}

				// Fetch farmer details for each unique farmer ID
				console.log("Fetching farmer details for IDs:", farmerIds);
				const farmerRequests = farmerIds.map(id => this.marketService.getFarmerById(id));

				forkJoin(farmerRequests).subscribe({
					next: (farmers: any[]) => {
						console.log("Fetched farmer details:", farmers);
						const farmerMap = new Map<number, any>();
						farmers.forEach(farmer => {
							console.log(`Processing farmer ${farmer.farmerId}:`, farmer);
							farmerMap.set(farmer.farmerId, {
								farmerId: farmer.farmerId,
								farmerName: `${farmer.name || 'Unknown'} ${farmer.lastName || ''}`.trim(),
								farmer: farmer,
								documents: []
							});
						});

						// Group documents by farmer ID
						pendingDocs.forEach(doc => {
							const farmerId = doc.farmerId;
							console.log(`🗂️ Assigning doc ${doc.id} (type: ${doc.docType}) to farmer ${farmerId}`);
							if (farmerMap.has(farmerId)) {
								farmerMap.get(farmerId).documents.push(doc);
							} else {
								console.warn(`⚠️ Farmer ${farmerId} not found in farmer map`);
							}
						});

						this.pendingFarmers = Array.from(farmerMap.values());
						this.stats.awaitingDocs = this.pendingFarmers.length;
						console.log("✅ Final farmers with pending docs:", this.pendingFarmers);
					},
					error: (err) => {
						console.error("❌ Failed to fetch farmer details:", err);
						this.toast.show('Failed to load farmer details', 'alert');
					}
				});
			},
			error: (err) => {
				console.error("❌ Document Fetch Failed:", err);
				this.toast.show('Failed to load documents', 'alert');
			}
		});

		// 3. Fetch Farmers
		this.marketService.getAllFarmers().subscribe({
			next: (data) => {
				this.allFarmers = data; // Now matches the HTML *ngFor="let farmer of allFarmers"
				this.stats.totalFarmersCount = data.length;
			},
			error: (err) => console.error("❌ Farmer Fetch Failed:", err)
		});

		// 4. Fetch Subsidies
		this.marketService.getSubsidies().subscribe({
			next: (data) => {
				this.pendingSubsidies = data.filter(sub => sub.disbursementStatus === 'PENDING');
				this.stats.subsidyApps = this.pendingSubsidies.length;
			},
			error: (err) => console.error("❌ Subsidy Fetch Failed:", err)
		});
	}

	setModule(moduleName: 'dashboard' | 'crops' | 'documents' | 'subsidies' | 'farmers') {
		this.activeModule = moduleName;
	}

	approveCrop(id: number) {
		const reason = prompt("Enter approval remarks:", "Quality standards verified") || "Verified by Officer";

		console.log("📡 Attempting to approve Crop ID:", id);

		this.marketService.approveCrop(id, 'VALIDATED', reason).subscribe({
			next: (response) => {
				console.log('✅ Crop Approved Successfully:', response);
				this.toast.show('Crop listing approved!', 'success');

				this.pendingListings = this.pendingListings.filter(item => item.listingId !== id);
				this.stats.pendingCrops = this.pendingListings.length;
			},
			error: (err) => {
				console.error('❌ Approval failed', err);
				this.toast.show('Failed to approve crop', 'alert');
			}
		});
	}



	rejectCrop(id: number) {
		const reason = prompt("Enter rejection reason:", "Quality standards not met");

		if (!reason) return;

		this.marketService.approveCrop(id, 'REJECTED', reason).subscribe({
			next: (response) => {
				this.toast.show('Crop listing rejected', 'alert');
				this.pendingListings = this.pendingListings.filter(item => item.listingId !== id);
				this.stats.pendingCrops = this.pendingListings.length;
			},
			error: (err) => {
				console.error('❌ Rejection failed', err);
			}
		});
	}

	approveDoc(docId: number) {
		if (!docId) {
			console.error("❌ documentId is missing! Still receiving null/undefined.");
			return;
		}

		this.marketService.verifyDocument(docId, 'VERIFIED').subscribe({
			next: () => {
				this.toast.show('Document verified successfully!', 'success');

				this.listingsWithDocs = this.listingsWithDocs.filter(d => d.id !== docId);
				// Update pendingFarmers
				for (let farmer of this.pendingFarmers) {
					farmer.documents = farmer.documents.filter((doc: any) => doc.id !== docId);
				}
				this.pendingFarmers = this.pendingFarmers.filter(farmer => farmer.documents.length > 0);
				this.stats.awaitingDocs = this.pendingFarmers.length;
			},
			error: (err) => {
				console.error("Verification failed", err);
				this.toast.show('Backend error: Check logs', 'alert');
			}
		});
	}

	rejectDoc(docId: number) {
		const reason = prompt("Please enter a reason for rejection:");
		if (!reason || !docId) return;

		this.marketService.verifyDocument(docId, 'REJECTED').subscribe({
			next: () => {
				this.toast.show('Document has been rejected', 'info');
				this.listingsWithDocs = this.listingsWithDocs.filter(d => d.id !== docId);
				// Update pendingFarmers
				for (let farmer of this.pendingFarmers) {
					farmer.documents = farmer.documents.filter((doc: any) => doc.id !== docId);
				}
				this.pendingFarmers = this.pendingFarmers.filter(farmer => farmer.documents.length > 0);
				this.stats.awaitingDocs = this.pendingFarmers.length;
			},
			error: (err) => console.error("Rejection failed", err)
		});
	}

	approveFarmer(farmerId: number) {
		const farmer = this.pendingFarmers.find(f => f.farmerId === farmerId);
		if (!farmer) return;

		console.log('farmer', farmer)
		const approvePromises = farmer.documents.map((doc: any) => {
			console.log('doc', doc)
			return this.marketService.verifyDocument(doc.documentId, 'VERIFIED').toPromise();
		}
		);

		Promise.all(approvePromises).then(() => {
			this.toast.show('Farmer documents verified successfully!', 'success');
			this.pendingFarmers = this.pendingFarmers.filter(f => f.farmerId !== farmerId);
			this.stats.awaitingDocs = this.pendingFarmers.length;
		}).catch(err => {
			console.error('Approval failed', err);
			this.toast.show('Failed to approve farmer documents', 'alert');
		});
	}

	rejectFarmer(farmerId: number) {
		const reason = prompt("Please enter a reason for rejection:");
		if (!reason) return;

		const farmer = this.pendingFarmers.find(f => f.farmerId === farmerId);
		if (!farmer) return;

		// Reject all documents for this farmer
		const rejectPromises = farmer.documents.map((doc: any) =>
			this.marketService.verifyDocument(doc.documentId, 'REJECTED').toPromise()
		);

		Promise.all(rejectPromises).then(() => {
			this.toast.show('Farmer documents rejected', 'info');
			this.pendingFarmers = this.pendingFarmers.filter(f => f.farmerId !== farmerId);
			this.stats.awaitingDocs = this.pendingFarmers.length;
		}).catch(err => {
			console.error('Rejection failed', err);
			this.toast.show('Failed to reject farmer documents', 'alert');
		});
	}

	viewFarmerDocs(farmer: any) {
		this.selectedFarmer = farmer;
		this.selectedFarmerDocs = farmer.documents;
		this.showDocPopup.set(true);
	}

	closeDocPopup() {
		this.showDocPopup.set(false);
		this.selectedFarmer = null;
		this.selectedFarmerDocs = [];
	}

	getFileUrl(doc: any): string {
		return this.marketService.getDocumentFileUrl(doc);
	}

	approveSubsidy(id: number, status: string = 'COMPLETED') {
		this.marketService.reviewDisbursement(id, status).subscribe({
			next: (res) => {
				this.toast.show(`Subsidy Approved successfully!`, 'success');
				this.refreshAllData();
			},
			error: (err) => {
				console.error('Disbursement failed', err);
				this.toast.show('Failed to process subsidy', 'alert');
			}
		});
	}
}