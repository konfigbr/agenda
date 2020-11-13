import { createSelector } from '@ngrx/store';
import { AppState } from 'src/models/app-state.model';
import AuthState from 'src/models/auth/auth-state.model';

export const authState = (state: AppState) => state.auth;

export const selectUserLogin = createSelector(
    authState,
    (state: AuthState) => state.userLogin
);

export const selectUserRegister = createSelector(
    authState,
    (state: AuthState) => state.userRegister
);

export const selectUsernameRecoverPassword = createSelector(
    authState,
    (state: AuthState) => state.emailRecoverPassword
);
export const selectAuthUser = (state: AuthState) => state.userLogin;
export const selectIsLogedIn = createSelector(selectAuthUser, user => !!user);