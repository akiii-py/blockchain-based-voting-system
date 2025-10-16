import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Vote } from './vote';

describe('Vote', () => {
  let component: Vote;
  let fixture: ComponentFixture<Vote>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Vote]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Vote);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
