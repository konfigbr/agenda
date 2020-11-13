import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfessionalsDiscountListComponentComponent } from './professionals-discount-list-component.component';

describe('ProfessionalsDiscountListComponentComponent', () => {
  let component: ProfessionalsDiscountListComponentComponent;
  let fixture: ComponentFixture<ProfessionalsDiscountListComponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProfessionalsDiscountListComponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfessionalsDiscountListComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
