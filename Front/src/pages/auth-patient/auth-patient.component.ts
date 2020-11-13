import { UserTermsModalComponent } from './../../components/user-terms-modal/user-terms-modal.component';
import TermOfUse from 'src/models/term-of-use.model';
import { CepService } from './../../services/cep.service';
import Patient from 'src/models/patient.model';
import { PatientService } from './../../services/patient.service';
import Professional from 'src/models/professional.model';
import { ProfessionalService } from 'src/services/professional.service';
import { SpecialtyService } from 'src/services/specialty.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AppState } from 'src/models/app-state.model';
import UserLogin from 'src/models/auth/user-login.model';
import { AuthService } from 'src/services/auth.service';
import { ValidatorsService } from 'src/services/validators.service';
import { SetUserLogin, SetUserRegister, SetUsernameRecoverPassword } from 'src/store/auth/actions';
import { selectUserLogin, selectUsernameRecoverPassword, selectUserRegister } from 'src/store/auth/selectors';
import { FormControl, Validators, FormGroupDirective, NgForm, ValidatorFn, AbstractControl, ValidationErrors, FormBuilder, FormGroup } from '@angular/forms';
import UserRegister from 'src/models/auth/user-register.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { MatDialog } from '@angular/material/dialog';
import { UtilsService } from 'src/services/utils.service';


export const confirmPasswordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  if (!control.parent || !control) {
    return null;
  }
  const password = control.parent.get('newPassword');
  const confirmPassword = control.parent.get('confirmPassword');
  if (!password || !confirmPassword) {
    return null;
  }
  if (confirmPassword.value === '') {
    return null;
  }
  if (password.value === confirmPassword.value) {
    return null;
  }
  return { passwordsNotMatching: true };
};

@Component({
  selector: 'app-auth-patient',
  templateUrl: './auth-patient.component.html',
  styleUrls: ['./auth-patient.component.scss']
})
export class AuthPatientComponent implements OnInit {
  userLogin: UserLogin = new UserLogin()
  usernameRecoverPassword$: Observable<string>
  isSubmitted: boolean = false;
  isLoading: boolean = false;
  changePassword: any = {}
  changePasswordFormGroup: FormGroup;
  confirmPassword: string = ''
  patient: Patient = new Patient();
  matcher = new MyErrorStateMatcher();
  isRegister: boolean = false;
  selectedTermOfUse: TermOfUse;
  page: string;
  specialties: any[] = []
  personTypes: any[] =
    [{ key: "CPF", value: "Pessoa Física" }
      , { key: "CNPJ", value: "Pessoa Jurídica" }
    ];
  userRegister: UserRegister = new UserRegister()

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  passwordFormControl = new FormControl('', [
    Validators.required,
  ]);

  usernameRecoverPasswordFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  token: any;
  changed: boolean = false

  formGroupUserRegister: FormGroup;
  professionalKey: String = "";

