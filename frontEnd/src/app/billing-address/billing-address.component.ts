import { AddressserviceService } from './../addressservice.service';
import { UserAddress } from './../address';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { Variable } from '@angular/compiler/src/render3/r3_ast';
@Component({
  selector: 'app-billing-address',
  templateUrl: './billing-address.component.html',
  styleUrls: ['./billing-address.component.css']
})
export class BillingAddressComponent implements OnInit {

  address:UserAddress=new UserAddress();
  constructor(private addressService:AddressserviceService, private router:Router, private formBuilder:FormBuilder){

  }

  myForm=this.formBuilder.group({
    address_line1:["",[Validators.required,Validators.pattern('[a-zA-Z0-9 ]*')]],
    address_line2:["",[Validators.required,Validators.pattern('[a-zA-Z0-9 ]*')]],
    district:["",[Validators.required,Validators.pattern('[a-zA-Z ]*')]],
    state:["",[Validators.required,Validators.pattern('[a-zA-Z ]*')]],
    landmark:["",[Validators.required,Validators.pattern('[a-zA-Z0-9 ]*')]],
    pinCode:["",[Validators.required,Validators.pattern('\\d{6}')]]
});

  ngOnInit(): void {
  }
  addNewAddress(){
    this.addressService.saveNewAddress(this.address).subscribe(
    );
    setTimeout(() => {
      this.router.navigate(['showAddress'])
    }, 2000);

  }

}
