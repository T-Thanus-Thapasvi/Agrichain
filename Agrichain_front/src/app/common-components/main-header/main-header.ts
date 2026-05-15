import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { timer, Subscription, switchMap } from 'rxjs';
import { RouterLink } from '@angular/router';
import { WebNameElement } from "../../elements/web-name";
import { faL, faSignOutAlt } from '@fortawesome/free-solid-svg-icons';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { AuthService } from '../../services/auth-service';
import { faChevronDown, faBell } from '@fortawesome/free-solid-svg-icons';
import { NotificationPop } from '../../pages/notifications/notifications';
import { NotificationService } from '../../services/notification.service';

@Component({
	selector: 'app-main-header',
	imports: [RouterLink, WebNameElement, FaIconComponent, NotificationPop],
	templateUrl: './main-header.html',
	styleUrl: './main-header.css'
})
export class MainHeader implements OnInit, OnDestroy {
	faLogout = faSignOutAlt;
	authService = inject(AuthService);
	notificationService = inject(NotificationService);

	faDown = faChevronDown;
	faBell = faBell;

	showNoti = signal(false);
	newNoti = signal(false);
	private pollSubscription?: Subscription;

	user = this.authService.currentUser;

	ngOnInit() {
        this.startPolling();
    }

	startPolling() {
        this.pollSubscription = timer(0, 30000).subscribe(() => {
			this.checkUnreadStatus();
		});
    }

	checkUnreadStatus() {
        const currentUser = this.authService.currentUser();
        if (!currentUser) return;

        this.notificationService.getAllNotifications().subscribe(data => {
            const hasUnread = data.some(n => 
                n.userId === currentUser.id && 
                n.status === 'UNREAD'
            );
            this.newNoti.set(hasUnread);
        });
    }

    ngOnDestroy() {
        this.pollSubscription?.unsubscribe();
    }

	toggleNotifications() {
		if(this.showNoti()) {
			this.hideNotifications();
		} else {
			this.showNotifications();
		}
	}

	showNotifications() {
		this.showNoti.set(true);
	}
	hideNotifications() {
		this.showNoti.set(false);
	}
}
