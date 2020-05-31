import { BillingService } from './../billing.service';
import { Order } from './../order';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import {CustomerService} from '../customer.service';
import { Customer } from '../customer';
import { Product } from '../product';
@Component({
  selector: 'app-customer-cart',
  templateUrl: './customer-cart.component.html',
  styleUrls: ['./customer-cart.component.css']
})
export class CustomerCartComponent implements OnInit {

  constructor(private _customerService:CustomerService, private _router: Router, private _billingService: BillingService) { }
  customer: Customer = new Customer();
  products: Product[] = [];
  order: Order;
 
  ngOnInit() {
    this._billingService.getCustomerById(3).subscribe(customer => this.customer = customer);
    setTimeout(()=> {
      console.log(this.customer);
      sessionStorage.setItem('userId', '3');
      this._billingService.getAllProductsWithUserId(3).subscribe(products => this.products = products);
    }, 2000);
  }

  placeOrder() {
    this._billingService.createOrder(this.customer).subscribe(order => this.order = order, err => {
      alert(err.error);
    });
    setTimeout(() => {
      sessionStorage.setItem('orderId', this.order.orderId.toString());
    this._router.navigate(['/showAddress']);
    }, 2000);
  }

}
