import { TestBed } from '@angular/core/testing';

import { Voting } from './voting';

describe('Voting', () => {
  let service: Voting;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Voting);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
