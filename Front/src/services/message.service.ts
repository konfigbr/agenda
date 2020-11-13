import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Room from 'src/models/room/room.model';

@Injectable({
    providedIn: 'root'
})
export class MessageService {

    constructor(private http: HttpClient) { }

    getRooms(): Observable<Room[]> {
        return this.http.get<Room[]>(`${environment.apiUrl}/rooms`);
    }
    getRoomById(id: string | number): Observable<Room> {
        return this.http.get<Room>(`${environment.apiUrl}/rooms/${id}`);
    }
    createRoom(room: Room): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/rooms`, room);
    }
    updateRoom(id: string | number, room: Room): Observable<any> {
        return this.http.put<any>(`${environment.apiUrl}/rooms/${id}`, room);
    }
    deleteRoom(id: string | number): Observable<any> {
        return this.http.delete<any>(`${environment.apiUrl}/rooms/${id}`);
    }
}
