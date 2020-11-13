import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportSchedulingsComponent } from './report-schedulings.component';

describe('ReportSchedulingsComponent', () => {
  let component: ReportSchedulingsComponent;
  let fixture: ComponentFixture<ReportSchedulingsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportSchedulingsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportSchedulingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
