import { Component, signal, inject, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SubsidyService } from '../../services/subsidy.service';
import { SubsidyProgram, SubsidyProgramCreateRequest, SubsidyProgramUpdateRequest } from '../../models/subsidy.model';
import { ToastService } from '../../services/toast-service';

interface StatCard {
  label: string;
  value: string | number;
  icon: string;
  bg: string;
  color: string;
}

@Component({
  selector: 'manager-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manager-dashboard.html',
  styleUrl: './manager-dashboard.css'
})
export class ManagerDashboard implements OnInit {
  private subsidyService = inject(SubsidyService);
  private toast = inject(ToastService);

  // Data signals
  readonly programs = signal<SubsidyProgram[]>([]);
  readonly isLoading = signal(false);
  readonly showModal = signal(false);
  readonly isEditMode = signal(false);
  readonly showDeleteConfirm = signal(false);
  readonly selectedProgram = signal<SubsidyProgram | null>(null);
  readonly deleteTarget = signal<number | null>(null);

  // Form signals
  readonly formData = signal({
    title: '',
    description: '',
    allottedBudget: 0,
    startDate: '',
    endDate: '',
    subsidyStatus: 'PENDING' as 'PENDING' | 'VALIDATED' | 'REJECTED'
  });

  // Computed statistics
  readonly totalBudget = computed(() => {
    return this.programs().reduce((sum, prog) => sum + prog.allottedBudget, 0);
  });

  readonly totalConsumed = computed(() => {
    return this.programs().reduce((sum, prog) => sum + (prog.consumedBudget ?? 0), 0);
  });

  readonly activePrograms = computed(() => {
    return this.programs().filter(prog => prog.subsidyStatus === 'PENDING').length;
  });

  readonly closedPrograms = computed(() => {
    return this.programs().filter(prog => prog.subsidyStatus === 'VALIDATED' || prog.subsidyStatus === 'REJECTED').length;
  });

  readonly statCards = computed<StatCard[]>(() => [
    {
      label: 'Total Budget',
      value: `₹${this.formatCurrency(this.totalBudget())}`,
      icon: '💰',
      bg: 'rgba(46, 173, 50, 0.1)',
      color: '#2EAD32'
    },
    {
      label: 'Consumed Budget',
      value: `₹${this.formatCurrency(this.totalConsumed())}`,
      icon: '📊',
      bg: 'rgba(245, 158, 11, 0.1)',
      color: '#F59E0B'
    },
    {
      label: 'Active Programs',
      value: this.activePrograms(),
      icon: '✅',
      bg: 'rgba(59, 130, 246, 0.1)',
      color: '#3B82F6'
    },
    {
      label: 'Closed Programs',
      value: this.closedPrograms(),
      icon: '🔒',
      bg: 'rgba(107, 114, 128, 0.1)',
      color: '#6B7280'
    }
  ]);

  ngOnInit(): void {
    this.loadPrograms();
  }

