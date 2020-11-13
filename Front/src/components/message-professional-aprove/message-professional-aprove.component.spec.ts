import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageProfessionalAproveComponent } from './message-professional-aprove.component';

describe('MessageProfessionalAproveComponent', () => {
  let component: MessageProfessionalAproveComponent;
  let fixture: ComponentFixture<MessageProfessionalAproveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MessageProfessionalAproveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageProfessionalAproveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
