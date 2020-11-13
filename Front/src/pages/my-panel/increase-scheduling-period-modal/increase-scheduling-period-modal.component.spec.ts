import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IncreaseSchedulingPeriodModalComponent } from './increase-scheduling-period-modal.component';

describe('IncreaseSchedulingPeriodModalComponent', () => {
  let component: IncreaseSchedulingPeriodModalComponent;
  let fixture: ComponentFixture<IncreaseSchedulingPeriodModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IncreaseSchedulingPeriodModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IncreaseSchedulingPeriodModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
