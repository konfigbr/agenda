import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
// import {User} from '../interfaces/user';
// import {environment} from '../../environments/environment';
// import {Medico} from '../interfaces/medico';
// import {Assistente} from '../interfaces/assistente';
import { BehaviorSubject, Observable } from 'rxjs';
import { ProfileRoles } from 'src/models/profile-roles.model';
import { environment } from 'src/environments/environment';
// import {Ativo} from '../interfaces/ativo';
// import {AuthenticationService} from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private perfilSubject: BehaviorSubject<ProfileRoles>;
  private usuarioSubject: BehaviorSubject<any>;
  public perfis = [{ perfilUsuario: ProfileRoles.ROLE_ADMINISTRADOR, nome: 'Administrador' },
  { perfilUsuario: ProfileRoles.ROLE_ATENDENTE, nome: 'Atendente' },
  { perfilUsuario: ProfileRoles.ROLE_ASSISTENTE, nome: 'Assistente' },
  { perfilUsuario: ProfileRoles.ROLE_MEDICO, nome: 'Profissional' }]

  constructor(private http: HttpClient) {
    this.perfilSubject = new BehaviorSubject<ProfileRoles>(ProfileRoles[localStorage.getItem('accessType')]);
    if (this.perfilSubject.value !== ProfileRoles.ROLE_ADMINISTRADOR.toString()) {
      this.usuarioSubject = new BehaviorSubject<any>(JSON.parse(localStorage.getItem('usuario')));
    } else {
      this.usuarioSubject = new BehaviorSubject<any>(undefined)
    }
  }

  public get perfilUsuarioValue(): ProfileRoles {
    return this.perfilSubject.value;
  }

  public usuarioValue(): any {
    return (new BehaviorSubject<any>(JSON.parse(localStorage.getItem('usuario'))) as any);
  }






  getAdministrativeUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/users/administrative-users`);
  }
  getAdministrativeUserById(id: string | number): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/users/administrative-users/${id}`);
  }
  createAdministrativeUsers(administrativeUser: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/users/administrative-users`, administrativeUser);
  }
  updateAdministrativeUsers(administrativeUser: any): Observable<any> {
    return this.http.put(`${environment.apiUrl}/users/administrative-users/${administrativeUser.id}`, administrativeUser);
  }
  deleteAdministrativeUser(administrativeUserId: any): Observable<any> {
    return this.http.delete<any>(`${environment.apiUrl}/users/administrative-users/${administrativeUserId}`);
  }

}
