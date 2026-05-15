import { Injectable, signal } from '@angular/core';

export interface PopupConfig {
	title: string;
	message: string;
	confirmText?: string;
	cancelText?: string;
	type?: 'danger' | 'warning' | 'info';
}

@Injectable({ providedIn: 'root' })
export class PopupService {
	readonly isOpen = signal(false);
	readonly config = signal<PopupConfig>({
		title: '',
		message: '',
		confirmText: 'Confirm',
		cancelText: 'Cancel',
		type: 'danger'
	});

	private resolveCallback: ((result: boolean) => void) | null = null;

	confirm(popupConfig: PopupConfig): Promise<boolean> {
		this.config.set({
			confirmText: 'Confirm',
			cancelText: 'Cancel',
			type: 'danger',
			...popupConfig
		});
		this.isOpen.set(true);

		return new Promise<boolean>((resolve) => {
			this.resolveCallback = resolve;
		});
	}

	onConfirm() {
		this.isOpen.set(false);
		this.resolveCallback?.(true);
		this.resolveCallback = null;
	}

	onCancel() {
		this.isOpen.set(false);
		this.resolveCallback?.(false);
		this.resolveCallback = null;
	}
}
