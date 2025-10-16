/**
 * Authentication Service - Handles user authentication and session management
 *
 * This service manages all authentication-related operations including login,
 * registration, and session state. It communicates with the backend authentication
 * API endpoints and manages JWT tokens for secure API communication.
 *
 * Features:
 * - User login and registration
 * - JWT token management
 * - Session state tracking
 * - Type-safe request/response interfaces
 * - HTTP client integration for API calls
 */
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Interface for login request payload
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * Interface for login response from backend
 */
export interface LoginResponse {
  token: string;
  user: {
    id: number;
    username: string;
    role: string;
    email: string;
    fullName: string;
    voterId: string;
  };
}

/**
 * Interface for user registration request payload
 */
export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
  voterId: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  /**
   * Authenticates a user with username and password
   * @param loginRequest - User credentials
   * @returns Observable with login response containing JWT token and user info
   */
  login(loginRequest: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, loginRequest);
  }

  /**
   * Registers a new user account
   * @param registerRequest - User registration data
   * @returns Observable with registration response
   */
  register(registerRequest: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, registerRequest);
  }
}
