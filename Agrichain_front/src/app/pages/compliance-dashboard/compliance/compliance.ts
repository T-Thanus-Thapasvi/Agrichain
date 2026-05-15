import { Component, OnInit, Signal, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router'; // For navigation
import { ComplianceService } from '../../../services/compliance.service';
import { ComplianceDTO } from '../../../models/dto.model'; // Updated import path
import { ComplianceResult,ComplianceType } from '../../../models/enum.model'; // Updated import path
import { faTrash, faEdit, faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";

@Component({
  selector: 'app-compliance',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, FaIconComponent],
  templateUrl: './compliance.html',
  styleUrl: './compliance.css'
})
export class ComplianceComponent implements OnInit {
  // Using inject for consistency with your Audit component
  private complianceService = inject(ComplianceService);
  private router = inject(Router);

  // Icons
  faTrash = faTrash;
  faEdit = faEdit;
  faCheck = faCheckCircle;

  compliances = signal<ComplianceDTO[]>([]);
  isEditing = false;
  selectedId: number | null = null;

  newCompliance: ComplianceDTO = {
    auditId: 0,
    entityId: 0,
    type: ComplianceType.PROGRAM,
    result: ComplianceResult.PENDING,
    notes: '',
    date: new Date().toISOString().split('T')[0]
  };

  ngOnInit(): void {
    this.loadCompliances();
  }

  loadCompliances(): void {
    this.complianceService.getAllCompliances().subscribe({
      next: (data) => this.compliances.set(data),
      error: (err) => console.error('Error fetching data', err)
    });
  }

  submitCompliance(): void {
    if (this.isEditing && this.selectedId != null) {
      // Logic for Update
      this.complianceService.updateCompliance(this.selectedId, this.newCompliance).subscribe({
        next: () => {
          alert('Compliance Updated!');
          this.onSuccess();
        },
        error: (err) => console.error('Update failed', err)
      });
    } else {
      // Logic for Create
      this.complianceService.addComplianceToAudit(this.newCompliance.auditId, this.newCompliance).subscribe({
        next: (res) => {
          alert('Compliance Saved Successfully!');
          this.onSuccess();
        },
        error: (err) => alert('Failed to save record. Ensure Audit ID is valid.')
      });
    }
  }

  // Helper to refresh data and navigate
  private onSuccess(): void {
    this.loadCompliances();
    this.resetForm();
    // Navigate to the Dashboard Home to see the new results
    this.router.navigate(['/dashboard/compliance/home']);
  }

  editCompliance(record: ComplianceDTO): void {
    this.isEditing = true;
    this.selectedId = record.complianceId ?? null;
    this.newCompliance = { ...record }; // Fill form with existing data
  }

  deleteCompliance(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this compliance record?')) {
      this.complianceService.deleteCompliance(id).subscribe({
        next: () => this.loadCompliances(),
        error: (err) => alert('Error deleting record.')
      });
    }
  }

  resetForm(): void {
    this.isEditing = false;
    this.selectedId = null;
    this.newCompliance = {
      auditId: 0,
      entityId: 0,
      type: ComplianceType.PROGRAM,
      result: ComplianceResult.PENDING,
      notes: '',
      date: new Date().toISOString().split('T')[0]
    };
  }
}