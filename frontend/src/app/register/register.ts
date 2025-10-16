import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  username: string = '';
  password: string = '';
  confirmPassword: string = '';
  email: string = '';
  fullName: string = '';
  voterId: string = '';
  agreeToTerms: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  isFormValid(): boolean {
    return !!(this.username && this.password && this.confirmPassword &&
              this.email && this.fullName && this.voterId && this.agreeToTerms &&
              this.password === this.confirmPassword);
  }

  onRegister() {
    if (!this.isFormValid()) {
      this.errorMessage = 'Please fill all fields and ensure passwords match.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register({
      username: this.username,
      password: this.password,
      email: this.email,
      fullName: this.fullName,
      voterId: this.voterId
    }).subscribe({
      next: (response: any) => {
        this.successMessage = 'Registration successful! Please login.';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (error: any) => {
        this.errorMessage = 'Registration failed. Please try again.';
        this.loading = false;
      }
    });
  }
}
