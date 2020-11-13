import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentAdministrativeModalComponent } from './appointment-administrative-modal.component';

describe('AppointmentAdministrativeModalComponent', () => {
  let component: AppointmentAdministrativeModalComponent;
  let fixture: ComponentFixture<AppointmentAdministrativeModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppointmentAdministrativeModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentAdministrativeModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
