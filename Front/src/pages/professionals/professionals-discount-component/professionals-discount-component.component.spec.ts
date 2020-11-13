import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfessionalsDiscountComponentComponent } from './professionals-discount-component.component';

describe('ProfessionalsDiscountComponentComponent', () => {
  let component: ProfessionalsDiscountComponentComponent;
  let fixture: ComponentFixture<ProfessionalsDiscountComponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProfessionalsDiscountComponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfessionalsDiscountComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
