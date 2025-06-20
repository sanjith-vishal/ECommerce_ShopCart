import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonService } from '../common.service';
import { UsersService, UserProfile, AdminDetails } from '../users.service'; // Import AdminDetails here

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  // Use 'any' to dynamically hold either UserProfile or AdminDetails
  currentUserProfile: any = {}; // Initialize as empty object

  isUser: boolean = false; // Flag to indicate if current user is a 'USER'
  isAdmin: boolean = false; // Flag to indicate if current user is an 'ADMIN'

  constructor(
    private router: Router,
    private commonService: CommonService,
    private usersService: UsersService // Renamed from usersService for consistency
  ) { }

  ngOnInit(): void {
    const loggedInId = this.commonService.getLoggedInUserId(); // This could be userId or adminId
    const role = this.commonService.role; // Get the role

    if (!loggedInId || !role) {
      alert('You are not logged in or role is missing. Please login to view your profile.');
      this.router.navigate(['/login']);
      return;
    }

    // Set flags based on role
    this.isUser = role === 'USER';
    this.isAdmin = role === 'ADMIN';

    this.fetchUserProfile(loggedInId, role);
  }

  fetchUserProfile(id: number, role: string): void {
    if (role === 'USER') {
      this.usersService.getUserById(id).subscribe({
        next: (data: UserProfile) => {
          this.currentUserProfile = data;
          console.log('Fetched user profile:', this.currentUserProfile);
        },
        error: (err) => {
          console.error('Error fetching user profile:', err);
          alert('Failed to load your user profile. Please try again.');
          this.router.navigate(['/home']);
        }
      });
    } else if (role === 'ADMIN') {
      this.usersService.getAdminById(id).subscribe({ // Call the new getAdminById
        next: (data: AdminDetails) => {
          this.currentUserProfile = data;
          console.log('Fetched admin profile:', this.currentUserProfile);
        },
        error: (err) => {
          console.error('Error fetching admin profile:', err);
          alert('Failed to load your admin profile. Please try again.');
          this.router.navigate(['/home']);
        }
      });
    } else {
      alert('Unknown role. Cannot fetch profile.');
      this.router.navigate(['/home']);
    }
  }

  saveProfile(): void {
    // Check if the profile is valid and the appropriate ID exists
    const idToUpdate = this.isUser ? this.currentUserProfile.userId : this.currentUserProfile.adminId;

    if (!idToUpdate) {
      alert('Cannot update profile: User/Admin ID is missing.');
      return;
    }

    if (this.isUser) {
      // Cast to UserProfile for type safety during update, though 'any' is used for display
      const userProfileToSave: UserProfile = {
        userId: this.currentUserProfile.userId,
        name: this.currentUserProfile.name,
        email: this.currentUserProfile.email,
        phoneNumber: this.currentUserProfile.phoneNumber,
        shippingAddress: this.currentUserProfile.shippingAddress
      };
      this.usersService.updateUser(userProfileToSave).subscribe({
        next: (updatedUser: UserProfile) => {
          alert('User profile updated successfully!');
          console.log('Updated user profile:', updatedUser);
          this.updateCommonUsername(updatedUser.name);
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Error updating user profile:', err);
          const errorMessage = err.error?.message || err.error?.details || 'Failed to update user profile.';
          alert('Failed to update user profile: ' + errorMessage);
        }
      });
    } else if (this.isAdmin) {
      // Cast to AdminDetails for type safety during update
      const adminProfileToSave: AdminDetails = {
        adminId: this.currentUserProfile.adminId, // Ensure adminId is used
        name: this.currentUserProfile.name,
        email: this.currentUserProfile.email,
        phoneNumber: this.currentUserProfile.phoneNumber
      };
      this.usersService.updateAdmin(adminProfileToSave).subscribe({ // Call the new updateAdmin
        next: (updatedAdmin: AdminDetails) => {
          alert('Admin profile updated successfully!');
          console.log('Updated admin profile:', updatedAdmin);
          this.updateCommonUsername(updatedAdmin.name);
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Error updating admin profile:', err);
          const errorMessage = err.error?.message || err.error?.details || 'Failed to update admin profile.';
          alert('Failed to update admin profile: ' + errorMessage);
        }
      });
    }
  }

  private updateCommonUsername(newName: string): void {
    if (this.commonService.username !== newName) {
      this.commonService.username = newName;
      localStorage.setItem('username', newName);
    }
  }
}