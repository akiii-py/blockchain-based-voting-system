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
  email: string = '';
  fullName: string = '';
  voterId: string = '';
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onRegister() {
    this.authService.register({
      username: this.username,
      password: this.password,
      email: this.email,
      fullName: this.fullName,
      voterId: this.voterId
    }).subscribe({
      next: (response: any) => {
        this.successMessage = 'Registration successful! Please login.';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (error: any) => {
        this.errorMessage = 'Registration failed. Please try again.';
      }
    });
  }
}
