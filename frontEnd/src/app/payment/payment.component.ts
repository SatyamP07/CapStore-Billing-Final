import { UserAddress } from './../address';
import { Transaction } from './../transaction';
import { BillingService } from './../billing.service';
import { Order } from './../order';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit {
  cashOnDeliver:boolean;
  debit:boolean;
  credit:boolean=true;
  netBanking:boolean;
  type: string = 'COD';
  transaction: Transaction = new Transaction();
  userAddress: UserAddress;

  selectedBankName:string='Allahabad Bank';
  banksName:any=[
    'Allahabad Bank',
    'Andhra Bank',
    'Bank of Baroda',
    'Bank of India',
    'Bank of Maharashtra',
    'Canara Bank',
    'Central Bank of India',
    'Corporation Bank',
    'Dena Bank',
    'Indian Bank',
    'Indian Overseas Bank'
  ]

  constructor(private formBuilder:FormBuilder, private _router: Router, private _billingService: BillingService) { }

  couponCode: string;
  model: Order = new Order();
  discount: number = 0;
  order = new Order();

  ch = false;

  ngOnInit() {
    this.userAddress = this._billingService.getUserAddress();
    this._billingService.getOrderById(Number.parseInt(sessionStorage.getItem('orderId')))
      .subscribe(order => this.model = order);
      setTimeout( () => {
      console.log(this.model);
      this._billingService.setOrder(this.model);
      }, 2000);
  }

  apply() {
    this._billingService.apply(this.model, this.couponCode).subscribe(
      res=>{
        this.ch=true;
        this.order=res;
        this.discount = -(this.model.orderAmount - this.order.orderAmount);
        alert("Coupon Applied Successfully!");
      },
      err=> {
        window.alert(err.error);
      })
  }



creditForm=this.formBuilder.group({
    name:["",[Validators.required,Validators.pattern('[a-zA-Z ]*')]],
    cardNumber:["",[Validators.required,Validators.pattern('\\d{16}')]],
    month:["",[Validators.required,Validators.pattern("^(1[012]|0?[1-9])$")]],
    year:["",[Validators.required,Validators.pattern('2[0-9]\\d{2}')]],
    cvv:["",[Validators.required,Validators.pattern('\\d{3}')]]
});

debitForm=this.formBuilder.group({
  name:["",[Validators.required,Validators.pattern('[a-zA-Z ]*')]],
  cardNumber:["",[Validators.required,Validators.pattern('\\d{16}')]],
  month:["",[Validators.required,Validators.pattern("^(1[012]|0?[1-9])$")]],
  year:["",[Validators.required,Validators.pattern('2[0-9]\\d{2}')]],
  cvv:["",[Validators.required,Validators.pattern('\\d{3}')]]
});

  setCashOnDelivery(){
    this.cashOnDeliver=true;
    this.debit=false;
    this.credit=false;
    this.netBanking=false;
    this.type = 'COD';
  }
  setDebit(){
    this.cashOnDeliver=false;
    this.debit=true;
    this.credit=false;
    this.netBanking=false;
    this.type = 'Debit Card';
  }
  setCredit(){
    this.cashOnDeliver=false;
    this.debit=false;
    this.credit=true;
    this.netBanking=false;
    this.type = 'Credit Card';
  }
  setNetBanking(){
    this.cashOnDeliver=false;
    this.debit=false;
    this.credit=false;
    this.netBanking=true;
    this.type = 'Net Banking';
  }

  confirmPayment() {
    this._billingService.doTransaction(this.model.orderId, this.discount, this.type)
      .subscribe(transcation => this.transaction = transcation);
      sessionStorage.setItem('couponDiscount', (-this.discount).toString());
    setTimeout( () => {
      this._billingService.setTransaction(this.transaction);
    this._router.navigate(['invoice']);
    }, 2000);
  }

  
netBankingForm=this.formBuilder.group({
  userName:["",[Validators.required,Validators.pattern('[a-zA-Z0-9]*')]],
  password:["",[Validators.required,Validators.pattern('[a-zA-Z0-9]*')]]
});

dropDownChangedHandler(event:any){
  this.selectedBankName=event.target.value;
}
}
