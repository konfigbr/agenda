import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Local from 'src/models/local.model';
import Room from 'src/models/room/room.model';

@Injectable({
    providedIn: 'root'
})
export class LocalService {

    constructor(private http: HttpClient) { }

    getLocals(): Observable<Local[]> {
        return this.http.get<Room[]>(`${environment.apiUrl}/locals`);
    }
    getLocalById(id: string | number): Observable<Local> {
        return this.http.get<Room>(`${environment.apiUrl}/locals/${id}`);
    }
    createLocal(room: Room): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/locals`, room);
    }
    updateLocal(id: string | number, room: Room): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/locals/${id}`, room);
    }
    deleteLocal(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/locals/${id}`);
    }
}
