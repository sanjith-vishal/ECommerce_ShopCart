import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { LoginService } from '../login.service';
import { CommonService } from '../common.service';
import { jwtDecode } from 'jwt-decode';
import { switchMap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'login',
  imports: [RouterLink, RouterOutlet ,FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent {
  constructor(private router: Router, private logService: LoginService, private common: CommonService, private access: ActivatedRoute) { }

  validated(form: NgForm): void {
    console.log("validate function calling.......");
    console.log(form.value);

    this.logService.login(form.value).pipe(
      switchMap((token: string) => {
        localStorage.setItem("token", token);
        console.log("Login successful, token received:", token);

        const decoded = jwtDecode<JwtPayload>(token);
        const role: string = decoded.roles ?? 'No role found';
        const username: string = decoded.sub ?? 'No sub found';

        localStorage.setItem("username", username);
        sessionStorage.setItem("role", role);

        let detailsFetch$: Observable<any>; 

        if (role === 'USER') {
          detailsFetch$ = this.logService.getUserDetailsByUsername(username);
          console.log("Attempting to fetch USER details...");
        } else if (role === 'ADMIN') {
          detailsFetch$ = this.logService.getAdminDetailsByUsername(username);
          console.log("Attempting to fetch ADMIN details...");
        } else {
          console.warn("Unknown role or role not found in JWT:", role);
          // Return an observable that emits an object indicating no ID found,
          // to prevent the subscription from breaking
          return of({ userId: null, adminId: null });
        }
        return detailsFetch$;
      })
    ).subscribe({
      next: (details: any) => {
        const username = localStorage.getItem("username") || 'Unknown';
        const role = sessionStorage.getItem("role") || 'No Role';

        let idToStore: number | null = null; //

        if (role === 'USER' && details && details.userId) {
          idToStore = details.userId;
          console.log("Fetched User ID:", idToStore);
        } else if (role === 'ADMIN' && details && details.adminId) {
          idToStore = details.adminId; // Use adminId for Admin role
          console.log("Fetched Admin ID:", idToStore);
        }

        if (idToStore !== null) { // Only proceed if a valid ID was found
          this.common.setLoginState(localStorage.getItem("token")!, role, username, idToStore);
          alert(`Successfully logged in as ${username} (${role}).`);
          this.router.navigate(["/home"]);
        } else {
          alert("Login successful, but failed to retrieve user/admin ID. Please contact support.");
          console.error("Login flow error - failed to get a valid ID from details:", details);
          this.common.clearLoginState();
        }
      },
      error: (err) => {
        // Clear any partially stored login data on error
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        sessionStorage.removeItem("role");
        this.common.clearLoginState();

        if (err.status === 403 || err.message?.includes('403')) {
          alert("Invalid credentials. Please try again.");
        } else if (err.message?.includes('Failed to fetch user details') || err.message?.includes('Failed to fetch admin details')) { // HIGHLIGHT: Updated error message check
          alert("Login successful, but failed to retrieve user/admin details. Please contact support.");
          console.error("Login flow error - failed to fetch details:", err);
        }
        else {
          alert("Something went wrong during login. Please try later.");
          console.error("Login error:", err);
        }
      }
    });
  }
}

interface JwtPayload {
  roles?: string;
  sub?: string;
}