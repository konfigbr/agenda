import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cep } from 'src/models/cep-model';

@Injectable({
    providedIn: 'root'
})
export class CepService {
    private apiUrl = 'https://viacep.com.br/ws'

    constructor(private http: HttpClient) { }

    getCep(cep: string | number): Observable<Cep> {
        return this.http.get<Cep>(`${this.apiUrl}/${cep}/json`)
    }
}