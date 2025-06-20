import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonService } from './common.service';

export interface UserProfile {
  userId: number;
  name: string;
  email: string;
  phoneNumber: string;
  shippingAddress: string;
}

export interface AdminDetails {
  adminId: number;
  name: string;
  email: string;
  phoneNumber: string;
}


@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private userApiUrl = 'http://localhost:9090/user';
  private adminApiUrl = 'http://localhost:9090/admin';

  constructor(private http: HttpClient, private commonService: CommonService) { }

  private getAuthHeaders(): HttpHeaders {
    let token: string | null = null;
    if (typeof localStorage !== 'undefined') {
      token = localStorage.getItem('token');
    }
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', 'Bearer ' + token);
    }
    return headers;
  }

  getAllUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(`${this.userApiUrl}/fetchAll`, { headers: this.getAuthHeaders() }).pipe(
      catchError(error => {
        console.error('Error fetching all users:', error);
        return throwError(() => new Error(`Failed to fetch users: ${error.message || 'Server error'}`));
      })
    );
  }

  // Fetches a USER profile by userId
  getUserById(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.userApiUrl}/fetchById/${userId}`, { headers: this.getAuthHeaders() }).pipe(
      catchError(error => {
        console.error(`Error fetching user details for ID ${userId}:`, error);
        return throwError(() => new Error(`Failed to fetch user details: ${error.message || 'Server error'}`));
      })
    );
  }

  // Fetches an ADMIN profile by adminId
  getAdminById(adminId: number): Observable<AdminDetails> {
    return this.http.get<AdminDetails>(`${this.adminApiUrl}/fetchById/${adminId}`, { headers: this.getAuthHeaders() }).pipe(
      catchError(error => {
        console.error(`Error fetching admin details for ID ${adminId}:`, error);
        return throwError(() => new Error(`Failed to fetch admin details: ${error.message || 'Server error'}`));
      })
    );
  }

  updateUser(userProfile: UserProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.userApiUrl}/update`, userProfile, { headers: this.getAuthHeaders() }).pipe(
      catchError(error => {
        console.error('Error updating user profile:', error);
        return throwError(() => error);
      })
    );
  }

  // NEW: Updates an ADMIN profile
  updateAdmin(adminProfile: AdminDetails): Observable<AdminDetails> {
    return this.http.put<AdminDetails>(`${this.adminApiUrl}/update`, adminProfile, { headers: this.getAuthHeaders() }).pipe(
      catchError(error => {
        console.error('Error updating admin profile:', error);
        return throwError(() => error);
      })
    );
  }

  deleteUser(userId: number): Observable<string> {
    return this.http.delete(`${this.userApiUrl}/delete/${userId}`, { headers: this.getAuthHeaders(), responseType: 'text' }).pipe(
      catchError(error => {
        console.error(`Error deleting user profile with ID ${userId}:`, error);
        return throwError(() => error);
      })
    );
  }
}