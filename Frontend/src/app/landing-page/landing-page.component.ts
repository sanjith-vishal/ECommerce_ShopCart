import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core'; // Import OnInit
import { Router, RouterLink } from '@angular/router';
import { CommonService } from '../common.service'; // Ensure this path is correct

interface Product {
  name: string;
  price: number;
  imageUrl: string;
}

@Component({
  selector: 'app-landing-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent implements OnInit { // Implement OnInit
  // Sample data for featured products
  featuredProducts: Product[] = []; // Explicitly type as Product[]
  currentYear: number = new Date().getFullYear();

  constructor(public router: Router, public common: CommonService) { } 

  ngOnInit(): void {
    this.featuredProducts = [
      {
        name: 'Wireless Bluetooth Headphones',
        price: 3499.00,
        imageUrl: '/bt_hp.png' // Consider using /assets/images/ for better organization
      },
      {
        name: 'Smartwatch with Heart Rate Monitor',
        price: 8999.00,
        imageUrl: '/smart_watch.png'
      },
      {
        name: 'High-Performance Laptop',
        price: 65000.00,
        imageUrl: '/laptop.png'
      },
      {
        name: 'Designer Leather Handbag',
        price: 12500.00,
        imageUrl: '/handbag.png'
      },
      {
        name: 'Organic Coffee Beans (1 Kg)',
        price: 950.00,
        imageUrl: '/coffee_beans.png'
      },
      {
        name: 'Professional Camera Lens',
        price: 28000.00,
        imageUrl: '/camera_lens.png'
      }
    ];
  }
}