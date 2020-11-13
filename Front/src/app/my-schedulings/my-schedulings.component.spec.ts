import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MySchedulingsComponent } from './my-schedulings.component';

describe('MySchedulingsComponent', () => {
  let component: MySchedulingsComponent;
  let fixture: ComponentFixture<MySchedulingsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MySchedulingsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MySchedulingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
