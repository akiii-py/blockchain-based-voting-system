import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Election {
  id: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  isActive: boolean;
  candidates: Candidate[];
}

export interface Candidate {
  id: number;
  name: string;
  party: string;
  description: string;
  voteCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class ElectionService {
  private apiUrl = 'http://localhost:8080/api/elections';

  constructor(private http: HttpClient) {}

  getActiveElections(): Observable<Election[]> {
    return this.http.get<Election[]>(`${this.apiUrl}/active`);
  }

  getElection(id: number): Observable<Election> {
    return this.http.get<Election>(`${this.apiUrl}/${id}`);
  }

  getCandidates(electionId: number): Observable<Candidate[]> {
    return this.http.get<Candidate[]>(`${this.apiUrl}/${electionId}/candidates`);
  }

  createElection(election: { title: string; description: string; startTime: string; endTime: string }): Observable<Election> {
    return this.http.post<Election>(this.apiUrl, election);
  }

  addCandidate(electionId: number, candidate: { name: string; party: string; description: string }): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/${electionId}/candidates`, candidate);
  }

  toggleElectionStatus(id: number): Observable<Election> {
    return this.http.put<Election>(`${this.apiUrl}/${id}/activate/deactivate`, {});
  }
}
