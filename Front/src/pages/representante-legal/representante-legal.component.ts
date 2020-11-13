import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import EmpresaRegister from 'src/models/auth/empresa-register.model';
import Specialty from 'src/models/specialty.model';
import { AuthService } from 'src/services/auth.service';
import { CepService } from 'src/services/cep.service';
import { SpecialtyService } from 'src/services/specialty.service';

export const confirmPasswordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  if (!control.parent || !control) {
    return null;
  }
  const password = control.parent.get('senha');
  const confirmPassword = control.parent.get('confSenha');
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
  selector: 'app-representante-legal',
  templateUrl: './representante-legal.component.html',
  styleUrls: ['./representante-legal.component.scss']
})

export class RepresentanteLegalComponent implements OnInit {
  states: string[] = [
    // tslint:disable-next-line:max-line-length
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];
  formGroupRepresentante: FormGroup;
  matcher = new MyErrorStateMatcher();

  @Input('empresa') empresa: EmpresaRegister;
  @Output() nextStep = new EventEmitter();
  isLoading: boolean = false;
  specialties: Specialty[];
  isRepresentanteLegal: boolean;

  constructor(private formBuilder: FormBuilder,
    private cepService: CepService,
    private specialtyService: SpecialtyService,
    private authService: AuthService,
    private snackBar: MatSnackBar,) {
    this.formGroupRepresentante = this.formBuilder.group({
      documentoProfissional: new FormControl('', [Validators.required]),
      nomeRepresentante: new FormControl('', [Validators.required, Validators.minLength(6)]),
      estadoCivil: new FormControl('', [Validators.required]),
      rg: new FormControl('', [Validators.required]),
      cpf: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
      endereco: new FormControl('', [Validators.required]),
      numero: new FormControl(''),
      bairro: new FormControl('', [Validators.required]),
      municipio: new FormControl('', [Validators.required]),
      uf: new FormControl('', [Validators.required]),
      ufCrm: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      senha: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confSenha: new FormControl('', [Validators.required, confirmPasswordValidator]),
      categoria: new FormControl('', [Validators.required]),
      especialidade: new FormControl('', [Validators.required])
    })
  }

  ngOnInit(): void {
    this.checkChange(false);
    this.getSpecialties();
  }

  next() {
    this.formGroupRepresentante.markAllAsTouched();
    if (this.formGroupRepresentante.valid) {
      if (this.isRepresentanteLegal)
        this.validarCRM().then(retorno => {
          if (retorno)
            this.nextStep.emit();
        }).catch(err => this.openSnackBar(err.error.message, 'OK', 5000));
    }
  }

  get isOdontologia() { return this.empresa.representanteLegal.especialidade?.tipoAceite === "ODONTOLOGIA" };

  verificarObgCategoria() {
    let control = this.formGroupRepresentante.controls['categoria'];
    if (this.isOdontologia) {
      if (this.empresa.representanteLegal.categoria)
        this.empresa.representanteLegal.categoria = "TODAS";

      control.setValidators([Validators.required]);
    }
    else
      control.clearValidators();

    control.updateValueAndValidity();
  }

  especialidadeChange() {
    this.verificarObgCategoria();
  }

  validarCampoForm(campo: string, error: string = null) {
    if (error != null)
      return this.formGroupRepresentante && this.formGroupRepresentante.controls[campo].hasError(error);
    else
      return this.formGroupRepresentante && this.formGroupRepresentante.controls[campo].hasError('required');
  }

  getCep() {
    const cep = this.empresa.representanteLegal.cep;

    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.empresa.representanteLegal.endereco = data.logradouro;
          this.empresa.representanteLegal.bairro = data.bairro;
          this.empresa.representanteLegal.municipio = data.localidade;
          this.empresa.representanteLegal.uf = data.uf;
          console.log(data);
        }
      }, _ => { })
    }
  }

  checkChange(isChecked: boolean) {
    if (isChecked) {
      this.formGroupRepresentante.controls['ufCrm'].enable();
      this.formGroupRepresentante.controls['documentoProfissional'].enable();
      this.formGroupRepresentante.controls['especialidade'].enable();
      this.formGroupRepresentante.controls['categoria'].enable();
      this.isRepresentanteLegal = true;
    }
    else {
      this.formGroupRepresentante.controls['ufCrm'].disable();
      this.formGroupRepresentante.controls['documentoProfissional'].disable();
      this.formGroupRepresentante.controls['especialidade'].disable();
      this.formGroupRepresentante.controls['categoria'].disable();
      this.isRepresentanteLegal = false;
    }
  }

  getSpecialties() {
    this.isLoading = true;
    this.specialtyService.getProfessionalSpecialties().subscribe((spacialties: Specialty[]) => {
      this.specialties = [...spacialties]
      this.isLoading = false;
    }, error => {
      console.log(error);
      this.isLoading = false;
    })
  }

  async validarCRM(): Promise<boolean> {
    if (this.isOdontologia) {
      let retornoCfo: any = await this.authService.getUserCFO(this.empresa.representanteLegal.crm, this.empresa.representanteLegal.ufCrm, this.empresa.representanteLegal.categoria).toPromise();
      if (retornoCfo.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.empresa.representanteLegal.especialidade.tipoAceite === "MEDICINA") {
      let retornoCfm: any = await this.authService.getUserCFM(this.empresa.representanteLegal.crm, this.empresa.representanteLegal.ufCrm).toPromise();
      if (retornoCfm.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.empresa.representanteLegal.especialidade.tipoAceite === "FONOAUDIOLOGIA") {
      this.isLoading = false;
      return true;
    }
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
