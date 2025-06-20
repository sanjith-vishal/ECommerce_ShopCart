import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonService } from '../common.service';


interface Deal {
  title: string;
  description: string;
  imageUrl: string;
}

@Component({
  selector: 'app-home-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit{
  userName: string = 'User';
  userRole: string = '';

  constructor(private commonService: CommonService) {}

  ngOnInit(): void {
    this.userName = this.commonService.username || 'Guest'; // Get username from CommonService
    this.userRole = this.commonService.role || ''; // Get role from CommonService
    console.log(`Home Page Loaded: User Name: ${this.userName}, Role: ${this.userRole}`); // For debugging
  }
  
}
