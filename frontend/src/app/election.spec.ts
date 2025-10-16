import { TestBed } from '@angular/core/testing';

import { Election } from './election';

describe('Election', () => {
  let service: Election;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Election);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
