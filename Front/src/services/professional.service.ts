import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Professional from 'src/models/professional.model';
import { AuthService } from './auth.service';
import { CreateAppointmentModel } from 'src/models/create-appointment.model';
import Appointment from 'src/models/appointment-model';
import { env } from 'process';

@Injectable({
    providedIn: 'root'
})
export class ProfessionalService {

    constructor(private http: HttpClient, private authService: AuthService) { }

    getProfessionals(): Observable<Professional[]> {
        return this.http.get<Professional[]>(`${environment.apiUrl}/professionals`);
    }
    getProfessionalsWithDiscountAttribute(): Observable<Professional[]> {
        return this.http.get<Professional[]>(`${environment.apiUrl}/professionals/withHasDiscounts`);
    }
    getProfessionalById(id: string | number): Observable<Professional> {
        return this.http.get<Professional>(`${environment.apiUrl}/professionals/${id}`);
    }
    getMe() {
        return this.http.get<Professional>(`${environment.apiUrl}/professionals/me`);
    }
    createProfessional(professional: Professional): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/professionals`, professional);
    }
    updateProfessional(id: string | number, professional: Professional): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/professionals/${id}`, professional);
    }
    updateMe(professional: Professional): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/professionals/me`, professional);
    }
    deleteProfessional(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/professionals/${id}`);
    }
    approveProfessional(id: string | number): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/professionals/approve-professional/${id}`, {});
    }
    reproveProfessional(id: string | number): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/professionals/reprove-professional/${id}`, {});
    }
    getCurrentScheduling() {
        return this.http.get<any>(`${environment.apiUrl}/professionals/current-scheduling`)
    }
    submitProfessionalCheckin(checkin: any): Observable<any[]> {
        return this.http.post<any>(`${environment.apiUrl}/checkin-scheduling/professional/checkin`, checkin);
    }
    submitProfessionalCheckout(checkout: any): Observable<any[]> {
        return this.http.post<any>(`${environment.apiUrl}/checkin-scheduling/professional/checkout`, checkout);
    }
    getTermsOfUse(): Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/professionals/terms-of-use`)
    }
    getProfessionalByUrl(url: string | number): Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/professionals/url/${url}`);
    }

    getDownloadPdfTerms(id: string | number): Observable<Blob> {
        return this.http.get(`${environment.apiUrl}/pdf/export/${id}`, { responseType: 'blob'});
    }

    createMedicalAppointment(professionalId: number, appointment: CreateAppointmentModel): Observable<Appointment> {
        return this.http.post<Appointment>(`${environment.apiUrl}/professionals/${professionalId}/medical-appointments`,
            appointment)
    }

    getMyAppointments(searchParams: any): Observable<Appointment[]> {
        return this.http.get<Appointment[]>(`${environment.apiUrl}/professionals/me/medical-appointments`, {
            params: searchParams
        })
    }

  /*   async getDownloadPdfTerms() {
        const myHeaders = new Headers()
        myHeaders.append("Authorization", `Bearer ${this.authService.getToken()}`)

        return fetch(`${environment.apiUrl}/pdf/export`, {
            method: 'GET',
            headers: myHeaders,
            redirect: 'follow'
        })

    } */
}
