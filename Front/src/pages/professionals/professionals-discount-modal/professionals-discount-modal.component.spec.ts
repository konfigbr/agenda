import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfessionalsDiscountModalComponent } from './professionals-discount-modal.component';

describe('ProfessionalsDiscountModalComponent', () => {
  let component: ProfessionalsDiscountModalComponent;
  let fixture: ComponentFixture<ProfessionalsDiscountModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProfessionalsDiscountModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfessionalsDiscountModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
