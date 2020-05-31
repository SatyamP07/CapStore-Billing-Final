import { StatusDeliveryComponent } from './status-delivery/status-delivery.component';
import { BillingComponent } from './billing/billing.component';
import { OrderComponent } from './order/order.component';
import { AddressserviceService } from './addressservice.service';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import { HttpModule } from '@angular/http';
import {ReactiveFormsModule,FormBuilder} from '@angular/forms';;
import { HomePageComponent } from './home-page/home-page.component'
import {Routes,RouterModule} from '@angular/router';
import { ProductPageComponent } from './product-page/product-page.component';
import { CustomerCartComponent } from './customer-cart/customer-cart.component';
import { CustomerWishlistComponent } from './customer-wishlist/customer-wishlist.component';
import {ProductFeedbackComponent} from './product-feedback/product-feedback.component';
import { InvoiceComponent } from './invoice/invoice.component';
import { BillingService } from './billing.service';
import { BillingAddressComponent } from './billing-address/billing-address.component';
import { PaymentComponent } from './payment/payment.component';
import { ShowBillingAddressesComponent } from './show-billing-addresses/show-billing-addresses.component';
import { AdminComponent } from './admin/admin.component';

const appRoutes: Routes = [
    {path: 'showHome', component: HomePageComponent},
    {path: 'showProduct', component: ProductPageComponent},
    {path: 'showCart', component: CustomerCartComponent},
    {path: 'showWishlist', component: CustomerWishlistComponent},
    {path: 'showProductFeedback', component: ProductFeedbackComponent},
    {path: 'invoice', component: InvoiceComponent},
    {path: 'showAddress', component: ShowBillingAddressesComponent},
    {path: 'newAddress', component: BillingAddressComponent},
    {path: 'payment', component: PaymentComponent},
    {path: 'coupon', component: BillingComponent},
    {path: 'order', component: OrderComponent},
    {path: 'status', component: StatusDeliveryComponent},
    {path: 'admin', component: AdminComponent}
]
@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        ReactiveFormsModule,
        HttpModule,
        RouterModule.forRoot(appRoutes)
    ],
    declarations: [
        AppComponent,
        HomePageComponent,
        ProductPageComponent ,
        CustomerCartComponent ,
        CustomerWishlistComponent ,
        ProductFeedbackComponent,
        InvoiceComponent,
        BillingAddressComponent,
        PaymentComponent,
        ShowBillingAddressesComponent,
        BillingComponent,
        OrderComponent,
        StatusDeliveryComponent,
        AdminComponent
    ],
    providers: [BillingService,
        AddressserviceService
    ],
    bootstrap: [AppComponent]
})

export class AppModule { }