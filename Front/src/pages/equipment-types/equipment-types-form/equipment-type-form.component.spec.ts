import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipmentTypeFormComponent } from './equipment-type-form.component';

describe('EquipmentTypesFormComponent', () => {
  let component: EquipmentTypeFormComponent;
  let fixture: ComponentFixture<EquipmentTypeFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EquipmentTypeFormComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EquipmentTypeFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
