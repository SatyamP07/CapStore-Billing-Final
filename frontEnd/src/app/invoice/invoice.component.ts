import { UserAddress } from './../address';
import { Transaction } from './../transaction';
import { Order } from './../order';
import { BillingService } from '../billing.service';
import { Component, OnInit } from '@angular/core';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-invoiceorder',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css']
})
export class InvoiceComponent implements OnInit {
  private fileName: string;
  private order: Order;
  private transaction: Transaction;
  private address: UserAddress;
  private customerId: number;
  constructor(private _billingService: BillingService) { }

  ngOnInit() {
    this.customerId = this._billingService.getCustomerId();
    this.order = this._billingService.getOrder();
    this.transaction = this._billingService.getTransaction();
    this.address = this._billingService.getUserAddress();
  }

  downloadInvoice() {
    this.fileName = 'Invoice-Order-Id-' + this.order.orderId + '.pdf';
    this._billingService.downloadInvoice(this.order.orderId, this.customerId, Number.parseFloat(sessionStorage.getItem('couponDiscount'))).subscribe( data => {
      saveAs(new Blob([data], {type: 'application/pdf'}), this.fileName);
      sessionStorage.setItem('couponDiscount', '0');
    });
  }
}
