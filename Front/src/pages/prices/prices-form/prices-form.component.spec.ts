import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PricesFormComponent } from './prices-form.component';

describe('PricesFormComponent', () => {
  let component: PricesFormComponent;
  let fixture: ComponentFixture<PricesFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PricesFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PricesFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
