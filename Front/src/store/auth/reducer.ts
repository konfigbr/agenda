import AuthState from 'src/models/auth/auth-state.model';
import * as authActions from './actions';

export function reducer(state = new AuthState(), action: authActions.AuthActions): AuthState {
    switch (action.type) {
        case authActions.AuthActionTypes.SET_USER_LOGIN:
            return { ...state, userLogin: { ...state.userLogin, ...action.payload.userLogin } };
        case authActions.AuthActionTypes.SET_USER_REGISTER:
            return { ...state, userRegister: { ...state.userRegister, ...action.payload.userRegister } };
        case authActions.AuthActionTypes.SET_USERNAME_RECOVER_PASSWORD:
            return { ...state, emailRecoverPassword: action.payload.username };
        default:
            return state;
    }
}
