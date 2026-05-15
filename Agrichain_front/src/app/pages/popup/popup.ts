import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PopupService } from '../../services/popup.service';
import { faExclamationTriangle, faExclamationCircle, faCircleInfo } from '@fortawesome/free-solid-svg-icons';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [CommonModule, FaIconComponent],
  templateUrl: './popup.html',
  styleUrl: './popup.css'
})
export class PopupComponent {
  popupService = inject(PopupService);
  faAlert = faExclamationTriangle;
  faWarning = faExclamationCircle;
  faInfo = faCircleInfo;
}
