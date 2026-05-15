import { Routes } from '@angular/router';
import { PublicLayoutComponent } from './public-layout';
import { AuthLayoutComponent } from './auth-layout';
import { WelcomePage } from './pages/welcome/welcome';
import { LoginPage } from './pages/login/login';
import { ProfilePage } from './pages/profile/profile';
// Guards
import { authGuard } from './core/guards/auth-guard';
import { guestGuard } from './core/guards/guest-guard';
import { roleGuard } from './core/guards/role-guard';
import { registrationGuard } from './core/guards/register-guard';
import { DashboardRedirectComponent } from './core/guards/dashboard-redirect';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    canActivate: [guestGuard],
    children: [
      { path: 'welcome', component: WelcomePage },
      { path: 'login', component: LoginPage },
      { path: '', pathMatch: 'full', redirectTo: '/welcome' }
    ]
  },

  // PROTECTED PAGES
  {
    path: 'dashboard',
    component: AuthLayoutComponent,
    canActivate: [authGuard],
    children: [
      // ADMIN SECTION
      {
        path: 'admin',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        children: [
          { 
            path: 'home', 
            loadComponent: () => import('./pages/admin-dashboard/admin-home/admin-home').then(m => m.AdminHome) 
          },
          { 
            path: 'reports', 
            loadComponent: () => import('./pages/admin-dashboard/admin-reports/admin-reports').then(m => m.AdminReports) 
          },
          { 
            path: 'notifications', 
            loadComponent: () => import('./pages/admin-dashboard/admin-notifications/admin-notifications').then(m => m.AdminNotifications) 
          },
          { path: '', pathMatch: 'full', redirectTo: 'home' }
        ]
      },

      // FARMER SECTION
      {
        path: 'farmer',
        canActivate: [roleGuard],
        data: { roles: ['FARMER'] },
        children: [
          { 
            path: 'register', 
            loadComponent: () => import('./pages/register/register').then(m => m.RegisterComponent) 
          },
          {
            path: 'home',
            canActivate: [registrationGuard],
            loadComponent: () => import('./pages/farmer-dashboard/farmer-home/farmer-dashboard').then(m => m.FarmerDashboardPage)
          },
          {
            path: 'listings',
            canActivate: [registrationGuard],
            loadComponent: () => import('./pages/farmer-dashboard/farmer-listings/farmer-listings').then(m => m.FarmerListings)
          },
          {
            path: 'subsidies',
            canActivate: [registrationGuard],
            loadComponent: () => import('./pages/farmer-dashboard/farmer-subsidy/farmer-subsidy').then(m => m.FarmerSubsidy)
          },
          { path: '', pathMatch: 'full', redirectTo: 'home' }
        ]
      },

      // TRADER SECTION
      {
        path: 'trader',
        canActivate: [roleGuard],
        data: { roles: ['TRADER'] },
        children: [
          { 
            path: 'home', 
            loadComponent: () => import('./pages/trader-dashboard/trader-home/trader').then(m => m.TraderPage) 
          },
          { 
            path: 'croplistings', 
            loadComponent: () => import('./pages/trader-dashboard/trader-croplistings/trader-croplistings').then(m => m.TraderCroplistings) 
          },
          { path: '', pathMatch: 'full', redirectTo: 'home' }
        ]
      },

      // OFFICER SECTION
      {
        path: 'officer',
        canActivate: [roleGuard],
        data: { roles: ['OFFICER'] },
        children: [
          { 
            path: 'home', 
            loadComponent: () => import('./pages/marketOfficer-dashboard/officer-home/officer-home').then(m => m.OfficerHome) 
          },
          { 
            path: 'history', 
            loadComponent: () => import('./pages/marketOfficer-dashboard/officer-history/officer-history').then(m => m.OfficerHistory) 
          },
          { path: '', pathMatch: 'full', redirectTo: 'home' }
        ]
      },

      // AUDITOR SECTION
      {
        path: 'auditor',
        canActivate: [roleGuard],
        data: { roles: ['AUDITOR'] },
        children: [
          { 
            path: 'home', 
            loadComponent: () => import('./pages/auditor-dashboard/auditor-home/auditor-home').then(m => m.AuditorHome) 
          },
          { 
            path: 'entry', 
            loadComponent: () => import('./pages/auditor-dashboard/audit/audit').then(m => m.AuditComponent) 
          },
          { path: '', pathMatch: 'full', redirectTo: 'home' }
        ]
      },

      // COMPLIANCE SECTION
      {
        path: 'compliance',
        canActivate: [roleGuard],
        data: { roles: ['COMPLIANCE'] },
        children: [
          { 
            path: 'home', 
            loadComponent: () => import('./pages/compliance-dashboard/compliance-home/compliance-home').then(m => m.ComplianceHomeComponent) 
          },
          { 
            path: 'entry', 
            loadComponent: () => import('./pages/compliance-dashboard/compliance/compliance').then(m => m.ComplianceComponent) 
          },
          { path: '', pathMatch: 'full', redirectTo: 'home' }
        ]
      },

      // MANAGER SECTION
      {
        path: 'manager',
        canActivate: [roleGuard],
        data: { roles: ['MANAGER'] },
        children: [
          { 
            path: 'home', 
            loadComponent: () => import('./pages/manager-dashboard/manager-dashboard').then(m => m.ManagerDashboard) 
          },
          { path: '', pathMatch: 'full', redirectTo: 'home' }
        ]
      },

      { path: 'profile', component: ProfilePage },
      { path: '', pathMatch: 'full', component: DashboardRedirectComponent }
    ]
  },

  // FALLBACK
  { path: '**', redirectTo: '/welcome' }
];