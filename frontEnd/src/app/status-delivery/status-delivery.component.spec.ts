import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusDeliveryComponent } from './status-delivery.component';

describe('StatusDeliveryComponent', () => {
  let component: StatusDeliveryComponent;
  let fixture: ComponentFixture<StatusDeliveryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StatusDeliveryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusDeliveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
