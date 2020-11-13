import { DocumentService } from './../../services/document.service';
import { ProfessionalService } from 'src/services/professional.service';
import { SpecialtyService } from 'src/services/specialty.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AppState } from 'src/models/app-state.model';
import AuthState from 'src/models/auth/auth-state.model';
import UserLogin from 'src/models/auth/user-login.model';
import { AuthService } from 'src/services/auth.service';
import { ValidatorsService } from 'src/services/validators.service';
import { SetUserLogin, SetUserRegister, SetUsernameRecoverPassword } from 'src/store/auth/actions';
import { selectUserLogin, selectUsernameRecoverPassword, selectUserRegister } from 'src/store/auth/selectors';
import { FormControl, Validators, FormGroupDirective, NgForm, ValidatorFn, AbstractControl, ValidationErrors, FormBuilder, FormGroup } from '@angular/forms';
import UserRegister from 'src/models/auth/user-register.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { RESET } from '@ngrx/store-devtools/src/actions';
import { MatDialog } from '@angular/material/dialog';
import { UserTermsModalComponent } from 'src/components/user-terms-modal/user-terms-modal.component';
import Specialty from 'src/models/specialty.model';
import { UtilsService } from 'src/services/utils.service';
import { CepService } from 'src/services/cep.service';
import EmpresaRegister from 'src/models/auth/empresa-register.model';
import { HttpResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

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
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent implements OnInit {
  appVersion: String = environment.appVersion;
  userLogin: UserLogin = new UserLogin()
  usernameRecoverPassword$: Observable<string>
  isSubmitted: boolean = false;
  isLoading: boolean = false;
  changePassword: any = {}
  changePasswordFormGroup: FormGroup;
  confirmPassword: string = ''
  matcher = new MyErrorStateMatcher();
  isRegister: boolean = false;
  page: string;
  empresa: EmpresaRegister = new EmpresaRegister()
  specialties: any[] = []
  states: string[] = [
    // tslint:disable-next-line:max-line-length
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];
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
  formGroupEmpresaRegister: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private store: Store<AppState>,
    private validatorsService: ValidatorsService,
    private authService: AuthService,
    private specialtyService: SpecialtyService,
    private cepService: CepService,
    private professionalService: ProfessionalService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private formBuilder: FormBuilder,
    private router: Router,
    private utilService: UtilsService,
    private documentService: DocumentService
  ) {

    this.formGroupUserRegister = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required, confirmPasswordValidator]),
      specialty: new FormControl([Validators.required]),
      document: new FormControl('', [Validators.required, Validators.minLength(11), this.cpfValidator])
    })
    this.formGroupEmpresaRegister = this.formBuilder.group({
      name: new FormControl({ value: '', disabled: true }, [Validators.required]),
      cep: new FormControl({ value: '', disabled: true }, [Validators.required]),
      uf: new FormControl({ value: '', disabled: true }, [Validators.required]),
      logradouro: new FormControl({ value: '', disabled: true }, [Validators.required]),
      bairro: new FormControl({ value: '', disabled: true }, [Validators.required]),
      municipio: new FormControl({ value: '', disabled: true }, [Validators.required]),
      numero: new FormControl({ value: '', disabled: true }, [Validators.required]),
      profDocument: new FormControl('', [Validators.required]),
      orgao: new FormControl({ value: '', disabled: true }, [Validators.required]),
      specialty: new FormControl('', [Validators.required]),
      razaoSocial: new FormControl({ value: '', disabled: true }, [Validators.required]),
      cnpj: new FormControl('', [Validators.required, Validators.minLength(11), this.cnpjValidator]),
      ufProfessional: new FormControl('', [Validators.required]),
      categoria: new FormControl(),
      especialty: new FormControl('', [Validators.required])
    })
    this.changePasswordFormGroup = this.formBuilder.group({
      newPassword: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required, confirmPasswordValidator])
    })
  }

  goToResponsavelTecnico() {
    console.log(this.empresa)
  }

  get isOdontologia() { return this.empresa.especialidade?.tipoAceite === "ODONTOLOGIA" }


  especialidadeChange() {
    this.verificarObgCategoria();
  }

  verificarObgCategoria() {
    let control = this.formGroupEmpresaRegister.controls['categoria'];
    if (this.isOdontologia)
      control.setValidators([Validators.required]);

    else
      control.clearValidators();

    control.updateValueAndValidity();
  }

  ngOnInit(): void {
    this.usernameRecoverPassword$ = this.store.select(selectUsernameRecoverPassword);
    this.pageInit();
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

  getCep() {
    const cep = this.empresa.cep

    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.userRegister.endereco = data.logradouro
        }
      }, _ => { })
    }
  }

  cpfValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (!control.parent || !control) {
      return null;
    }
    const cpf = control.parent.get('document').value

    if (this.utilService.validaCPF(cpf)) {
      return null;
    }
    return { invalidCpf: true }

  }

  cnpjValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (!control.parent || !control) {
      return null;
    }
    const cnpj = control.parent.get('cnpj').value

    if (cnpj && this.utilService.validaCNPJ(cnpj)) {
      return null
    }
    return { invalidCnpj: true }
  }

  formChangePasswordHasError(control: any, error: string) {
    return this.changePasswordFormGroup.controls[control].hasError(error);
  }

  pageInit() {
    this.route.params.subscribe(params => {
      this.page = params.page && params.page;
      if (this.page === 'alterar-senha' && !this.token) {
        this.router.navigate(['auth/login'])
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

  formEmpresaRegisterHasError(control: any, error: string) {
    return this.formGroupEmpresaRegister.controls[control].hasError(error);
  }

  onSubmitLogin(userLogin: UserLogin) {
    this.authService.login(userLogin).subscribe(res => {
      if ((res.passwordRecovered || res.accountLocked) && res.accessType !== `ROLE_PACIENTE`) {
        this.token = res.token;
        this.router.navigate(['auth/alterar-senha'])
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
          this.openSnackBar("Não autorizado", 'OK', 5000)
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
      this.authService.userRegister(
        {
          ...this.userRegister,
          especialidade: this.userRegister.especialidade.id,
          tipoDocumento: 'CPF'
        }).subscribe((res: any) => {
          this.isRegister = true;
          this.openSnackBar('Cadastro efetuado com sucesso!', 'OK', 2000);
          this.onSubmitLogin({ password: this.userRegister.password, username: this.userRegister.username } as UserLogin);
        }, error => {
          console.log(error);
          this.openSnackBar(error.error.message, 'OK', 5000)
        })
    }
  }

  next() {
    // this.formGroupEmpresaRegister.markAllAsTouched();
    // if (this.formGroupEmpresaRegister.valid) {
    //   this.validarCRM().then(retorno => {
    //     if (retorno)
          this.goToRepresentante();
    //   }).catch(err => this.openSnackBar(err.error.message, 'OK', 5000));
    // }
  }

  async validarCRM(): Promise<boolean> {
    this.isLoading = true;

    if (this.isOdontologia) {
      let retornoCfo: any = await this.authService.getUserCFO(this.empresa.profDocument, this.empresa.ufProfissional, this.empresa.categoria).toPromise();
      if (retornoCfo.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.empresa.especialidade.tipoAceite === "MEDICINA") {
      let retornoCfm: any = await this.authService.getUserCFM(this.empresa.profDocument, this.empresa.ufProfissional).toPromise();
      if (retornoCfm.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.empresa.especialidade.tipoAceite === "FONOAUDIOLOGIA") {
      this.isLoading = false;
      return true;
    }
  }

  registerEmpresa() {
    if (this.empresa.orgao !== "ATIVA") {
      this.isLoading = false;
      return this.openSnackBar("Empresa encontra-se inativa", 'OK', 5000);
    }

    if (!this.isOdontologia)
      this.empresa.categoria = null;

    const empresa = {
      cnpj: this.empresa.cnpj,
      nome: this.empresa.nome,
      razaoSocial: this.empresa.razaoSocial,
      cep: this.empresa.cep,
      logradouro: this.empresa.logradouro,
      numero: this.empresa.numero,
      bairro: this.empresa.bairro,
      municipio: this.empresa.municipio,
      uf: this.empresa.uf,
      situacao: this.empresa.orgao,
      representanteLegal: null,
      categoria: this.empresa.categoria,
      ufcrm: this.empresa.ufProfissional,
      refEspecialidadeEntity: this.empresa.especialidade
    }

    if (this.formGroupEmpresaRegister.valid) {
      this.authService.companyRegister(empresa).subscribe(res => {
        this.openSnackBar("Empresa criada com sucesso", 'OK', 5000);
        this.isLoading = false;
      }, error => {
        this.openSnackBar(error.error.message, 'OK', 5000);
        this.isLoading = false;
      });
    } else {
      this.openSnackBar("Erros presentes no formulário", 'OK', 5000);
      this.isLoading = false;
    }
  }

  onSubmitChangePassword() {
    if (this.changePasswordFormGroup.valid) {
      this.isLoading = true;
      this.authService.changePassword(this.changePassword.newPassword, this.token).subscribe(res => {
        this.isRegister = true;
        this.onSubmitLogin({ username: this.userLogin.username, password: this.changePassword.newPassword });
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

  clearForm() {
    Object.keys(this.formGroupUserRegister.controls).map((value) => {
      this.formGroupUserRegister.controls[value].setValue('');
      this.formGroupUserRegister.controls[value].markAsPristine();
      this.formGroupUserRegister.controls[value].markAsUntouched();
      this.formGroupUserRegister.controls[value].updateValueAndValidity();
    });
  }

  selectPessoa(opcao: string) {
    if (opcao == 'PESSOA') {
      this.router.navigate(['/auth/cadastro'])
    }
    else if (opcao == 'EMPRESA') {
      this.router.navigate(['/auth/empresa'])
    }
  }

  onCnpjChange() {
    this.isRegister = true
    this.documentService.getCompanyByCnpj(this.empresa.cnpj).subscribe(res => {
      this.empresa.nome = res.nome
      this.empresa.razaoSocial = ""//res.
      this.empresa.cep = res.cep
      this.empresa.logradouro = res.logradouro
      this.empresa.numero = res.numero
      this.empresa.bairro = res.bairro
      this.empresa.municipio = res.municipio
      this.empresa.uf = res.uf
      this.empresa.orgao = res.situacao

      if (this.empresa.orgao !== "ATIVA")
        this.openSnackBar("Empresa encontra-se inativa", 'OK', 5000)

    }, error => {
      this.isLoading = false
      console.log(error)
    })
  }

  goToRepresentante() {
    this.page = 'representante-legal';
  }
}
