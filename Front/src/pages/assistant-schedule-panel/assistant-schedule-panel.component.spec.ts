import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AssistantSchedulePanelComponent } from './assistant-schedule-panel.component';

describe('AssistantSchedulePanelComponent', () => {
  let component: AssistantSchedulePanelComponent;
  let fixture: ComponentFixture<AssistantSchedulePanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AssistantSchedulePanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AssistantSchedulePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
