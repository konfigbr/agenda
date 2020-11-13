import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchedulingAppointmentModalComponent } from './scheduling-appointment-modal.component';

describe('SchedulingAppointmentModalComponent', () => {
  let component: SchedulingAppointmentModalComponent;
  let fixture: ComponentFixture<SchedulingAppointmentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchedulingAppointmentModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchedulingAppointmentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
