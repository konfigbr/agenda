import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { format } from 'date-fns';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root'
})
export class InvoicesService {

    constructor(private http: HttpClient) { }

    getInvoicesByProfessional(professionalId: string | number): Observable<any[]> {
        return this.http.get<any[]>(`${environment.apiUrl}/invoices/professionals/${professionalId}`);
    }
    createReservation(reservation: any) {
        reservation.dataInicio = format(reservation.dataInicio, 'yyyy-MM-dd')
        reservation.dataFim = (reservation.dataFim) ? format(reservation.dataFim, 'yyyy-MM-dd') : null
        reservation.horaInicio = format(reservation.horaInicio, 'HH:mm:ss')
        reservation.horaFim = format(reservation.horaFim, 'HH:mm:ss')
        return this.http.post<any>(`${environment.apiUrl}/scheduling/reservar`, reservation)
    }
    getInvoiceDetail(invoiceId: string | number) {
        return this.http.get<any>(`${environment.apiUrl}/invoices/detailInvoice/${invoiceId}`);
    }

}
