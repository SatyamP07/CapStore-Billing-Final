import { BillingService } from './../billing.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  private revenue: number = 0;
  constructor(private _billingService: BillingService) { }

  ngOnInit() {
    this._billingService.getTotalRevenue().subscribe((revenue) => this.revenue = revenue);
  }

}
