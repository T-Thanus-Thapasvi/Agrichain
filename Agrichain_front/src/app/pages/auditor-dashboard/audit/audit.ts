import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { AuditService } from '../../../services/audit.service';
import { AuditScope, AuditStatus } from '../../../models/enum.model';
import { AuditDTO } from '../../../models/dto.model';
import { faTrash, faEdit } from '@fortawesome/free-solid-svg-icons';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";
import { PopupService } from '../../../services/popup.service';
import { ToastService } from '../../../services/toast-service';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, FaIconComponent],
  templateUrl: './audit.html',
  styleUrls: ['./audit.css']
})
export class AuditComponent implements OnInit {
  // Using inject for cleaner dependency management
  private auditService = inject(AuditService);
  private route = inject(ActivatedRoute);
  private popupService = inject(PopupService);
  private toast = inject(ToastService);

  faTrash = faTrash;
  faEdit = faEdit;
  audits = signal<AuditDTO[]>([]);
  viewOnly = false; // Flag to hide the entry form based on navigation
  selectedAuditId: number | null = null;
  isEditing = false;

  // Initializing with Enums to fix the errors in image_956994.png
  newAudit: AuditDTO = {
    officerId: 101,
    scope: AuditScope.PROGRAM,
    findings: '',
    date: new Date().toISOString().split('T')[0],
    status: AuditStatus.OPEN
  };

  ngOnInit(): void {
    // Check URL parameters: if ?mode=view exists, we hide the entry form
    this.route.queryParams.subscribe(params => {
      this.viewOnly = params['mode'] === 'view';
    });

    this.loadAudits();
  }

  loadAudits(): void {
    this.auditService.getAllAudits().subscribe({
      next: (data) => {
        this.audits.set(data);
      },
      error: (err) => {
        console.error('Error fetching audits:', err);
      }
    });
  }

  submitAudit(): void {
    const auditToSave: AuditDTO = {
      officerId: Number(this.newAudit.officerId),
      scope: this.newAudit.scope,
      findings: this.newAudit.findings,
      date: this.newAudit.date,
      status: this.newAudit.status
    };

    if (this.isEditing && this.selectedAuditId != null) {
      auditToSave.auditId = this.selectedAuditId;
      this.auditService.updateAudit(this.selectedAuditId, auditToSave).subscribe({
        next: (response) => {
          this.toast.show('Audit updated succesfully', 'success');
          this.loadAudits();
          this.resetForm();
        },
        error: (err) => {
          console.error('Error updating audit:', err);
          this.toast.show('Could not update audit. Please try again.', 'alert');
        }
      });
      return;
    }

    this.auditService.createAudit(auditToSave).subscribe({
      next: (response) => {
        this.toast.show('Audit saved successfully!', 'success');
        this.loadAudits(); // Refresh the records table
        this.resetForm();
      },
      error: (err) => {
        console.error('Error creating audit:', err);
        // Reminder: Ensure @CrossOrigin is in your Java Controller
        this.toast.show('Could not save audit. Is the backend running?', 'alert');
      }
    });
  }

  editAudit(audit: AuditDTO): void {
    this.selectedAuditId = audit.auditId ?? null;
    this.isEditing = true;
    this.newAudit = {
      officerId: audit.officerId,
      scope: audit.scope,
      findings: audit.findings,
      date: audit.date,
      status: audit.status
    };
  }

  async deleteAudit(auditId: number | undefined): Promise<void> {
    if (auditId == null) {
      return;
    }
    const confirmed = await this.popupService.confirm({
            title: 'Delete Record?',
            message: 'Are you sure you want to delete this record? This action cannot be undone.',
            confirmText: 'Delete',
            cancelText: 'Cancel',
            type: 'danger'
          });
    if (!confirmed) {
      return;
    }
    this.auditService.deleteAudit(auditId).subscribe({
      next: () => {
        this.toast.show('Audit deleted successfully!', 'success');
        if (this.selectedAuditId === auditId) {
          this.resetForm();
        }
        this.loadAudits();
      },
      error: (err) => {
        console.error('Error deleting audit:', err);
        this.toast.show('Unable to delete audit.', 'alert');
      }
    });
  }

  resetForm(): void {
    this.selectedAuditId = null;
    this.isEditing = false;
    this.newAudit = {
      officerId: 101,
      scope: AuditScope.PROGRAM,
      findings: '',
      date: new Date().toISOString().split('T')[0],
      status: AuditStatus.OPEN
    };
  }
}