import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { environment } from 'src/environments/environment';
@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService {
  constructor(private authService: AuthService) { }


  intercept(req, next) {
    if (req.url === `${environment.apiUrl}/users/login` || req.url === `${environment.apiUrl}/professionals/sign-up` || req.url === `${environment.apiUrl}/users/update-password` || !req.url.includes(environment.apiUrl)) {
      return next.handle(req);
    } else {
      const tokenizeReq = req.clone({
        setHeaders: {
          Authorization: 'Bearer ' + this.authService.getToken()
          // 'X-Auth-Token': this.authService.getToken()
        }
      });
      return next.handle(tokenizeReq);
    }

  }
}
