﻿import { Component } from '@angular/core';

@Component({
    selector: 'app',
    templateUrl: 'app.component.html',
    styleUrls:['app.component.css']
})

export class AppComponent {

    showMenu(menu){
        menu.style.display="block";
      }
      closeMenu(menu){
        menu.style.display="none";
      }

 }