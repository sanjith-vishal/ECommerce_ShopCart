import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomePageComponent } from './home-page/home-page.component';
import { CartComponent } from './cart/cart.component';
import { OrdersComponent } from './orders/orders.component';
import { ProductsComponent } from './products/products.component';
import { authGuard } from './auth.guard';
import { UsersComponent } from './users/users.component';
import { ProfileComponent } from './profile/profile.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegistrationComponent },
  { path: 'home', component: HomePageComponent, canActivate:[authGuard]},
  { path: 'products', component: ProductsComponent, canActivate:[authGuard]},
  { path: 'cart', component: CartComponent, canActivate:[authGuard]},
  { path: 'orders', component: OrdersComponent, canActivate:[authGuard]},
  { path: 'users', component: UsersComponent, canActivate:[authGuard]}, 
  { path: 'profile', component: ProfileComponent, canActivate:[authGuard]}, 
  { path: '**', redirectTo: 'home' }
];

