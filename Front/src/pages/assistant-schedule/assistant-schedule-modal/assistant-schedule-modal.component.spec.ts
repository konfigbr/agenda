import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AssistantScheduleModalComponent } from './assistant-schedule-modal.component';

describe('AssistantScheduleModalComponent', () => {
  let component: AssistantScheduleModalComponent;
  let fixture: ComponentFixture<AssistantScheduleModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AssistantScheduleModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AssistantScheduleModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
