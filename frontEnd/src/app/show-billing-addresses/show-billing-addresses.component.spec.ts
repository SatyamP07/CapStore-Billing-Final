import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowBillingAddressesComponent } from './show-billing-addresses.component';

describe('ShowBillingAddressesComponent', () => {
  let component: ShowBillingAddressesComponent;
  let fixture: ComponentFixture<ShowBillingAddressesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowBillingAddressesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowBillingAddressesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
