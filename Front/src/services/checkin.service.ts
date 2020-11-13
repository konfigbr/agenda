import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { format } from 'date-fns';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root'
})
export class CheckinService {

    constructor(private http: HttpClient) { }

    submitAssistantCheckin(checkin: any): Observable<any[]> {
        return this.http.post<any>(`${environment.apiUrl}/checkin-scheduling/assistant/checkin`, checkin);
    }
    submitAssistantCheckout(checkout: any): Observable<any[]> {
        return this.http.post<any>(`${environment.apiUrl}/checkin-scheduling/assistant/checkout`, checkout);
    }
    submitAssistantCheckinProfessional(checkin: any): Observable<any[]> {
        return this.http.post<any>(`${environment.apiUrl}/checkin-scheduling/assistant/checkin-professional`, checkin);
    }
    submitAssistantCheckoutProfessional(checkout: any): Observable<any[]> {
        return this.http.post<any>(`${environment.apiUrl}/checkin-scheduling/assistant/checkout-professional`, checkout);
    }
}
