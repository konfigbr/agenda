import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfessionalRegisterComponent } from './professional-register.component';

describe('ProfessionalRegisterComponent', () => {
  let component: ProfessionalRegisterComponent;
  let fixture: ComponentFixture<ProfessionalRegisterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProfessionalRegisterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfessionalRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
