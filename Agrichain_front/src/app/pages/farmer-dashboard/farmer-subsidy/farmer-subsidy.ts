import { Component, signal, inject, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SubsidyService } from '../../../services/subsidy.service';
import { AuthService } from '../../../services/auth-service';
import { ToastService } from '../../../services/toast-service';
import { SubsidyProgram } from '../../../models/subsidy.model';
import { Disbursement, DisbursementDTO } from '../../../models/dto.model';
import { JwtHelperService } from '@auth0/angular-jwt';
import { API_URL, FARMER_REGI } from '../../../elements/constants';
import { VerifyPending } from '../../../common-components/verify-pending/verify-pending';
import { Farmer } from '../../../models/user.model';
import { HttpClient } from '@angular/common/http';
import { Loader } from "../../../common-components/loader/loader";

@Component({
  selector: 'app-farmer-subsidy',
  standalone: true,
  imports: [CommonModule, FormsModule, VerifyPending, Loader],
  templateUrl: './farmer-subsidy.html',
  styleUrl: './farmer-subsidy.css',
})
export class FarmerSubsidy implements OnInit {
  private subsidyService = inject(SubsidyService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private http = inject(HttpClient);

  isApproved = signal(false);

  // Data signals
  subsidyPrograms = signal<SubsidyProgram[]>([]);
  farmerDisbursements = signal<Disbursement[]>([]);
  farmerUserId = signal<number | null>(null);
  farmerId = signal<number | null>(null);
  isLoadingPrograms = signal(false);
  isLoadingApplications = signal(false);

  // Modal control
  showApplyModal = signal(false);
  selectedProgram = signal<SubsidyProgram | null>(null);

  // Form data
  requestedAmount = signal<number | null>(null);

  ngOnInit() {
    const user = this.authService.currentUser();
    if (user && user.id) {
      const farmer: Farmer = JSON.parse(localStorage.getItem(FARMER_REGI) || '{}');
      this.farmerUserId.set(farmer?.farmerId);
      this.loadSubsidyPrograms();
      this.loadFarmerDisbursements();

      this.http.get<Farmer>(`${API_URL}farmers/get-by-userid/${user.id}`)
        .subscribe({
          next: (data) => {
            this.isApproved.set(data.status === "APPROVED");
            this.farmerId.set(data.farmerId);
          },
          error: (err) => {
            this.isApproved.set(false);
          }
        });
    } else {
      this.toastService.show('Your session has expired. Please login again.', 'alert');
    }
  }

  /**
   * Load all subsidy programs (filter PENDING ones for display)
   */
  loadSubsidyPrograms() {
    this.isLoadingPrograms.set(true);
    this.subsidyService.getAllPrograms().subscribe({
      next: (programs) => {
        // Filter to show only PENDING (active/open) programs
        const filteredPrograms = programs.filter(p => p.subsidyStatus === 'PENDING');
        this.subsidyPrograms.set(filteredPrograms);
        this.isLoadingPrograms.set(false);
      },
      error: (err) => {
        console.error('Failed to load subsidy programs:', err);
        this.toastService.show('Failed to load subsidy programs. Please try again.', 'alert');
        this.isLoadingPrograms.set(false);
      }
    });
  }

  /**
   * Load farmer's own disbursements/applications
   */
  loadFarmerDisbursements() {
    const farmerId = this.farmerUserId();
    if (!farmerId) return;

    this.isLoadingApplications.set(true);
    this.subsidyService.getFarmerDisbursements(farmerId).subscribe({
      next: (disbursements) => {
        this.farmerDisbursements.set(disbursements);
        this.isLoadingApplications.set(false);
      },
      error: (err) => {
        console.error('Failed to load applications:', err);
        // Don't show error if it's 404 (no applications yet)
        if (err.status !== 404) {
          this.toastService.show('Failed to load your applications.', 'alert');
        }
        this.isLoadingApplications.set(false);
      }
    });
  }

  /**
   * Check if farmer has already applied for a specific program
   */
  hasAlreadyApplied(programId: number): boolean {
    return this.farmerDisbursements().some(
      d => d.subsidyProgram?.programID === programId
    );
  }

  /**
   * Get the button state for a program
   * Returns: 'apply' | 'already-applied' | 'closed' | 'rejected'
   */
  getButtonState(program: SubsidyProgram): string {
    if (this.hasAlreadyApplied(program.programID)) {
      return 'already-applied';
    }

    if (program.subsidyStatus === 'VALIDATED') {
      return 'closed';
    }

    if (program.subsidyStatus === 'REJECTED') {
      return 'rejected';
    }

    return 'apply';
  }

  /**
   * Open the apply modal for a program
   */
  openApplyModal(program: SubsidyProgram) {
    this.selectedProgram.set(program);
    this.requestedAmount.set(null);
    this.showApplyModal.set(true);
  }

  /**
   * Close the apply modal
   */
  closeApplyModal() {
    this.showApplyModal.set(false);
    this.selectedProgram.set(null);
    this.requestedAmount.set(null);
  }

  /**
   * Submit subsidy application
   */
  submitSubsidyApplication() {
    const program = this.selectedProgram();
    const farmerId = this.farmerId();
    const amount = this.requestedAmount();

    console.log('Submitting application with:', { program, farmerId, amount });

    if (!program || !farmerId || !amount) {
      this.toastService.show('Please fill in all required fields.', 'alert');
      return;
    }

    if (amount <= 0) {
      this.toastService.show('Please enter a valid amount.', 'alert');
      return;
    }

    const disbursementPayload: DisbursementDTO = {
      farmerId: farmerId,
      programId: program.programID,
      disbursementAmount: amount
    };

    this.subsidyService.applyForSubsidy(disbursementPayload).subscribe({
      next: (response) => {
        this.toastService.show('Successfully applied for subsidy!', 'success');
        this.closeApplyModal();
        // Refresh the applications list
        this.loadFarmerDisbursements();
      },
      error: (err) => {
        console.error('Subsidy application failed:', err);
        const errorMsg = err.error?.message || 'Something went wrong.';

        if (errorMsg.toLowerCase().includes('already applied')) {
          this.toastService.show('You have already applied for this program.', 'alert');
        } else if (errorMsg.toLowerCase().includes('budget')) {
          this.toastService.show('This program is closed — budget has been fully used.', 'alert');
        } else {
          this.toastService.show(errorMsg, 'alert');
        }
      }
    });
  }

  /**
   * Format date from string (YYYY-MM-DD) to (DD MMM YYYY)
   */
  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    const day = date.getDate().toString().padStart(2, '0');
    const month = date.toLocaleString('en-US', { month: 'short' });
    const year = date.getFullYear();
    return `${day} ${month} ${year}`;
  }

  /**
   * Format amount with rupee symbol and comma separation
   */
  formatCurrency(amount: number): string {
    return '₹' + amount.toLocaleString('en-IN');
  }

  /**
   * Get the display status for a disbursement application
   */
  getApplicationStatusDisplay(status: string | undefined): string {
    if (!status) return 'Pending';
    
    switch (status.toUpperCase()) {
      case 'PENDING':
        return 'Under Review';
      case 'IN_PROGRESS':
        return 'In Progress';
      case 'COMPLETED':
        return 'Approved';
      case 'REJECTED':
        return 'Rejected';
      default:
        return status;
    }
  }

  /**
   * Get CSS class for status badge
   */
  getStatusBadgeClass(status: string | undefined): string {
    if (!status) return 'status-pending';
    const statusLower = status.toLowerCase();
    if (statusLower === 'pending') return 'status-pending';
    if (statusLower === 'in_progress') return 'status-in-progress';
    if (statusLower === 'completed') return 'status-completed';
    if (statusLower === 'rejected') return 'status-rejected';

    return 'status-pending';
  }
}

