import { Product } from './product';
import { Customer } from './customer';
import { UserAddress } from './address';
import { Order } from './order';
import { Transaction } from './transaction';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BillingService {

  private transaction: Transaction;
  private model: Order;
  private baseURL = 'http://localhost:5000/CapStore/Billing';
  private headers = new HttpHeaders ({'Content-Type': 'application/json'});
  private options = {headers: this.headers};

  constructor(private _http: HttpClient ) { }

  downloadInvoice (orderId, customerId, couponDiscount) {
    return this._http.get(this.baseURL + '/download/' + orderId + '/' + customerId + '/' + couponDiscount, {
      responseType: 'arraybuffer'
    });
  }

  apply(order:Order, code: string) {
    console.log("checking");
    return this._http.put<Order>(this.baseURL + '/applyCoupon/' + code, order, this.options).pipe(catchError(this.handleError));
  }

  getAllProductsWithUserId(userId: number) {
    return this._http.get<Product[]>(this.baseURL + '/' + userId + '/products', this.options);
  }

  public getOrderById (orderId: number) {
    return this._http.get<Order>(this.baseURL + '/order/' + orderId, this.options);
  }

  public createOrder(customer: Customer) {
   return this._http.post<Order>(this.baseURL + '/order/create', customer).pipe(catchError(this.handleError));
  }

  public getOrder(): Order {
      return JSON.parse(sessionStorage.getItem('order'));
  }

  public setOrder(newModel: Order) {
      sessionStorage.setItem('order', JSON.stringify(newModel));
  }

  public getTransaction(): Transaction {
    return JSON.parse(sessionStorage.getItem('transaction'));
}

  public setTransaction(newModel: Transaction) {
      sessionStorage.setItem('transaction', JSON.stringify(newModel));
  }

  public getUserAddress(): UserAddress {
    return JSON.parse(sessionStorage.getItem('userAddress'));
  }

  public setUserAddress(newModel: UserAddress) {
    sessionStorage.setItem('userAddress', JSON.stringify(newModel));
  }

  public getCustomerId(): number {
    return JSON.parse(sessionStorage.getItem('userId'));
  }

  public setCustomerId(customerId: number) {
    sessionStorage.setItem('userId', JSON.stringify(customerId));
  }

  public getDelivaryStatus(userId: number) {
    return this._http.get<Order[]>(this.baseURL + '/getDelivaryStatus/' + userId, this.options);
  }

  public doTransaction(orderId: number, discount: number, transactionType: string) {
    return this._http.get<Transaction>(this.baseURL + '/transaction/' + orderId + '/' + discount + '/' + transactionType, this.options);
  }

  public getCustomerById(userId: number) {
    console.log(this.baseURL + '/customer/' + userId);
    return this._http.get<Customer>(this.baseURL + '/customer/' + userId, this.options);
  }

  public getTotalRevenue() {
    return this._http.get<number>(this.baseURL + '/admin/revenue', this.options);
  }


  handleError(error) {
    let errorMessage = '';
    if(error.error instanceof ErrorEvent) {
        //client-side error
        errorMessage = `Error: ${error.error.errorMessage}`;
        console.log("From Client Side");
    }
    else {
        //server-side error
        errorMessage = `Error: ${error.error.errorMessage}`;
    }
    console.log(error.error.errorMessage);
    return throwError(error);
}
}
