import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Price from 'src/models/price.model';

@Injectable({
    providedIn: 'root'
})
export class PriceService {

    constructor(private http: HttpClient) { }

    getPrices(): Observable<Price[]> {
        return this.http.get<Price[]>(`${environment.apiUrl}/prices-list`);
    }
    getPriceById(id: string | number): Observable<Price> {
        return this.http.get<Price>(`${environment.apiUrl}/prices-list/${id}`);
    }
    createPrice(price: Price): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/prices-list`, price);
    }
    updatePrice(id: string | number, price: Price): Observable<any> {       
        return this.http.put<any>(`${environment.apiUrl}/prices-list/${id}`, price);
    }
    deletePrice(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/prices-list/${id}`);
    }
}
