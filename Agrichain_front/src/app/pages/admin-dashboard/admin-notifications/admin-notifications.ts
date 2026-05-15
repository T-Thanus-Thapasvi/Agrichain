import { Component, OnInit, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../services/notification.service';
import { Notification, NotificationDTO } from '../../../models/dto.model';
import { NotificationCategory, UserRole } from '../../../models/enum.model';
import { Loader } from "../../../common-components/loader/loader";
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import { PopupService } from '../../../services/popup.service';
import { ToastService } from '../../../services/toast-service';

@Component({
	selector: 'admin-notifications',
	imports: [FormsModule, CommonModule, Loader, FaIconComponent],
	templateUrl: './admin-notifications.html',
	styleUrl: './admin-notifications.css'
})
export class AdminNotifications implements OnInit {
	notifications = signal<Notification[]>([]);
	loading = signal(false);
	private toast = inject(ToastService);

	faDelete = faTrash;

	// Form state
	showForm = signal(false);
	notificationForm: NotificationDTO = { subject: '', message: '', category: NotificationCategory.BROADCAST, role: '' };
	roles = Object.values(UserRole);

	private popupService = inject(PopupService);
	constructor(private notificationService: NotificationService) {}

	ngOnInit(): void {
		this.fetchNotifications();
	}

	fetchNotifications() {
		this.loading.set(true);
		this.notificationService.getAllNotifications().subscribe({
			next: (data) => {
				const broadcasts = data.filter(n => n.userId == null);
				this.notifications.set(broadcasts);
				this.loading.set(false);
			},
			error: (err) => {
				console.error('Error fetching notifications', err);
				this.loading.set(false);
			}
		});
	}

	openCreateForm() {
		this.notificationForm = { subject: '', message: '', category: NotificationCategory.BROADCAST, role: '' };
		this.showForm.set(true);
	}

	cancelForm() {
		this.showForm.set(false);
	}

	submitForm() {
		const payload: NotificationDTO = {
			...this.notificationForm,
		};
		if (!payload.role || payload.role === '') {
            delete payload.role;
        }
		this.notificationService.createNotification(payload).subscribe({
			next: () => {
				this.fetchNotifications();
				this.toast.show('Notification Boardcasted', 'success');
				this.showForm.set(false);
			},
			error: (err) => {
				this.toast.show('Error creating notification', 'alert');
				console.error('Error creating notification', err);
			}
		});
	}

	async deleteNotification(id: number) {
		const confirmed = await this.popupService.confirm({
			title: 'Delete Notification',
			message: 'Are you sure you want to delete this broadcast notification? This action cannot be undone.',
			confirmText: 'Delete',
			cancelText: 'Cancel',
			type: 'danger'
		});

		if (confirmed) {
			this.notificationService.deleteNotification(id).subscribe({
				next: () => this.fetchNotifications(),
				error: (err) => console.error('Error deleting notification', err)
			});
		}
	}
}
