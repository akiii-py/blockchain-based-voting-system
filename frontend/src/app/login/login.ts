/**
 * Login Component - Handles user authentication
 *
 * This component manages the login form and authentication process.
 * It communicates with the AuthService to authenticate users and
 * stores JWT tokens in localStorage upon successful login.
 *
 * Features:
 * - User authentication via username/password
 * - JWT token storage for session management
 * - Role-based navigation (USER/ADMIN)
 * - Error handling for failed login attempts
 * - Automatic redirect to dashboard after login
 */
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Handles the login form submission
   * Authenticates user credentials and manages session
   */
  onLogin() {
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (response: any) => {
        // Store authentication token and user role
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.user.role);

        // Navigate to dashboard after successful login
        this.router.navigate(['/dashboard']);
      },
      error: (error: any) => {
        // Display error message for invalid credentials
        this.errorMessage = 'Invalid credentials';
      }
    });
  }
}
