import { Component, OnInit } from '@angular/core';
import { Order } from '../order';
import { BillingService } from '../billing.service';

@Component({
  selector: 'app-billing',
  templateUrl: './billing.component.html',
  styleUrls: ['./billing.component.css']
})
export class BillingComponent implements OnInit {

  constructor(private billingService: BillingService) { }

  couponCode: string;

  model: Order;
  discount: number;
  order = new Order();

  ch = false;

  ngOnInit() {
    this.model = this.billingService.getOrder();
  }

  apply() {
    this.billingService.apply(this.model, this.couponCode).subscribe(
      res=>{
        if(res) {
          this.ch=true;
          this.order=res;
          this.discount = -(this.model.orderAmount - this.order.orderAmount);
          alert("Coupon Applied Successfully!");
        }
        else {
          alert("Invalid Coupon!!");
        }
      },
      err=> {
        alert("Invalid Coupon!!");
      })
  }

}