  constructor(
    private route: ActivatedRoute,
    private store: Store<AppState>,
    private validatorsService: ValidatorsService,
    private authService: AuthService,
    private specialtyService: SpecialtyService,
    private professionalService: ProfessionalService,
    private patientService: PatientService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private formBuilder: FormBuilder,
    private router: Router,
    private cepService: CepService,
    private utilService: UtilsService
  ) {

    this.formGroupUserRegister = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      document: new FormControl('', [Validators.required]),
      rg: new FormControl('', [Validators.required]),
      endereco: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
    })
    this.changePasswordFormGroup = this.formBuilder.group({
      newPassword: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required, confirmPasswordValidator])
    })
  }

  ngOnInit(): void {
    this.verifyProfessionalKey()
    this.usernameRecoverPassword$ = this.store.select(selectUsernameRecoverPassword);
    this.pageInit();
  }

  verifyProfessionalKey() {
    this.isLoading = true
    const professionalKey = this.route.snapshot.params.professionalKey
    console.log(professionalKey)
    this.professionalService.getProfessionalByUrl(professionalKey).subscribe((professional: Professional) => {
      localStorage.setItem("professionalKey", professionalKey)
      localStorage.setItem("professionalObj", JSON.stringify(professional))
      this.professionalKey = professionalKey

      this.isLoading = false
    }, error => {
      console.log("teste")
      this.router.navigate(["auth/login"], { replaceUrl: true })
      this.openSnackBar('Profissional não encontrado!', 'Ok', 5000);
    })
  }

  getProfessionalKey() {
    return localStorage.getItem("professionalKey")
  }

  getProfessionalObj() {
    return JSON.parse(localStorage.getItem("professionalObj"))
  }

  navigate(route: String) {
    this.formGroupUserRegister.reset();
    this.router.navigate([`auth/${this.getProfessionalKey()}/${route}`], { replaceUrl: true })
  }

  getSpecialties() {
    this.isLoading = true;
    this.specialtyService.getProfessionalSpecialties().subscribe((specialties: any[]) => {
      this.specialties = specialties;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
      console.log(error)
    })
  }
  cpfValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (!control.parent || !control) {
      return null;
    }
    const cpf = control.parent.get('document').value
    const cnpj = control.parent.get('document').value
    const tipoPessoa = control.parent.get('documentType').value.key
    console.log(tipoPessoa)
    if (tipoPessoa === 'CPF') {
      if (this.utilService.validaCPF(cpf)) {
        return null;
      }
      return { invalidCpf: true };
    } else {
      if (this.utilService.validaCNPJ(cnpj)) {
        return null;
      }
      return { invalidCnpj: true };

    }

  };

  formChangePasswordHasError(control: any, error: string) {
    return this.changePasswordFormGroup.controls[control].hasError(error);
  }
  pageInit() {
    this.route.params.subscribe(params => {
      this.page = params.page && params.page;
      console.log(this.getProfessionalKey())
      if (this.page === 'alterar-senha' && !this.token) {
        this.router.navigate([`auth/${this.getProfessionalKey()}/login`])
      }
      this.getSpecialties();
    });
  }
  onSubmit() {
    if (!this.isLoading && this.emailFormControl.valid && this.passwordFormControl.valid) {
      this.isLoading = true;
      this.onSubmitLogin(this.userLogin);
    }

  }
  formRegisterHasError(control: any, error: string) {
    return this.formGroupUserRegister.controls[control].hasError(error);
  }
  onSubmitLogin(userLogin: UserLogin) {
    this.authService.login(userLogin).subscribe(res => {
      if (res.passwordRecovered || res.accountLocked) {
        this.token = res.token;
        this.router.navigate([`auth/${this.getProfessionalKey()}/alterar-senha`])
      } else {
        this.authService.setAccessType(res.accessType);
        this.authService.setToken(res.token);
        if (res.accessType === 'ROLE_ADMINISTRADOR' || res.accessType === 'ROLE_ATENDENTE' || res.accessType === 'ROLE_PACIENTE') {
          this.authService.setUserId(res.userId);
        } else {
          this.authService.setUserId(res.idProfile);
        }
        if (res.accessType === 'ROLE_ADMINISTRADOR') {
          this.router.navigate(["agendamento-de-salas"], { replaceUrl: true })
        } else if (res.accessType === "ROLE_PROFISSIONAL") {
          if (this.isRegister) {
            this.router.navigate(["meu-perfil"], { replaceUrl: true });
          } else {
            this.router.navigate(["meu-painel"], { replaceUrl: true });
          }
        } else if (res.accessType === "ROLE_ASSISTENTE") {
          this.router.navigate(["meu-painel"], { replaceUrl: true })
        } else if (res.accessType === `ROLE_ATENDENTE`) {
          this.router.navigate(["agendamentos-do-dia"], { replaceUrl: true })
        } else if (res.accessType === `ROLE_PACIENTE`) {
          this.router.navigate([`${this.getProfessionalKey()}/minhas-consultas`], { replaceUrl: true })
        }
      }
      this.isLoading = false;
    },
      error => {
        console.log(error)

        if ((error && error.status !== 401) || (!error || error.status === 401))
          if (error.error.message === "Unauthorized")
            this.openSnackBar("Não autorizado", 'OK', 5000)
          else
            this.openSnackBar(error.error.message, 'OK', 5000)

        this.isLoading = false;
      })
  }
  setProfile(user) {
    this.authService.getFullUser().subscribe(response => {
      localStorage.setItem('usuario', JSON.stringify(response));
      localStorage.setItem('perfil', user.roles[0]);
    }, error => console.log(error))
  }

  onSubmitRegister() {
    if (!this.isLoading && this.formGroupUserRegister.valid) {

      const professional = this.getProfessionalObj()
      
      this.patientService.createPatient({ ...this.patient, idProfissional: professional.id }).subscribe(response => {
        this.isRegister = true;
        this.openSnackBar('Perfil de paciente criado com sucesso!', 'Ok', 3000);
        this.formGroupUserRegister.reset();
        this.navigate('login')
      }, error => {
        this.openSnackBar(error.error.message, 'Ok', 5000);
        console.log(error)
      })
    }
  }
  
  getCep() {
    const cep = this.patient.cep

    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.patient.endereco = data.logradouro
        }
      }, _ => {})
    }
  }

  openTerms() {
    const dialogRef = this.matDialog.open(UserTermsModalComponent, {
      width: '640px',
      maxWidth: '100%',
      data: this.selectedTermOfUse.texto
    });
    dialogRef.afterClosed().subscribe(result => {
    });
  }


  onSubmitChangePassword() {
    if (this.changePasswordFormGroup.valid) {
      this.isLoading = true;
      this.authService.changePassword(this.changePassword.newPassword, this.token).subscribe(res => {
        this.isRegister = true;
        this.onSubmitLogin({ username: this.userLogin.username, password: this.changePassword.newPassword })
      }, error => {
        this.isLoading = false
        console.log(error)
      })
    }
  }
  onSubmitRevocerPassword() {
    if (!this.isLoading && this.usernameRecoverPasswordFormControl.valid) {
      this.isLoading = true;
      let username: string;
      this.usernameRecoverPassword$.subscribe(usernameToRecover => {
        username = usernameToRecover;
      })
      this.authService.recoverPassword(username).subscribe((res: any) => {
        this.isLoading = false;
        this.openSnackBar('E-mail enviado com sucesso!', 'OK', 2000)
      }, error => {
        console.log(error)
        this.isLoading = false;
        this.openSnackBar(error.error.message, 'OK', 5000)
      })
    }
  }

  inputEvent($event) {
    this.changed = true;
  }

  inputAtributeUserLogin(atribute) {
    this.store.dispatch(new SetUserLogin({ userLogin: atribute }));
  }
  inputAtributeUserRegister(atribute) {
    this.store.dispatch(new SetUserRegister({ userRegister: atribute }));
  }
  inputAtributeUsername(atribute) {
    this.store.dispatch(new SetUsernameRecoverPassword({ ...atribute }));
  }
  validateLoginForm() {
    if (this.validatorsService.email(this.userLogin.username)) {
    }
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
