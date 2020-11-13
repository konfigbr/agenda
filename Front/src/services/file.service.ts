import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root'
})
export class FileServive {

    constructor(private http: HttpClient) { }

    fileUpload(formData: FormData): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/files/upload`, formData);
    }
    fileDownload(name: string): Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/files/download/${name}`);

    }
    getUrlFile(filename: String):Observable<any> {
        return this.http.get<any>(`${environment.apiUrl}/files/upload/url/${filename}`,);
    }
}
