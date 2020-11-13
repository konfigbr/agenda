import { CreateAppointmentModel } from './../models/create-appointment.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AppointmentHour } from 'src/models/appointment-hour-model';
import Appointment from 'src/models/appointment-model';

@Injectable({
    providedIn: 'root'
})
export class ConsultaService {

    constructor(private http: HttpClient) { }

    getAppointments(searchParams: any): Observable<Appointment[]> {
        return this.http.get<Appointment[]>(`${environment.apiUrl}/medical-appointments`, {
            params: searchParams
        })
    }

    getBusyTimes(professionalId: number, startDate: string, endDate: string): Observable<AppointmentHour[]> {
        return this.http.get<AppointmentHour[]>(`${environment.apiUrl}/medical-appointments/busyTimes`, { params: {
            idProfissional: String(professionalId),
            inicio: startDate,
            fim: endDate,
        }})
    }
    deleteAppointment(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/medical-appointments/cancelAppointment/${id}`);
    }

    updateStatus(id: string | number, status: any): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/medical-appointments/changeStatus/${id}`, status);
    }

    getMyAppointments(id: string | number, idPaciente: string | number = null): Observable<Appointment[]> {
        let params = new HttpParams();

        if (idPaciente)
            params = params.append('idPaciente', idPaciente.toString());

        return this.http.get<Appointment[]>(`${environment.apiUrl}/medical-appointments/professional/${id}`, { params });
    }

    getStatusRequestedProfessional(): Observable<Appointment[]> {
        return this.http.get<Appointment[]>(`${environment.apiUrl}/medical-appointments/statusRequested/me`);
    }

    getStatusRequested(): Observable<Appointment[]> {
        return this.http.get<Appointment[]>(`${environment.apiUrl}/medical-appointments/statusRequested`);
    }
    getWithLimit(): Observable<Appointment[]> {
        return this.http.get<Appointment[]>(`${environment.apiUrl}/medical-appointments/with-limit`);
    }
    getWithLimitProf(): Observable<Appointment[]> {
        return this.http.get<Appointment[]>(`${environment.apiUrl}/medical-appointments/with-limit/professional`);
    }

    getAvailableTimes(professionalId: number, startDate: string, endDate: string): Observable<AppointmentHour[]> {
        return this.http.get<AppointmentHour[]>(`${environment.apiUrl}/medical-appointments/availableTimes`, {
            params: {
                idProfissional: String(professionalId),
                inicio: startDate,
                fim: endDate,
            }
        })
    }

    rescheulingMedicalAppointment(idConsulta: number, appointment: CreateAppointmentModel): Observable<Appointment> {
        return this.http.put<Appointment>(`${environment.apiUrl}/medical-appointments/rescheduling/${idConsulta}`,
            appointment)
    }

    createMedicalAppointmentWithPatient(professionalId: number, appointment: CreateAppointmentModel): Observable<Appointment> {
        return this.http.post<Appointment>(`${environment.apiUrl}/medical-appointments/${professionalId}/with-patient`,
            appointment)
    }

}
