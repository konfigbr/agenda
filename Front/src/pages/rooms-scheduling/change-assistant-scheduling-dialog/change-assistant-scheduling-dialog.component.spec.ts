import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeAssistantSchedulingDialogComponent } from './change-assistant-scheduling-dialog.component';

describe('ChangeAssistantSchedulingDialogComponent', () => {
  let component: ChangeAssistantSchedulingDialogComponent;
  let fixture: ComponentFixture<ChangeAssistantSchedulingDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangeAssistantSchedulingDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeAssistantSchedulingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
