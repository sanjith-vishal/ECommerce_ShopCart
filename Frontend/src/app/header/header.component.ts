import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonService } from '../common.service';
import { LoginService } from '../login.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  constructor(public router: Router, public common: CommonService, public loginService: LoginService) { } 

  onLogout(): void {
    this.loginService.logout();
    this.common.isLoggedIn = false;
    this.common.role = '';
    this.common.username = '';
    this.common.clearLoginState();
    this.router.navigate(['/login']);
  }

}
