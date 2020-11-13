import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckingCheckoutProfessionalAuthComponent } from './checking-checkout-professional-auth.component';

describe('CheckingCheckoutProfessionalAuthComponent', () => {
  let component: CheckingCheckoutProfessionalAuthComponent;
  let fixture: ComponentFixture<CheckingCheckoutProfessionalAuthComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckingCheckoutProfessionalAuthComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckingCheckoutProfessionalAuthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
