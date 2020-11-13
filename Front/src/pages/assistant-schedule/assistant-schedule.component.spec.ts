import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AssistantScheduleComponent } from './assistant-schedule.component';

describe('AssistantScheduleComponent', () => {
  let component: AssistantScheduleComponent;
  let fixture: ComponentFixture<AssistantScheduleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AssistantScheduleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AssistantScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
