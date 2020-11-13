import { HttpClient, HttpClientJsonpModule, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class DocumentService {
    private apiReceitaFederalUrl = 'https://www.receitaws.com.br/v1/cnpj'
    private apiInfoMaisUrl = 'https://api.infosimples.com/api/v1'
    private token = 'XSap-XuNyeoFl4gTgQktT-xzBsfxFDAKof3dfLcC'

    constructor(private http: HttpClient) { }

    getCompanyByCnpj(cnpj: string | number): Observable<any> {
        return this.http.jsonp(`${this.apiReceitaFederalUrl}/${cnpj}`, "callback")
    }

    /* getProfessionalByCFO(params: any): Observable<any> {
        return this.http.jsonp(`${this.apiInfoMaisUrl}/cfo/cadastro.json?token=${this.token}&inscricao=${params.inscricao}&uf=${params.uf}&categoria=${params.categoria}`, "callback")
    } */
    /* 
        getProfessionalByCFM(params: any): Observable<any> {
            return this.http.jsonp(`${this.apiInfoMaisUrl}/cfm/cadastro.json?token=${this.token}&inscricao=${params.inscricao}&uf=${params.uf}`, "callback")
        } */

    /* getProfessionalByCFO(params: any): Observable<any> {
        return this.http.post<any>(`${this.apiInfoMaisUrl}/cfo/cadastro.json?token=${this.token}&inscricao=${params.inscricao}&uf=${params.uf}&categoria=${params.categoria}`, "callback")
    }

    getProfessionalByCFM(params: any): Observable<any> {
        return this.http.post<any>(`${this.apiInfoMaisUrl}/cfm/cadastro.json?token=${this.token}&inscricao=${params.inscricao}&uf=${params.uf}`, "callback")
    } */


   getProfessionalByCFM(params: any){

        const headers: any = {
            'Content-Type': 'multipart/form-data; boundary=----WebKitFormBoundaryO8UiBmseBlCBoJCu',
            'Access-Control-Allow-Origin': '*',
        }

        const formData: FormData = new FormData()
        formData.append("token", this.token)
        formData.append("inscricao", params.inscricao)
        formData.append("uf", params.uf)
        formData.append("max_age", "86400")
        formData.append("timeout", "600")

        return this.http.post<any>(`${this.apiInfoMaisUrl}/cfm/cadastro`, formData)

       /*  const response = fetch(`${this.apiInfoMaisUrl}/cfm/cadastro`, {
            method: 'POST', // *GET, POST, PUT, DELETE, etc.
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
              'Content-Type': 'multipart/form-data; boundary=----WebKitFormBoundaryO8UiBmseBlCBoJCu'
              // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: 'follow', // manual, *follow, error
            referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
            body: JSON.stringify(params) // body data type must match "Content-Type" header
          });
          return response;  */
    } 
}