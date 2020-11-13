import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Patient from 'src/models/patient.model';
import { AuthService } from './auth.service';
import { DateService } from './date.service';

@Injectable({
    providedIn: 'root'
})
export class PatientService {

    constructor(private http: HttpClient, private dateService: DateService, private authService: AuthService) { }

    getPatients(): Observable<Patient[]> {
        return this.http.get<Patient[]>(`${environment.apiUrl}/patient`);
    }
    getMe(): Observable<Patient> {
        return this.http.get<Patient>(`${environment.apiUrl}/patient/me`);
    }
    getPatientId(id: string | number): Observable<Patient> {
        return this.http.get<Patient>(`${environment.apiUrl}/patient/${id}`);
    }
    createPatient(patient: Patient): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/patient`, patient);
    }
    updatePatient(id: string | number, patient: Patient): Observable<any> {       
        return this.http.put<any>(`${environment.apiUrl}/patient/${id}`, patient);
    }
    deletePatient(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/patient/${id}`);
    }
}
