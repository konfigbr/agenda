import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleRoomByTheTeamComponent } from './schedule-room-by-the-team.component';

describe('ScheduleRoomByTheTeamComponent', () => {
  let component: ScheduleRoomByTheTeamComponent;
  let fixture: ComponentFixture<ScheduleRoomByTheTeamComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScheduleRoomByTheTeamComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduleRoomByTheTeamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
