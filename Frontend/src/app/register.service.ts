import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  form: RegisterUser;
  form2: User; 
  constructor(private client:HttpClient) { }

  path1 = "http://localhost:9090/auth/new";
  path2 = "http://localhost:9090/user/save";
  path3 = "http://localhost:9090/admin/save";

  // Register only the user
  registerUser(form: RegisterUser): Observable<any> {
    console.log("Registering user:", form);
    return this.client.post(this.path1, form, { responseType: 'text' });
  }

  // Register only the customer
  registerUser2(form: User): Observable<any> {
    console.log("Registering user:", this.form2);
    return this.client.post(this.path2, form, { responseType: 'text' });
  }

  registerAdmin(form: Admin): Observable<any> {
    console.log("Registering customer:", this.form2);
    return this.client.post(this.path3, form, { responseType: 'text' });
  }

  // Register both user and customer in sequence
  registerBoth1(form: RegisterUser & User): Observable<any> {
    console.log("Registering both user and customer:", form);
    return this.registerUser(form).pipe(
      switchMap(() => this.registerUser2(form)),
      catchError((error) => {
        console.error("Registration failed:", error);
        return throwError(() => error);
      })
    );
  }

  registerBoth2(form: RegisterUser & Admin): Observable<any> {
    console.log("Registering both user and Agent:", form);
    return this.registerUser(form).pipe(
      switchMap(() => this.registerAdmin(form)),
      catchError((error) => {
        console.error("Registration failed:", error);
        return throwError(() => error);
      })
    );
  }
}

export class RegisterUser {
  name: string;
  email: string;
  password: string;
  role: string;
}
export class User {
  name: string;
  email: string;
  phoneNumber: string;
  shippingAddress: string;
}
export class Admin{
  name:string;
  email:string;
  phoneNumber: string;
}