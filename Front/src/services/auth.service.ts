import EmpresaRegister from 'src/models/auth/empresa-register.model';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import UserLogin from 'src/models/auth/user-login.model';
import UserRegister from 'src/models/auth/user-register.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private router: Router) { }

  login(user: UserLogin): Observable<any> {
    return this.http.post(`${environment.apiUrl}/login`, user)
  }
  logAuditLogout(): Observable<any> {
    return this.http.post(`${environment.apiUrl}/auditLog/logout`,{})
  }
  getFullUser(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/user/fullUser`)
  }
  recoverPassword(username: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/users/recover-password`, { username })
  }
  userRegister(user: UserRegister): Observable<any> {
    return this.http.post(`${environment.apiUrl}/professionals/sign-up`, user)
  }
  getToken() {
    return localStorage.getItem('accessToken');
  }

  setToken(token: string) {
    localStorage.setItem('accessToken', token);
  }
  setUserId(userId: string | number) {
    localStorage.setItem('userId', userId.toString());
  }
  getUserId(): string {
    const userId: any = localStorage.getItem('userId');
    return userId;
  }

  getAccessType() {
    return localStorage.getItem('accessType');
  }

  setAccessType(accessType: string) {
    localStorage.setItem('accessType', accessType);
  }
  logout() {
    this.logAuditLogout().subscribe(res => {
      localStorage.clear();
      this.router.navigate(["login"], { replaceUrl: true });
    }, error => {
      localStorage.clear();
      this.router.navigate(["login"], { replaceUrl: true });
    });
  }
  logoutPatient(professionalKey: String) {
    this.logAuditLogout().subscribe(res => {
      localStorage.clear();
      console.log("oi")
      console.log(`auth/${professionalKey}/login`)
      this.router.navigate([`auth/${professionalKey}/login`], { replaceUrl: true })
    }, error => {
      localStorage.clear();
      this.router.navigate([`auth/${professionalKey}/login`], { replaceUrl: true })
    });
  }
  changePassword(password: any, token: string) {
    const headers: any = {
      Authorization: 'Bearer ' + token
    }
    return this.http.post(`${environment.apiUrl}/users/update-password`, { password }, { headers })
  }

  companyRegister(company): Observable<any> {
    return this.http.post(`${environment.apiUrl}/company`, company)
  }
}
