import { CommonModule, CurrencyPipe, NgFor } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CommonService } from '../common.service';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Renderer2, ElementRef, ViewChild } from '@angular/core'; // For manual modal control
import { ProductsService } from '../products.service';
import { forkJoin, Observable } from 'rxjs'; // Import forkJoin for combining observables
import { CartService } from '../cart.service';

interface Toast {
  id: number;
  message: string;
  type: string; // e.g., 'success', 'danger', 'warning', 'info'
}

@Component({
  selector: 'app-products',
  imports: [CommonModule, NgFor, HttpClientModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit{

  products: any[] = []; // This will hold the "master" list of products after initial fetch
  filteredProducts: any[] = []; // This will hold the products currently displayed after all filters/sorts
  role:string = '';
  loggedInUserId: number | null = null; 

  // Filter/Sort/Search variables
  selectedCategory: string = '';
  selectedSortOption: string = ''; // Renamed from selectedPriceSort
  searchTerm: string = '';
  minPrice: number | null = null; // New: for price range filter
  maxPrice: number | null = null; // New: for price range filter

  // Toast properties
  toasts: Toast[] = [];
  private nextToastId: number = 0;


  // Admin specific for modal and CRUD operations
  showModal: boolean = false; // Controls visibility of the custom modal
  isEditMode: boolean = false;
  currentProduct: any = { // Initialize with default values for new product
    productId: 0,
    productName: '',
    description: '',
    price: 0,
    category: '',
    imageURL: '',
    quantity: 0
  };

  constructor(private commonService: CommonService, private productsService: ProductsService, private cartService: CartService, private renderer: Renderer2) { }

  ngOnInit(): void {
    this.role = this.commonService.role;
    this.loggedInUserId = this.commonService.getLoggedInUserId();
    this.fetchAllProducts();
  }

  // --- Modal Management (Manual Bootstrap Modal) ---
  openProductModal(): void {
    this.isEditMode = false;
    this.currentProduct = {
      productId: 0,
      productName: '',
      description: '',
      price: 0,
      category: '',
      imageURL: '',
      quantity: 0
    };
    this.showModal = true;
    this.renderer.addClass(document.body, 'modal-open');
  }

  editProduct(product: any): void {
    this.isEditMode = true;
    this.currentProduct = { ...product }; // Create a deep copy
    this.showModal = true;
    this.renderer.addClass(document.body, 'modal-open');
  }

  closeProductModal(): void {
    this.showModal = false;
    this.renderer.removeClass(document.body, 'modal-open');
  }

  closeModalOnBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal')) {
      this.closeProductModal();
    }
  }

  // --- Toast Management ---
  showToast(type: string, message: string): void {
    const id = this.nextToastId++;
    this.toasts.push({ id, message, type });

    // Automatically remove the toast after 3 seconds
    setTimeout(() => {
      this.removeToast(id);
    }, 3000);
  }

  removeToast(id: number): void {
    this.toasts = this.toasts.filter(toast => toast.id !== id);
  }


  // --- Product Fetching and Initial Filtering/Sorting ---
  fetchAllProducts(): void {
    let observables: Observable<any>[] = [];

    if (this.selectedCategory) {
      observables.push(this.productsService.getProductsByCategory(this.selectedCategory));
    } else if (this.minPrice !== null && this.maxPrice !== null && this.minPrice >= 0 && this.maxPrice >= 0 && this.minPrice <= this.maxPrice) {
      observables.push(this.productsService.getProductsByPriceRange(this.minPrice, this.maxPrice));
    } else {
      observables.push(this.productsService.getAllProducts());
    }

    observables[0].subscribe({
      next: (data: any[]) => {
        this.products = data;
        this.applyFiltersAndSort();
      },
      error: (err) => {
        console.error('Error fetching products:', err);
        this.showToast('danger', 'Failed to fetch products. Please try again later.'); // Using toast
        this.products = [];
        this.filteredProducts = [];
      }
    });
  }

  filterProducts(): void {
    this.minPrice = null;
    this.maxPrice = null;
    this.fetchAllProducts();
  }

  filterByPriceRange(): void {
    if ((this.minPrice !== null && this.maxPrice !== null) && (this.minPrice < 0 || this.maxPrice < 0 || this.minPrice > this.maxPrice)) {
      this.showToast('warning', 'Please enter a valid price range (Min Price should be less than or equal to Max Price, and both should be non-negative).'); // Using toast
      return;
    }
    this.selectedCategory = '';
    this.fetchAllProducts();
  }

  applyFiltersAndSort(): void {
    let tempProducts = [...this.products];

    if (this.searchTerm) {
      const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
      tempProducts = tempProducts.filter(product =>
        product.productName.toLowerCase().includes(lowerCaseSearchTerm) ||
        product.category.toLowerCase().includes(lowerCaseSearchTerm)
      );
    }

    if (this.selectedSortOption === 'low-to-high') {
      tempProducts.sort((a, b) => a.price - b.price);
    } else if (this.selectedSortOption === 'high-to-low') {
      tempProducts.sort((a, b) => b.price - a.price);
    }

    this.filteredProducts = tempProducts;
  }

  // --- Cart Functionality ---
  addToCart(product: any): void {
    if (this.loggedInUserId === null) {
      this.showToast('warning', 'Please log in to add items to your cart.'); // Using toast
      return;
    }

    if (product.quantity === 0) {
      this.showToast('warning', 'This product is out of stock.'); // Using toast
      return;
    }

    const cartItemData = {
      userId: this.loggedInUserId,
      productId: product.productId,
      quantity: 1
    };

    this.cartService.addToCart(cartItemData).subscribe({
      next: (res: string) => {
        this.showToast('success', res); // Using toast
        console.log('Add to cart response:', res);
      },
      error: (err) => {
        console.error('Error adding to cart:', err);
        this.showToast('danger', 'Failed to add to cart: ' + (err.error?.message || err.message || 'Server error.')); // Using toast
      }
    });
  }

  // --- Admin Actions (Create, Update, Delete) ---
  saveOrUpdateProduct(): void {
    if (this.isEditMode) {
      this.productsService.updateProduct(this.currentProduct).subscribe({
        next: (res: any) => {
          console.log('Product updated:', res);
          this.closeProductModal();
          this.fetchAllProducts();
          this.showToast('success', 'Product updated successfully!'); // Using toast
        },
        error: (err) => {
          console.error('Error updating product:', err);
          this.showToast('danger', 'Failed to update product: ' + (err.error?.message || err.message)); // Using toast
        }
      });
    } else {
      const productToSave = { ...this.currentProduct };
      if (productToSave.productId === 0) {
        delete productToSave.productId;
      }

      this.productsService.saveProduct(productToSave).subscribe({
        next: (res: string) => {
          console.log('Product added:', res);
          this.closeProductModal();
          this.fetchAllProducts();
          this.showToast('success', 'Product added successfully!'); // Using toast
        },
        error: (err) => {
          console.error('Error adding product:', err);
          this.showToast('danger', 'Failed to add product: ' + (err.error?.message || err.message)); // Using toast
        }
      });
    }
  }

  deleteProduct(productId: number): void {
    if (confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
      this.productsService.deleteProduct(productId).subscribe({
        next: (res: string) => {
          console.log('Product deleted:', res);
          this.fetchAllProducts();
          this.showToast('success', 'Product deleted successfully!'); // Using toast
        },
        error: (err) => {
          console.error('Error deleting product:', err);
          this.showToast('danger', 'Failed to delete product: ' + (err.error?.message || err.message)); // Using toast
        }
      });
    }
  }
}