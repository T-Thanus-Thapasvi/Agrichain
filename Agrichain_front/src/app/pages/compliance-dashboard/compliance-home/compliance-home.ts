import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ComplianceService } from '../../../services/compliance.service';
import { ComplianceDTO } from '../../../models/dto.model';
import { ComplianceType, ComplianceResult } from '../../../models/enum.model';

@Component({
  selector: 'app-compliance-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './compliance-home.html',
  styleUrls: ['./compliance-home.css']
})
export class ComplianceHomeComponent {
  private complianceService = inject(ComplianceService);

  compliances = signal<ComplianceDTO[]>([]);
  totalCount = 0;
  typeCounts: Record<ComplianceType, number> = {
    [ComplianceType.LISTING]: 0,
    [ComplianceType.TRANSACTION]: 0,
    [ComplianceType.PROGRAM]: 0
  };
  resultCounts: Record<ComplianceResult, number> = {
    [ComplianceResult.PASSED]: 0,
    [ComplianceResult.FAILED]: 0,
    [ComplianceResult.PENDING]: 0,
    [ComplianceResult.REVIEW]: 0
  };

  typeLabels = [
    { value: ComplianceType.PROGRAM, label: 'Program Compliance' },
    { value: ComplianceType.LISTING, label: 'Listing Compliance' },
    { value: ComplianceType.TRANSACTION, label: 'Transaction Compliance' }
  ];

  resultLabels = [
    { value: ComplianceResult.PASSED, label: 'Passed' },
    { value: ComplianceResult.FAILED, label: 'Failed' },
    { value: ComplianceResult.PENDING, label: 'Pending' },
    { value: ComplianceResult.REVIEW, label: 'Review' }
  ];

  constructor() {
    this.loadComplianceRecords();
  }

  loadComplianceRecords(): void {
    this.complianceService.getAllCompliances().subscribe({
      next: (data) => {
        this.compliances.set(data || []);
        this.updateCounts();
      },
      error: (err) => {
        console.error('Unable to load compliance records', err);
        this.compliances.set([]);
        this.updateCounts();
      }
    });
  }

  updateCounts(): void {
    this.totalCount = this.compliances().length;
    this.typeCounts = {
      [ComplianceType.LISTING]: 0,
      [ComplianceType.TRANSACTION]: 0,
      [ComplianceType.PROGRAM]: 0
    };
    this.resultCounts = {
      [ComplianceResult.PASSED]: 0,
      [ComplianceResult.FAILED]: 0,
      [ComplianceResult.PENDING]: 0,
      [ComplianceResult.REVIEW]: 0
    };

    for(const item of this.compliances()) {
      if (item.type in this.typeCounts) {
        this.typeCounts[item.type] += 1;
      }
      if (item.result in this.resultCounts) {
        this.resultCounts[item.result] += 1;
      }
    }
  }

  getResultBadgeClass(result: ComplianceResult): string {
    switch (result) {
      case ComplianceResult.PASSED:
        return 'bg-success';
      case ComplianceResult.FAILED:
        return 'bg-danger';
      case ComplianceResult.PENDING:
        return 'bg-warning text-dark';
      case ComplianceResult.REVIEW:
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }
}

