import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class OrdersService {

  private orderApiUrl = 'http://localhost:9090/order';

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

  placeOrder(userId: number, orderData: any): Observable<string> {
    return this.http.post(`${this.orderApiUrl}/placeOrderByUserId/${userId}`, orderData, { headers: this.getAuthHeaders(), responseType: 'text' });
  }

  updateOrder(order: any): Observable<any> {
    return this.http.put<any>(`${this.orderApiUrl}/update`, order, { headers: this.getAuthHeaders() });
  }

  getOrdersByUserId(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.orderApiUrl}/byUser/${userId}`, { headers: this.getAuthHeaders() });
  }

  getAllOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.orderApiUrl}/fetchAll`, { headers: this.getAuthHeaders() });
  }
}
