import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../admin';
import { ElectionService, Election } from '../election';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminComponent implements OnInit {
  users: any[] = [];
  elections: Election[] = [];
  newElection = { title: '', description: '', startTime: '', endTime: '' };
  newUser = { username: '', password: '', email: '', fullName: '', voterId: '', role: 'USER' };

  constructor(
    private adminService: AdminService,
    private electionService: ElectionService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadUsers();
    this.loadElections();
  }

  loadUsers() {
    this.adminService.getUsers().subscribe({
      next: (data: any[]) => {
        this.users = data;
      },
      error: (error: any) => {
        console.error('Error loading users', error);
      }
    });
  }

  loadElections() {
    this.electionService.getActiveElections().subscribe({
      next: (data: Election[]) => {
        this.elections = data;
      },
      error: (error: any) => {
        console.error('Error loading elections', error);
      }
    });
  }

  createUser() {
    this.adminService.createUser(this.newUser).subscribe({
      next: (response: any) => {
        this.loadUsers();
        this.newUser = { username: '', password: '', email: '', fullName: '', voterId: '', role: 'USER' };
      },
      error: (error: any) => {
        console.error('Error creating user', error);
      }
    });
  }

  createElection() {
    this.electionService.createElection(this.newElection).subscribe({
      next: (response: Election) => {
        this.loadElections();
        this.newElection = { title: '', description: '', startTime: '', endTime: '' };
      },
      error: (error: any) => {
        console.error('Error creating election', error);
      }
    });
  }

  toggleElectionStatus(electionId: number) {
    this.electionService.toggleElectionStatus(electionId).subscribe({
      next: (response: Election) => {
        this.loadElections();
      },
      error: (error: any) => {
        console.error('Error toggling election status', error);
      }
    });
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/login']);
  }
}
