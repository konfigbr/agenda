import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthPatientComponent } from './auth-patient.component';

describe('AuthPatientComponent', () => {
  let component: AuthPatientComponent;
  let fixture: ComponentFixture<AuthPatientComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuthPatientComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthPatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
