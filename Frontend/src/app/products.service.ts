import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  private apiUrl = 'http://localhost:9090/product';

  constructor(private http: HttpClient) { }

  // Helper method to get headers with JWT token directly from localStorage
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

  getAllProducts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/fetchAll`, { headers: this.getAuthHeaders() });
  }

  getProductsByCategory(category: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/category/${category}`, { headers: this.getAuthHeaders() });
  }

  getProductsByPriceRange(min: number, max: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/price-range/${min}/${max}`, { headers: this.getAuthHeaders() });
  }

  saveProduct(product: any): Observable<string> {
    return this.http.post(`${this.apiUrl}/save`, product, { headers: this.getAuthHeaders(), responseType: 'text' });
  }

  updateProduct(product: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/update`, product, { headers: this.getAuthHeaders() });
  }

  deleteProduct(productId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/delete/${productId}`, { headers: this.getAuthHeaders(), responseType: 'text' });
  }

  getProductById(productId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/fetchById/${productId}`, { headers: this.getAuthHeaders() });
  }
}