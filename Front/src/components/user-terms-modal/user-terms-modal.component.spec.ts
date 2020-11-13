import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserTermsModalComponent } from './user-terms-modal.component';

describe('UserTermsModalComponent', () => {
  let component: UserTermsModalComponent;
  let fixture: ComponentFixture<UserTermsModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserTermsModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserTermsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
