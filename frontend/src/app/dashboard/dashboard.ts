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

  constructor(private electionService: ElectionService, private router: Router) {}

  ngOnInit() {
    this.userRole = localStorage.getItem('role') || '';
    this.loadActiveElections();
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

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/login']);
  }

  goToVote(electionId: number) {
    this.router.navigate(['/vote'], { queryParams: { electionId } });
  }

  goToAdmin() {
    this.router.navigate(['/admin']);
  }
}
