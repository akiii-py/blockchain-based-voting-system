import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: number;
  username: string;
  password: string;
  role: string;
  enabled: boolean;
  email: string;
  fullName: string;
  voterId: string;
  hasVoted: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  createUser(user: any): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/users`, user);
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/users`);
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/${id}`);
  }

  createElection(election: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/elections`, election);
  }

  toggleElectionStatus(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/elections/${id}/activate/deactivate`, {});
  }
}
