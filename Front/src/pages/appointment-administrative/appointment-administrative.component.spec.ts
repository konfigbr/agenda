import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentAdministrativeComponent } from './appointment-administrative.component';

describe('AppointmentAdministrativeComponent', () => {
  let component: AppointmentAdministrativeComponent;
  let fixture: ComponentFixture<AppointmentAdministrativeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppointmentAdministrativeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentAdministrativeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
