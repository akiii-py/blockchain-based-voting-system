import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ElectionService, Candidate } from '../election';
import { VotingService } from '../voting';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-vote',
  imports: [CommonModule, FormsModule],
  templateUrl: './vote.html',
  styleUrl: './vote.css'
})
export class VoteComponent implements OnInit {
  electionId: number = 0;
  candidates: Candidate[] = [];
  selectedCandidateId: number | null = null;
  hasVoted: boolean = false;
  message: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private electionService: ElectionService,
    private votingService: VotingService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.electionId = +params['electionId'];
      this.loadCandidates();
      this.checkVotingStatus();
    });
  }

  loadCandidates() {
    this.electionService.getCandidates(this.electionId).subscribe({
      next: (data: Candidate[]) => {
        this.candidates = data;
      },
      error: (error: any) => {
        console.error('Error loading candidates', error);
      }
    });
  }

  checkVotingStatus() {
    const userId = 1; // Assuming user ID is stored or retrieved from token
    this.votingService.getVotingStatus(userId, this.electionId).subscribe({
      next: (status: boolean) => {
        this.hasVoted = status;
      },
      error: (error: any) => {
        console.error('Error checking voting status', error);
      }
    });
  }

  castVote() {
    if (this.selectedCandidateId && !this.hasVoted) {
      const userId = 1; // Assuming user ID is stored or retrieved from token
      this.votingService.castVote(userId, this.selectedCandidateId, this.electionId).subscribe({
        next: (response: any) => {
          this.message = 'Vote cast successfully!';
          this.hasVoted = true;
        },
        error: (error: any) => {
          this.message = 'Error casting vote. Please try again.';
        }
      });
    }
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
