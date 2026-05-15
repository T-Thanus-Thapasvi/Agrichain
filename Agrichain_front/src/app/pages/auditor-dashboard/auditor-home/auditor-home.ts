import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuditService } from '../../../services/audit.service';
import { AuditDTO } from '../../../models/dto.model';
import { AuditStatus } from '../../../models/enum.model';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";


@Component({
  selector: 'app-auditor-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './auditor-home.html',
  styleUrl: './auditor-home.css'
})
export class AuditorHome implements OnInit {
  private auditService = inject(AuditService);

  // CRITICAL: This was missing! The HTML table needs this.
  allAudits = signal<AuditDTO[]>([]);

  stats = {
    total: 0,
    completed: 0,
    inProgress: 0,
    underReview: 0,
    lastAuditDate: 'N/A'
  };

  ngOnInit(): void {
    this.calculateStatsFromAllAudits();
  }

  calculateStatsFromAllAudits() {
    this.auditService.getAllAudits().subscribe({
      next: (audits: AuditDTO[]) => {
        // 1. Store the full list for the table
        this.allAudits.set(audits);

        if (audits && audits.length > 0) {
          this.stats.total = audits.length;
          
          // 2. Filter using 'a.status' (to match your AuditDTO interface)
          this.stats.completed = audits.filter(a => 
            a.status === AuditStatus.CLOSED
          ).length;

          this.stats.inProgress = audits.filter(a => 
            a.status === AuditStatus.OPEN || 
            a.status === AuditStatus.IN_PROGRESS
          ).length;

          this.stats.underReview = audits.filter(a => 
            a.status === AuditStatus.REVIEW
          ).length;

          const lastAudit = audits[audits.length - 1];
          this.stats.lastAuditDate = lastAudit.date || 'N/A';
        }
      },
      error: (err) => {
        console.error('Failed to calculate dashboard stats', err);
      }
    });
  }
}