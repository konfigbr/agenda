import UserLogin from './user-login.model';
import UserRegister from './user-register.model';

export default class AuthState {
    public userLogin: UserLogin = new UserLogin();
    public userRegister: UserRegister = new UserRegister();
    public emailRecoverPassword!: string;
}
