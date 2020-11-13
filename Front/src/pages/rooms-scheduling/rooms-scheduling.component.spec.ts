import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomsSchedulingComponent } from './rooms-scheduling.component';

describe('RoomsSchedulingComponent', () => {
  let component: RoomsSchedulingComponent;
  let fixture: ComponentFixture<RoomsSchedulingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoomsSchedulingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoomsSchedulingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
