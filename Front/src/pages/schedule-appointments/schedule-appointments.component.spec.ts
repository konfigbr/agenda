import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleAppointmentsComponent } from './schedule-appointments.component';

describe('ScheduleAppointmentsComponent', () => {
  let component: ScheduleAppointmentsComponent;
  let fixture: ComponentFixture<ScheduleAppointmentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScheduleAppointmentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduleAppointmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
