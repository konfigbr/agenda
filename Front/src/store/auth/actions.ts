import { Action } from '@ngrx/store';
import UserLogin from 'src/models/auth/user-login.model';
import UserRegister from 'src/models/auth/user-register.model';

export enum AuthActionTypes {
    SET_USER_LOGIN = '[SET_USER_LOGIN]: Set user login',
    SET_USER_REGISTER = '[SET_USER_REGISTER]: Set user register',
    SET_USERNAME_RECOVER_PASSWORD = '[SET_USERNAME_RECOVER_PASSWORD]: Set username to recover password',
}

export class SetUserLogin implements Action {
    readonly type = AuthActionTypes.SET_USER_LOGIN;
    constructor(public payload: { userLogin: UserLogin }) { }
}

export class SetUserRegister implements Action {
    readonly type = AuthActionTypes.SET_USER_REGISTER;
    constructor(public payload: { userRegister: UserRegister }) { }
}

export class SetUsernameRecoverPassword implements Action {
    readonly type = AuthActionTypes.SET_USERNAME_RECOVER_PASSWORD;
    constructor(public payload: { username: string }) { }
}

export type AuthActions = SetUserLogin | SetUserRegister | SetUsernameRecoverPassword;
