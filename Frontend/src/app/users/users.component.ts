import { CommonModule, NgFor } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { UsersService } from '../users.service';
import { CommonService } from '../common.service';
import { Observable, forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { RegisterService, RegisterUser } from '../register.service';

interface UserFormData {
  userId?: number;
  name: string;
  email: string;
  password?: string;
  phoneNumber: string;
  shippingAddress: string;
}

interface UserProfile {
  userId: number;
  name: string;
  email: string;
  phoneNumber: string;
  shippingAddress: string;
}

@Component({
  selector: 'app-users',
  imports: [CommonModule, NgFor,  FormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})

export class UsersComponent implements OnInit {

  users: UserProfile[] = [];
  filteredUsers: UserProfile[] = [];
  searchTerm: string = '';

  showModal: boolean = false;
  isEditMode: boolean = false;
  currentUser: UserFormData = {
    userId: 0,
    name: '',
    email: '',
    password: '',
    phoneNumber: '',
    shippingAddress: ''
  };

  userRole: string | null = null;

  constructor(
    private userService: UsersService ,
    private commonService: CommonService,
    private registerService: RegisterService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.userRole = this.commonService.role;

    if (this.userRole === 'ADMIN') {
      this.fetchAllUsers();
    } else {
      alert('Access Denied: Only administrators can manage users.');
      this.router.navigate(['/home']);
    }
  }

  fetchAllUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (data: UserProfile[]) => {
        this.users = data;
        this.filterUsers();
        console.log('Fetched all users:', this.users);
      },
      error: (err) => {
        console.error('Error fetching users:', err);
        alert('Failed to load users. Please try again.');
        this.users = [];
        this.filteredUsers = [];
      }
    });
  }

  filterUsers(): void {
    const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.name.toLowerCase().includes(lowerCaseSearchTerm) ||
      user.email.toLowerCase().includes(lowerCaseSearchTerm) ||
      user.phoneNumber.includes(lowerCaseSearchTerm) ||
      user.shippingAddress.toLowerCase().includes(lowerCaseSearchTerm)
    );
  }

  openUserModal(user?: UserProfile): void {
    if (user) {
      this.isEditMode = true;
      this.currentUser = {
        userId: user.userId,
        name: user.name,
        email: user.email,
        password: '', // **CHANGE: Always clear password for edit mode - it won't be shown anyway**
        phoneNumber: user.phoneNumber,
        shippingAddress: user.shippingAddress
      };
    } else {
      this.isEditMode = false;
      this.currentUser = {
        userId: 0,
        name: '',
        email: '',
        password: '', // **CHANGE: Ensure password is empty for new user**
        phoneNumber: '',
        shippingAddress: ''
      };
    }
    this.showModal = true;
  }

  closeUserModal(): void {
    this.showModal = false;
    this.currentUser = {
      userId: 0,
      name: '',
      email: '',
      password: '',
      phoneNumber: '',
      shippingAddress: ''
    };
  }

  closeModalOnBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal')) {
      this.closeUserModal();
    }
  }

  saveOrUpdateUser(): void {
    if (this.isEditMode) {
      // Logic for UPDATING an existing user - only profile details
      const userProfileToUpdate: UserProfile = {
        userId: this.currentUser.userId!,
        name: this.currentUser.name,
        email: this.currentUser.email,
        phoneNumber: this.currentUser.phoneNumber,
        shippingAddress: this.currentUser.shippingAddress
      };

      // **CHANGE: Removed password update logic entirely for edit mode**
      // Admins will not be able to edit passwords via this modal.
      // Password reset should be a separate, user-initiated or dedicated admin action.

      this.userService.updateUser(userProfileToUpdate).subscribe({
        next: (updatedUser: UserProfile) => {
          alert('User profile updated successfully!');
          console.log('Updated user profile:', updatedUser);
          this.closeUserModal();
          this.fetchAllUsers();
        },
        error: (err) => {
          console.error('Error updating user profile:', err);
          const errorMessage = err.error?.message || err.error?.details || 'Failed to update user profile.';
          alert('Failed to update user: ' + errorMessage);
        }
      });
    } else {
      // Logic for ADDING a new user (calls both auth/new and user/save)
      if (!this.currentUser.password || this.currentUser.password.length === 0) {
        alert('Password is required for new customers.');
        return;
      }

      const registerUser: RegisterUser = {
        name: this.currentUser.name,
        email: this.currentUser.email,
        password: this.currentUser.password,
        role: 'USER' // **This role is being sent from frontend. Verify backend processing.**
      };

      const userProfile: UserProfile = {
        userId: 0,
        name: this.currentUser.name,
        email: this.currentUser.email,
        phoneNumber: this.currentUser.phoneNumber,
        shippingAddress: this.currentUser.shippingAddress
      };

      this.registerService.registerBoth1({ ...registerUser, ...userProfile as any }).subscribe({
        next: (response: string) => {
          alert('New customer added successfully!');
          console.log('New customer registration response:', response);
          this.closeUserModal();
          this.fetchAllUsers();
        },
        error: (err) => {
          console.error('Error adding new customer:', err);
          let errorMessage = 'Failed to add new customer. Please try again.';
          if (err.status === 409) {
            errorMessage = 'User with this name or email already exists.';
          } else if (err.error?.message) {
            errorMessage = err.error.message;
          } else if (err.message) {
            errorMessage = err.message;
          }
          alert(errorMessage);
        }
      });
    }
  }

  editUser(user: UserProfile): void {
    this.openUserModal(user);
  }

  deleteUser(userId: number): void {
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
      console.warn('Full user deletion (UserInfo and UserProfile) needs backend coordination.');

      this.userService.deleteUser(userId).subscribe({
        next: (response: string) => {
          alert(response);
          console.log('Deleted user profile response:', response);
          this.fetchAllUsers();
        },
        error: (err) => {
          console.error('Error deleting user profile:', err);
          const errorMessage = err.error?.message || err.error?.details || 'Failed to delete user.';
          alert('Failed to delete user: ' + errorMessage);
        }
      });
    }
  }
}