import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchedulingPanelComponent } from './scheduling-panel.component';

describe('SchedulingPanelComponent', () => {
  let component: SchedulingPanelComponent;
  let fixture: ComponentFixture<SchedulingPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchedulingPanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchedulingPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
