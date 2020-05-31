import { BillingService } from './../billing.service';
import { Order } from './../order';
import { Router } from '@angular/router';
import { UserAddress } from './../address';
import { AddressserviceService } from './../addressservice.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-show-billing-addresses',
  templateUrl: './show-billing-addresses.component.html',
  styleUrls: ['./show-billing-addresses.component.css']
})
export class ShowBillingAddressesComponent implements OnInit {

  addresses:UserAddress[];
  errorMessage:any;
  showComponent:boolean;
  addressId: number;
  order: Order = new Order();
  constructor(private addressService:AddressserviceService,private _router:Router, private _billingService: BillingService) {
   }

  ngOnInit(): void {
    this.addressService.getAddresses().subscribe(
      data=>{
        console.log(data);
        this.addresses=data;
      }
    );
  }

  setCureentAddress(event :any)
  {
      this.addressId=event.target.value;
      this.errorMessage="";
  } 

  currentAddress(){
      if(this.addressId==undefined){
        this.errorMessage="select the address";
      }
      else{
        this.order.orderId = Number.parseInt(sessionStorage.getItem('orderId'));
        this.order.addressId = this.addressId;
        sessionStorage.setItem('addressId', this.addressId.toString());
        this.addressService.updateOrderAddress(this.order).subscribe(
          data=>{
            console.log("addressId updated");
            console.log(this.addressId);
          }
        );
        for ( let i = 0; i < this.addresses.length; i++) {
          if (this.addresses[i].addressId = this.addressId) {
            this._billingService.setUserAddress(this.addresses[i]);
          }
        }

        this._router.navigate(['payment']);
      }
      
  }
  
  deleteAddress(addressId:number) {
    console.log("deleted");
      this.addressService.deleteAddress(addressId).subscribe(
        data=>{
            console.log(data);
            console.log("deleted");
            console.log(addressId);
        }
      );
     window.location.reload();
  }

}