  loadPrograms(): void {
    this.isLoading.set(true);
    this.subsidyService.getAllPrograms().subscribe({
      next: (data: SubsidyProgram[]) => {
        this.programs.set(data);
        this.isLoading.set(false);
      },
      error: (error: any) => {
        console.error('Error loading programs:', error);
        this.toast.show('Failed to load subsidy programs', 'alert');
        this.isLoading.set(false);
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode.set(false);
    this.selectedProgram.set(null);
    this.resetForm();
    this.showModal.set(true);
  }

  openEditModal(program: SubsidyProgram): void {
    this.isEditMode.set(true);
    this.selectedProgram.set(program);
    this.formData.set({
      title: program.title,
      description: program.description,
      allottedBudget: program.allottedBudget,
      startDate: program.startDate,
      endDate: program.endDate,
      subsidyStatus: program.subsidyStatus
    });
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.resetForm();
  }

  resetForm(): void {
    this.formData.set({
      title: '',
      description: '',
      allottedBudget: 0,
      startDate: '',
      endDate: '',
      subsidyStatus: 'PENDING'
    });
  }

  submitForm(): void {
    const formValue = this.formData();

    // Validation
    if (!formValue.title.trim()) {
      this.toast.show('Title is required', 'alert');
      return;
    }
    if (!formValue.description.trim()) {
      this.toast.show('Description is required', 'alert');
      return;
    }
    if (formValue.allottedBudget <= 0) {
      this.toast.show('Budget must be greater than 0', 'alert');
      return;
    }
    if (!formValue.startDate) {
      this.toast.show('Start date is required', 'alert');
      return;
    }
    if (!formValue.endDate) {
      this.toast.show('End date is required', 'alert');
      return;
    }
    if (new Date(formValue.endDate) <= new Date(formValue.startDate)) {
      this.toast.show('End date must be after start date', 'alert');
      return;
    }

    if (this.isEditMode()) {
      this.updateProgram();
    } else {
      this.createProgram();
    }
  }

  createProgram(): void {
    const formValue = this.formData();
    const request: SubsidyProgramCreateRequest = {
      title: formValue.title,
      description: formValue.description,
      allottedBudget: formValue.allottedBudget,
      startDate: formValue.startDate,
      endDate: formValue.endDate,
      subsidyStatus: formValue.subsidyStatus
    };

    this.subsidyService.createProgram(request).subscribe({
      next: () => {
        this.toast.show('Subsidy program created successfully', 'success');
        this.closeModal();
        this.loadPrograms();
      },
      error: (error: any) => {
        console.error('Error creating program:', error);
        this.toast.show(error?.error?.message || 'Failed to create subsidy program', 'alert');
      }
    });
  }

  updateProgram(): void {
    const selected = this.selectedProgram();
    if (!selected) return;

    const formValue = this.formData();
    const request: SubsidyProgramUpdateRequest = {
      programID: selected.programID,
      title: formValue.title,
      description: formValue.description,
      allottedBudget: formValue.allottedBudget,
      startDate: formValue.startDate,
      endDate: formValue.endDate,
      subsidyStatus: formValue.subsidyStatus
    };

    this.subsidyService.updateProgram(request).subscribe({
      next: () => {
        this.toast.show('Subsidy program updated successfully', 'success');
        this.closeModal();
        this.loadPrograms();
      },
      error: (error: any) => {
        console.error('Error updating program:', error);
        this.toast.show(error?.error?.message || 'Failed to update subsidy program', 'alert');
      }
    });
  }

  openDeleteConfirm(program: SubsidyProgram): void {
    this.deleteTarget.set(program.programID);
    this.showDeleteConfirm.set(true);
  }

  closeDeleteConfirm(): void {
    this.showDeleteConfirm.set(false);
    this.deleteTarget.set(null);
  }

  confirmDelete(): void {
    const target = this.deleteTarget();
    if (target === null) return;

    this.subsidyService.deleteProgram(target).subscribe({
      next: () => {
        this.toast.show('Subsidy program deleted successfully', 'success');
        this.closeDeleteConfirm();
        this.loadPrograms();
      },
      error: (error: any) => {
        console.error('Error deleting program:', error);
        this.toast.show(error?.error?.message || 'Failed to delete subsidy program', 'alert');
      }
    });
  }

  getStatusDisplay(status: string): { label: string; color: string } {
    switch (status) {
      case 'PENDING':
        return { label: 'Active', color: 'success' };
      case 'VALIDATED':
        return { label: 'Closed – Budget Full', color: 'neutral' };
      case 'REJECTED':
        return { label: 'Programme Cancelled', color: 'danger' };
      default:
        return { label: status, color: 'neutral' };
    }
  }

  getBudgetProgress(program: SubsidyProgram): { percentage: number; color: string; label: string } {
    const consumed = program.consumedBudget;
    const percentage = program.allottedBudget > 0
      ? Math.round((consumed / program.allottedBudget) * 100)
      : 0;

    let color = 'success';
    let label = '';

    if (program.subsidyStatus === 'VALIDATED') {
      color = 'neutral';
      label = 'Budget Exhausted';
    } else if (percentage >= 75) {
      color = 'warning';
    } else {
      color = 'success';
    }

    return { percentage, color, label };
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-IN', {
      maximumFractionDigits: 0
    }).format(value);
  }

  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    }).format(date);
  }

  getStatusDisplayLabel(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'Active';
      case 'VALIDATED':
        return 'Closed – Budget Full';
      case 'REJECTED':
        return 'Programme Cancelled';
      default:
        return status;
    }
  }

  updateFormField(field: string, value: any): void {
    const current = this.formData();
    this.formData.set({
      ...current,
      [field]: value
    });
  }
}
