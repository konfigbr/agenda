import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchedulesofthedayMyPanelComponent } from './schedulesoftheday-my-panel.component';

describe('SchedulesofthedayMyPanelComponent', () => {
  let component: SchedulesofthedayMyPanelComponent;
  let fixture: ComponentFixture<SchedulesofthedayMyPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchedulesofthedayMyPanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchedulesofthedayMyPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
