import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import Audit from 'src/models/audit.model';
@Injectable({
    providedIn: 'root'
})
export class AuditService {
    constructor(private http: HttpClient) { }
    getAll(): Observable<Audit[]> {
        return this.http.get<Audit[]>(`${environment.apiUrl}/auditLog/`);
    }
    
}
