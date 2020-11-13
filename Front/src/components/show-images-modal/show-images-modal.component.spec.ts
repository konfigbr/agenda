import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowImagesModalComponent } from './show-images-modal.component';

describe('ShowImagesModalComponent', () => {
  let component: ShowImagesModalComponent;
  let fixture: ComponentFixture<ShowImagesModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowImagesModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowImagesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
