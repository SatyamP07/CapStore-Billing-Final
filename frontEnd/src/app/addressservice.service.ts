import { Order } from './order';
import { UserAddress } from './address';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class AddressserviceService {

  private baseURL = 'http://localhost:5000/CapStore/Billing';
  constructor(private http:HttpClient) { 

  }
  getAddresses():Observable<UserAddress[]>{
      return this.http.get<UserAddress[]>(this.baseURL + '/addresses/' + Number.parseInt(sessionStorage.getItem('userId')));
  }

  saveNewAddress(address:UserAddress){
      return this.http.post(this.baseURL + '/address/create/' + Number.parseInt(sessionStorage.getItem('userId')),address);
  }
  deleteAddress(addressId:number){
      return this.http.delete(this.baseURL + '/address/delete/' + Number.parseInt(sessionStorage.getItem('userId')) + '/' + addressId);
  }
  updateOrderAddress(order: Order){
   return this.http.patch(this.baseURL + '/address/update/', order);
   }
}
