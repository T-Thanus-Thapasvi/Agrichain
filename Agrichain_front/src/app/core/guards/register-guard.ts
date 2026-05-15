// core/guards/registration-guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { FARMER_REGI } from '../../elements/constants';

export const registrationGuard: CanActivateFn = (route, state) => {
    const router = inject(Router);
    const farmerData = localStorage.getItem(FARMER_REGI);

    if (farmerData) {
        const farmer = JSON.parse(farmerData);
        const isIncomplete = !farmer.address || !farmer.landDetails || !farmer.dob;

        if (isIncomplete && !state.url.includes('register')) {
            return router.createUrlTree(['/dashboard/farmer/register']);
        }
    }

    return true;
};