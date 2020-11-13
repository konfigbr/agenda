import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchedulesofthedayComponent } from './schedulesoftheday.component';

describe('SchedulesofthedayComponent', () => {
  let component: SchedulesofthedayComponent;
  let fixture: ComponentFixture<SchedulesofthedayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchedulesofthedayComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchedulesofthedayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
