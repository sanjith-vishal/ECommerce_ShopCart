import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class CommonService {

  isLoggedIn: boolean = false;
  role: string = '';
  username: string = '';
  private loggedInUserId: number | null = null;

  constructor(private router: Router) { 
    if (typeof localStorage !== 'undefined') {
      const token = localStorage.getItem('token');
      const storedUsername = localStorage.getItem('username'); //Load username from localStorage
      const storedUserId = localStorage.getItem('userId');
      if (token) {
        this.isLoggedIn = true;
      }
      if (storedUsername) {
        this.username = storedUsername; //Set username from localStorage
      }
      if (storedUserId) {
        this.loggedInUserId = parseInt(storedUserId, 10);
      }
    }

    if (typeof sessionStorage !== 'undefined') {
      const storedRole = sessionStorage.getItem('role');
      if (storedRole) {
        this.role = storedRole; //Set role from session storage
      }
    }
  }
  setLoginState(token: string, role: string, username: string, userId: number): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('token', token);
      localStorage.setItem('username', username);
      localStorage.setItem('userId', userId.toString());
    }
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.setItem('role', role);
    }
    this.isLoggedIn = true;
    this.role = role;
    this.username = username;
    this.loggedInUserId = userId;
  }

  clearLoginState(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('userId');
    }
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.removeItem('role');
    }
    this.isLoggedIn = false;
    this.role = '';
    this.username = '';
    this.loggedInUserId = null;
  }

  getLoggedInUserId(): number | null {
    return this.loggedInUserId;
  }

  getUsername(): string | null {
    return this.username;
  }
}
