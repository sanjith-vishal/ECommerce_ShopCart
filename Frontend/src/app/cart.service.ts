import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private apiUrl = 'http://localhost:9090/cart';

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

  addToCart(cartItem: { userId: number, productId: number, quantity: number }): Observable<string> {
    return this.http.post(`${this.apiUrl}/add`, cartItem, { headers: this.getAuthHeaders(), responseType: 'text' });
  }

  getCartItemsByUserId(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cartItems/withProducts/${userId}`, { headers: this.getAuthHeaders() });
  }

  // Update the quantity of a cart item
  updateCartItem(cartItem: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/update`, cartItem, { headers: this.getAuthHeaders() });
  }

  removeItemFromCart(cartItemId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/delete/${cartItemId}`, { headers: this.getAuthHeaders(), responseType: 'text' });
  }

  clearCartByUserId(userId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/clearByUserId/${userId}`, { headers: this.getAuthHeaders(), responseType: 'text' });
  }


}
