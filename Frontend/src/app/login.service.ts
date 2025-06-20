import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { __await } from 'tslib';
import { catchError, tap } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
 
@Injectable({
  providedIn: 'root'
}) 
export class LoginService {
  user:LoginUser;
  path= "http://localhost:9090/auth/authenticate";
  private userDetailsPath = "http://localhost:9090/user/fetchByName";
  private adminDetailsPath = "http://localhost:9090/admin/fetchByName";

  constructor(private client:HttpClient, private router: Router) {
  }

  login(user: LoginUser) {
    console.log("In service - attempting login");
    console.log(user);
    return this.client.post(this.path, user, { responseType: 'text' }).pipe(
      tap((response: string) => {
      }),
      catchError(error => {
        return throwError(() => error);
      })
    );
  }

  getUserDetailsByUsername(username: string): Observable<any> {
    let token: string | null = null;
    if (typeof localStorage !== 'undefined') {
      token = localStorage.getItem('token');
    }

    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', 'Bearer ' + token);
    }
    return this.client.get<any>(`${this.userDetailsPath}/${username}`, { headers: headers }).pipe(
      catchError(error => {
        console.error(`Error fetching user details for ${username}:`, error);
        return throwError(() => new Error(`Failed to fetch user details: ${error.message || 'Server error'}`));
      })
    );
  }

  getAdminDetailsByUsername(username: string): Observable<any> {
    let token: string | null = this.getJWT();
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', 'Bearer ' + token);
    }
    return this.client.get<any>(`${this.adminDetailsPath}/${username}`, { headers: headers }).pipe(
      catchError(error => {
        console.error(`Error fetching admin details for ${username}:`, error);
        return throwError(() => new Error(`Failed to fetch admin details: ${error.message || 'Server error'}`));
      })
    );
  }

  getJWT(): string | null {
    return typeof localStorage !== 'undefined' ? localStorage.getItem("token") : null;
  }

  private clearAuthDataFromStorage(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      localStorage.removeItem("userId"); 
    }
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.removeItem("role");
    }
  }
  
  logout(): void {

    this.clearAuthDataFromStorage();
    localStorage.clear();
    this.router.navigate(["/login"]).then(() => {
      setTimeout(() => {
        alert("Logout successful.");
      }, 100); 
    }).catch(err => {
      console.error("Error navigating after logout:", err);
      setTimeout(() => {
        alert("Logout successful, but there was an issue navigating. Please refresh.");
      }, 100);
    });
  }

}
export class LoginUser{
  name: string;
  password: string;
} 