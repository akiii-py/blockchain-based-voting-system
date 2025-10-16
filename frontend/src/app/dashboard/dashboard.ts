import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ElectionService } from '../election';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {
  elections: any[] = [];
  userRole: string = '';
  user: any = null;
  userVotesCount: number = 0;
  recentVotes: any[] = [];

  constructor(private electionService: ElectionService, private router: Router) {}

  ngOnInit() {
    this.userRole = localStorage.getItem('role') || '';
    this.loadUserData();
    this.loadActiveElections();
    this.loadUserStats();
    this.loadRecentVotes();
  }

  loadUserData() {
    // Load user data from localStorage or API
    const userData = localStorage.getItem('user');
    if (userData) {
      this.user = JSON.parse(userData);
    }
  }

  loadActiveElections() {
    this.electionService.getActiveElections().subscribe({
      next: (data: any[]) => {
        this.elections = data;
      },
      error: (error: any) => {
        console.error('Error loading elections', error);
      }
    });
  }

  loadUserStats() {
    // Load user's voting statistics
    // This would typically come from an API call
    this.userVotesCount = 0; // Placeholder
  }

  loadRecentVotes() {
    // Load recent voting activity
    // This would typically come from an API call
    this.recentVotes = []; // Placeholder
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }

  goToVote(electionId: number) {
    this.router.navigate(['/vote'], { queryParams: { electionId } });
  }

  goToAdmin() {
    this.router.navigate(['/admin']);
  }

  createElection() {
    // Navigate to election creation page
    this.router.navigate(['/admin/create-election']);
  }

  viewReports() {
    // Navigate to reports page
    this.router.navigate(['/admin/reports']);
  }
}
