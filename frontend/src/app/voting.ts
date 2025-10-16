import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Vote {
  id: number;
  userId: number;
  candidateId: number;
  electionId: number;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class VotingService {
  private apiUrl = 'http://localhost:8080/api/voting';

  constructor(private http: HttpClient) {}

  castVote(userId: number, candidateId: number, electionId: number): Observable<Vote> {
    return this.http.post<Vote>(`${this.apiUrl}/vote`, { userId, candidateId, electionId });
  }

  getVotes(electionId: number): Observable<Vote[]> {
    return this.http.get<Vote[]>(`${this.apiUrl}/election/${electionId}/votes`);
  }

  getVotingStatus(userId: number, electionId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/user/${userId}/election/${electionId}/status`);
  }

  getUserVote(userId: number, electionId: number): Observable<Vote> {
    return this.http.get<Vote>(`${this.apiUrl}/user/${userId}/election/${electionId}`);
  }
}
