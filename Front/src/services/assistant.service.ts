import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Assistant from 'src/models/assistant.model';

@Injectable({
    providedIn: 'root'
})
export class AssistantService {

    constructor(private http: HttpClient) { }

    getAssistants(): Observable<Assistant[]> {
        return this.http.get<Assistant[]>(`${environment.apiUrl}/assistants`);
    }
    getAssistantById(id: string | number): Observable<Assistant> {
        return this.http.get<Assistant>(`${environment.apiUrl}/assistants/${id}`);
    }
    createAssistant(assistant: Assistant): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/assistants`, assistant);
    }
    updateAssistant(id: string | number, assistant: Assistant): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/assistants/${id}`, assistant);
    }
    deleteAssistant(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/assistants/${id}`);
    }
    getMe() {
        return this.http.get<any>(`${environment.apiUrl}/assistants/me`);
    }
    getAssistantSchedule(id: number | string) {
        return this.http.get<any[]>(`${environment.apiUrl}/scheduling/listarAgendaAssistente?assistente=${id}`)
    }
    createAssistantSchedule(id: number | string, schedule: any) {
        return this.http.post<any[]>(`${environment.apiUrl}/scheduling/agendarAssistente`, schedule)
    }
    getCurrentScheduling() {
        return this.http.get<any>(`${environment.apiUrl}/assistants_scheduling/current`)
    }
    changeRoomAssistant(changeRoomAssistant: any) {
        return this.http.post<any>(`${environment.apiUrl}/scheduling/agendarAssistenteEmSala`, changeRoomAssistant)
    }
    changeAssistant(changeAssistant: any) {
        return this.http.put<any>(`${environment.apiUrl}/scheduling/alterarAssistente`, changeAssistant)
    }
}
