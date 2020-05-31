import { BillingService } from './../billing.service';
import { Order } from './../order';
import { Component, OnInit } from '@angular/core';
import { equal } from 'assert';

@Component({
  selector: 'app-status-delivery',
  templateUrl: './status-delivery.component.html',
  styleUrls: ['./status-delivery.component.css']
})
export class StatusDeliveryComponent implements OnInit {

  user: number = Number.parseInt(sessionStorage.getItem('userId'));
  lastOrder: Order;

  firstOrder: Order ={
    orderId: 101,
    orderAmount : 500,
    orderStatus : "confirmed",
    products : null,
    addressId : 8,
    transaction : null,
    userId: 10
  };

  secondOrder: Order = {
    orderId: 102,
    orderAmount : 500,
    orderStatus : "shipped",
    products : null,
    addressId : 8,
    transaction : null,
    userId: 10
  };

  thirdOrder: Order = {
    orderId: 103,
    orderAmount : 500,
    orderStatus : "delivered",
    products : null,
    addressId : 8,
    transaction : null,
    userId: 10
  };

  Olists: Order[] //= [this.firstOrder, this.secondOrder, this.thirdOrder];
  orderdemo: Order;
  constructor( private _billingService: BillingService ) { }

 percentage: any;

  ngOnInit(): void {
    this._billingService.getDelivaryStatus(this.user).subscribe( response =>{this.Olists = response;});
    setTimeout(() => this.getStatus(), 2000);
       // here by default call to the function at the time of integration we need to assign this to button
  }

  getStatus(){
      console.log(this.Olists);
      let last = this.Olists.length-1;
      this.lastOrder = this.Olists[last];      
      this.getPercentage();
  }

   getPercentage(){
    if (this.lastOrder.orderStatus == "pending")
      {
        this.percentage = 1;
      }
    if (this.lastOrder.orderStatus =="confirmed")
    {
      this.percentage = 6;
    }
    if (this.lastOrder.orderStatus =="shipped")
    {
      this.percentage = 50;
    }
    if (this.lastOrder.orderStatus =="delivered")
    {
      this.percentage = 100;
    }

   }

   selectOrder(i:number){

    this.lastOrder = this.Olists[i];
    this.getPercentage();
    console.log(this.percentage);

  }


}
