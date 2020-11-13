import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Discount from 'src/models/discount-model';

@Injectable({
    providedIn: 'root'
})


export class DiscountService {

    constructor(private http: HttpClient) { }

    getAllDiscountsByProfessional(idProfissional: any): Observable<Discount[]> {
        return this.http.get<Discount[]>(`${environment.apiUrl}/discounts/profissional/${idProfissional}`, {
            params: idProfissional
        })
    }
    getCurrentDiscounts(discount: Discount): Observable<Discount> {
        return this.http.post<Discount>(`${environment.apiUrl}/discounts/current/find`, discount)

    }
    getCurrentDiscountsPeriod(idProfissional: any): Observable<Discount[]> {
        return this.http.get<Discount[]>(`${environment.apiUrl}/discounts/current/profissional/${idProfissional}`, {
           
        })

    }
    getCurrentDiscountsByProfessional(idProfissional: any): Observable<Discount[]> {
        return this.http.get<Discount[]>(`${environment.apiUrl}/discounts/current/profissional/${idProfissional}`, {
            params: idProfissional
        })
    }


    deleteDiscount(idDesconto: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/discounts/cancel/${idDesconto}`);
    }

    updateDiscount(id: string | number, discount: Discount) : Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/discounts/${id}`, discount);
    }
    createDiscount(discount: Discount): Observable<Discount> {
        return this.http.post<Discount>(`${environment.apiUrl}/discounts`,
            discount)
    }





}