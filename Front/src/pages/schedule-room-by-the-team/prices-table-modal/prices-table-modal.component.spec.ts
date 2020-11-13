import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PricesTableModalComponent } from './prices-table-modal.component';

describe('PricesTableModalComponent', () => {
  let component: PricesTableModalComponent;
  let fixture: ComponentFixture<PricesTableModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PricesTableModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PricesTableModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
