import { Component, OnInit } from '@angular/core';
import { Order } from '../order';
import { BillingService } from '../billing.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnInit {

  constructor(private billingService: BillingService) { }

  model: Order = {
    orderId:10,
    orderAmount:1000.0,
    orderStatus:'Pending',
    products:null,
    addressId:2,
    transaction:null,
    userId: 3
  }

  ngOnInit(): void {
    sessionStorage.setItem('userId', '3');
  }

  makePayment() {
    this.billingService.setOrder(this.model);
  }
}
