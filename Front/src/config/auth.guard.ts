import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from 'src/services/auth.service';
import { Observable } from 'rxjs';
@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private authService: AuthService) { }
    // tslint:disable-next-line:max-line-length
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

        if (route.data.canAccess.find(access => access === this.authService.getAccessType())) {
            return true;
        }

        this.authService.logout();
        return false;
    }
}
