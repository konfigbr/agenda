import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Specialty from 'src/models/specialty.model';
import Price from 'src/models/price.model';

@Injectable({
    providedIn: 'root'
})
export class SpecialtyService {

    constructor(private http: HttpClient) { }

    getSpecialties(): Observable<Specialty[]> {
        return this.http.get<Specialty[]>(`${environment.apiUrl}/specialties`);
    }
    getProfessionalSpecialties(): Observable<Specialty[]> {
        return this.http.get<Specialty[]>(`${environment.apiUrl}/specialties/professionals`);
    }
    getSpecialtyById(id: string | number): Observable<Specialty> {
        return this.http.get<Specialty>(`${environment.apiUrl}/specialties/${id}`);
    }
    createSpecialty(specialty: Specialty): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/specialties`, specialty);
    }
    updateSpecialty(id: string | number, specialty: Specialty): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/specialties/${id}`, specialty);
    }
    deleteSpecialty(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/specialties/${id}`);
    }
    getPriceTables(): Observable<Price[]> {
        return this.http.get<Price[]>(`${environment.apiUrl}/prices-list`);
    }
}
