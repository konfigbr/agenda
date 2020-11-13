import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportProfessionalsComponent } from './report-professionals.component';

describe('ReportProfessionalsComponent', () => {
  let component: ReportProfessionalsComponent;
  let fixture: ComponentFixture<ReportProfessionalsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportProfessionalsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportProfessionalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
