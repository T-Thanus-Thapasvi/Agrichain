import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WebNameElement } from "../../elements/web-name";
import { UserRole } from '../../models/enum.model';
import { API_URL, FARMER_REGI } from '../../elements/constants';
import { ToastService } from '../../services/toast-service';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, WebNameElement],
    templateUrl: './register.html',
    styleUrl: './register.css'
})
export class RegisterComponent {
    private http = inject(HttpClient);
	private router = inject(Router);
    private toast = inject(ToastService);

    farmer: any = JSON.parse(localStorage.getItem(FARMER_REGI) || '{}');

    selectedFiles: { [key: string]: File } = {};

    onFileChange(event: any, fieldName: string) {
        const file = event.target.files[0];
        if (file) {
            this.selectedFiles[fieldName] = file;
        }
    }

    onRegisterSubmit(formValue: any) {
        const formData = new FormData();
        
        const farmerData = {
            id: null,
            name: this.farmer.name,
            email: this.farmer.email,
            contactInfo: this.farmer.contactInfo,
            dob: formValue.dob,
            gender: formValue.gender,
            address: formValue.address,
            landDetails: formValue.landDetails,
            status: 'PENDING_VERIFICATION'
        };

        console.log('Complete farmer data being sent:', farmerData);

        formData.append('farmer', new Blob([JSON.stringify(farmerData)], { type: 'application/json' }));

        // Log FormData contents before adding files
        console.log('FormData contents before adding files:');
        for (let [key, value] of formData.entries()) {
            console.log(key, value);
        }

        // Sending documents as separate named parts instead of array
        if (this.selectedFiles['idProof']) {
            formData.append('idProof', this.selectedFiles['idProof']);
        }
        if (this.selectedFiles['landRecord']) {
            formData.append('landRecord', this.selectedFiles['landRecord']);
        }

        this.http.post(`${API_URL}farmers/register/${this.farmer.userId}`, formData)
            .subscribe({
                next: (res) => {
                    this.toast.show('Registration Successful. Awaiting verification!', 'success');
                    localStorage.setItem(FARMER_REGI, JSON.stringify(res));
                    this.router.navigate(['/dashboard/farmer/home']);
                },
                error: (err) => {
                    console.error('Registration failed:', err);
                    this.toast.show('Registration failed. Please check the console for details.', 'alert');
                }
            });
    }

    onClear() {
        this.selectedFiles = {};
    }
}