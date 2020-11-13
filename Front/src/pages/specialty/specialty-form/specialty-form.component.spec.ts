import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialtyFormComponent } from './specialty-form.component';

describe('SpecialtyFormComponent', () => {
  let component: SpecialtyFormComponent;
  let fixture: ComponentFixture<SpecialtyFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SpecialtyFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpecialtyFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
