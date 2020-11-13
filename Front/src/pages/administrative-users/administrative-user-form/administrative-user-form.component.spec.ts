import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministrativeUserFormComponent } from './administrative-user-form.component';

describe('AdministrativeUserFormComponent', () => {
  let component: AdministrativeUserFormComponent;
  let fixture: ComponentFixture<AdministrativeUserFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdministrativeUserFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdministrativeUserFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
