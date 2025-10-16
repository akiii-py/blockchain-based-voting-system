/**
 * Application Routes Configuration
 *
 * Defines the routing structure for the Angular application.
 * Uses lazy loading for components to improve performance and
 * implements route guards for authentication and authorization.
 *
 * Route Structure:
 * - Public routes: login, register (no guards)
 * - Protected routes: dashboard, vote (require authentication)
 * - Admin routes: admin (require admin role)
 * - Default redirect: login page
 * - Wildcard route: redirects to login
 */
import { Routes } from '@angular/router';
import { authGuard } from './auth-guard';
import { adminGuard } from './admin-guard';

export const routes: Routes = [
  // Default route redirects to login
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Public authentication routes
  { path: 'login', loadComponent: () => import('./login/login').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./register/register').then(m => m.RegisterComponent) },

  // Protected user routes (require authentication)
  { path: 'dashboard', loadComponent: () => import('./dashboard/dashboard').then(m => m.DashboardComponent), canActivate: [authGuard] },
  { path: 'vote', loadComponent: () => import('./vote/vote').then(m => m.VoteComponent), canActivate: [authGuard] },

  // Admin-only routes (require admin role)
  { path: 'admin', loadComponent: () => import('./admin/admin').then(m => m.AdminComponent), canActivate: [adminGuard] },

  // Wildcard route for unknown paths
  { path: '**', redirectTo: '/login' }
];
