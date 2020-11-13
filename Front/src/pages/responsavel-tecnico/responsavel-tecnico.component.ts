import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import EmpresaRegister from 'src/models/auth/empresa-register.model';
import { AuthService } from 'src/services/auth.service';
import { SpecialtyService } from 'src/services/specialty.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CepService } from 'src/services/cep.service';
import MyErrorStateMatcher from 'src/config/error-state-matcher';

@Component({
  selector: 'app-responsavel-tecnico',
  templateUrl: './responsavel-tecnico.component.html',
  styleUrls: ['./responsavel-tecnico.component.scss']
})
export class ResponsavelTecnicoComponent implements OnInit {
  states: string[] = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];
  formGroupResponsavel: FormGroup;
  isLoading: boolean = false;
  specialties: any[] = [];
  snackbarRef: any;
  matcher = new MyErrorStateMatcher();

  @Input('empresa') empresa: EmpresaRegister;

  constructor(private formBuilder: FormBuilder,
    private specialtyService: SpecialtyService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private cepService: CepService,) {
    this.formGroupResponsavel = this.formBuilder.group({
      ufCrm: new FormControl('', [Validators.required]),
      documentoProfissional: new FormControl('', [Validators.required]),
      nome: new FormControl('', [Validators.required]),
      estadoCivil: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
      endereco: new FormControl('', [Validators.required]),
      numero: new FormControl('', [Validators.required]),
      bairro: new FormControl('', [Validators.required]),
      municipio: new FormControl('', [Validators.required]),
      uf: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      especialidade: new FormControl('', [Validators.required]),
      categoria: new FormControl('', [Validators.required])
    });
  }

  ngOnInit(): void {
    this.getSpecialties();
  }

  validarCampoForm(campo: string) {
    return this.formGroupResponsavel && this.formGroupResponsavel.controls[campo].hasError('required');
  }

  next() {
    this.formGroupResponsavel.markAllAsTouched();
    if (this.formGroupResponsavel.valid)
      this.validarCRM().then(retorno => {
        if (retorno)
          console.log(retorno)
      }).catch(err => this.openSnackBar(err.error.message, 'OK', 5000));
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

  especialidadeChange() {
    this.verificarObgCategoria();
  }

  verificarObgCategoria() {
    let control = this.formGroupResponsavel.controls['categoria'];
    if (this.isOdontologia)
      control.setValidators([Validators.required]);

    else
      control.clearValidators();

    control.updateValueAndValidity();
  }

  get isOdontologia() { return this.empresa.responsavelTecnico.especialidade?.tipoAceite === "ODONTOLOGIA" }

  async validarCRM(): Promise<boolean> {
    if (this.isOdontologia) {
      let retornoCfo: any = await this.authService.getUserCFO(this.empresa.responsavelTecnico.crm, this.empresa.responsavelTecnico.ufCrm, this.empresa.responsavelTecnico.categoria).toPromise();
      if (retornoCfo.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.empresa.responsavelTecnico.especialidade.tipoAceite === "MEDICINA") {
      let retornoCfm: any = await this.authService.getUserCFM(this.empresa.responsavelTecnico.crm, this.empresa.responsavelTecnico.ufCrm).toPromise();
      if (retornoCfm.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.empresa.responsavelTecnico.especialidade.tipoAceite === "FONOAUDIOLOGIA") {
      this.isLoading = false;
      return true;
    }
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackbarRef = this.snackBar.open(message, action, {
      duration: timing,
    })
  }

  getCep() {
    const cep = this.empresa.responsavelTecnico.cep
    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.empresa.responsavelTecnico.endereco = data.logradouro;
          this.empresa.responsavelTecnico.bairro = data.bairro;
          this.empresa.responsavelTecnico.municipio = data.localidade;
          this.empresa.responsavelTecnico.uf = data.uf;
        }
      }, _ => { })
    }
  }

}
