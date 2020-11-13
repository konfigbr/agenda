import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpresaProfissionaisComponent } from './professionals.component';

describe('EmpresaProfissionaisComponent', () => {
  let component: EmpresaProfissionaisComponent;
  let fixture: ComponentFixture<EmpresaProfissionaisComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EmpresaProfissionaisComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmpresaProfissionaisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
