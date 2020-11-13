import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Scheduling from 'src/models/scheduling.model';
import { DateService } from './date.service';
import { saveAs } from 'file-saver';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class SchedulingService {

    constructor(private http: HttpClient, private dateService: DateService, private authService: AuthService) { }

    getSchedulings(params?: any): Observable<Scheduling[]> {
        return this.http.get<Scheduling[]>(`${environment.apiUrl}/scheduling`, { params });
    }
    getFullScheduleByRoom(params: any): Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/scheduling/sala`, { params });
    }

    getAssistantsSchedules(params: any): Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/scheduling/horariosAssistentes`, { params });
    }

    getCredit(professionalId: any): Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/scheduling/creditos/${professionalId}`);
    }

    validDateForReservation(date: Date): boolean {
        if (this.dateService.previousCurrentServerDate(date)) {
            return false
        }
        return this.validDateForScheduling(date)
    }

    getListaEquipamentos(id: number | string): Observable<any[]> {
        return this.http.get<any>(environment.apiUrl + '/scheduling/listarEquipamentos', { params: { agendamento: id.toString() } })
    }
    getSchegulingList(params?: any): Observable<any[]> {
        return this.http.get<any[]>(`${environment.apiUrl}/scheduling/listarAgendamentos`, { params })
    }
    getProfessionalSchedule(params?: any): Observable<any[]> {
        return this.http.get<any[]>(`${environment.apiUrl}/scheduling/listarAgendamentosMedico`, { params })
    }

    increaseSchedulingPeriod(increaseSchedulingPeriod: any) {
        return this.http.post<any>(`${environment.apiUrl}/scheduling/alterarAgendamento`, increaseSchedulingPeriod)
    }
    validDateForScheduling(date: Date): boolean {
        const hours = date.getHours()
        if (date.getDay() === 6 && hours >= 12) {
            return false
        }
        if ((hours >= 0 && hours < 8) || hours === 12 || hours === 17 || hours >= 22) {
            return false
        }
        return true
    }
    deleteAssistantSchedule(assistantSchedule: any) {
        return this.http.post<any>(`${environment.apiUrl}/scheduling/excluirAgendaAssistente`, assistantSchedule)
    }
    getAssistantScheduleList(params: any) {
        return this.http.get<any[]>(`${environment.apiUrl}/assistants_scheduling/list`, { params })
    }
    cancelScheduling(scheduling: any) {
        return this.http.post<any>(`${environment.apiUrl}/scheduling/cancelarAgendamento`, scheduling)
    }
    cancelSchedulingReservation(scheduling: any) {
        return this.http.post<any>(`${environment.apiUrl}/scheduling/cancelarSolicitacaoAgendamento`, scheduling)
    }
    clearCart() {
        return this.http.delete(`${environment.apiUrl}/scheduling/apagarReservas`)
    }
    clearCartAttendant(id: any) {
        return this.http.delete(`${environment.apiUrl}/scheduling/apagarReservasPorProfissional/${id}`)
    }
    finishSale(finishSaleForm: any) {
        return this.http.post(`${environment.apiUrl}/scheduling/finalizarCarrinho`, finishSaleForm)
    }

    addEquipments(form: any) {
        return this.http.post(`${environment.apiUrl}/scheduling/adicionarEquipamentos`, form)
    }
    getDaysUnavailable(params: any) {
        return this.http.get<any[]>(`${environment.apiUrl}/scheduling/diasIndisponiveis`, { params })

    }

    getReportProfessionalScheduling(params: any) {
        return this.http.get<any>(`${environment.apiUrl}/scheduling/pesquisar-agendamento-profissional`, { params })
    }

    getReportProfessional(specialityId: any) {
        return this.http.get<any>(`${environment.apiUrl}/professionals/pesquisar-profissionais/${specialityId}`)
    }
    // downloadReportProfessionalScheduling(params: any) {
    //     // const headers: any = new HttpHeaders({
    //     //     responseType: 'arraybuffer'
    //     // })
    //     return this.http.get(`${environment.apiUrl}/scheduling/export-agendamento-profissional`, { params })
    // }
    async downloadReportProfessionalScheduling(params: any) {
        const myHeaders = new Headers()
        myHeaders.append("Authorization", `Bearer ${this.authService.getToken()}`)

        return fetch(`${environment.apiUrl}/scheduling/export-agendamento-profissional?startDate=${params.startDate}&endDate=${params.endDate}&professionalId=${params.professionalId}&cpf=${params.cpf}`, {
            method: 'GET',
            headers: myHeaders,
            redirect: 'follow'
        })

    }
    // downloadReportProfessional(specialityId: any) {
    //     return this.http.get(`${environment.apiUrl}/professionals/export-profissionais?especialidadeId=${specialityId}`)
    // }
    async downloadReportProfessional(specialityId: any) {
        const myHeaders = new Headers()
        myHeaders.append("Authorization", `Bearer ${this.authService.getToken()}`)

        return fetch(`${environment.apiUrl}/professionals/export-profissionais/?especialidadeId=${specialityId}`, {
            method: 'GET',
            headers: myHeaders,
            redirect: 'follow'
        })

    }
    async downloadSchedulingOfDay() {
        const myHeaders = new Headers()
        myHeaders.append("Authorization", `Bearer ${this.authService.getToken()}`)

        return fetch(`${environment.apiUrl}/scheduling/export-schedules-of-day`, {
            method: 'GET',
            headers: myHeaders,
            redirect: 'follow'
        })

    }
    cancelEquipamento(cancelEquipment: any) {
        return this.http.post(`${environment.apiUrl}/scheduling/cancelarEquipamentos`, cancelEquipment)
    }

    getSchegulingDayList(): Observable<any[]> {
        return this.http.get<any[]>(`${environment.apiUrl}/scheduling/schedules-of-day`)
    }

    getSchegulingDay(id: string | number): Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/scheduling/schedules-of-day/${id}`)
    }

    schedulingMaintenance(increaseSchedulingPeriod: any) {
        return this.http.post<any>(`${environment.apiUrl}/scheduling/agendarManutencao`, increaseSchedulingPeriod)
    }

}


