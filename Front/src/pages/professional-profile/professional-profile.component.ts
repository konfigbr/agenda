import { Component, OnInit } from '@angular/core';
import Professional from 'src/models/professional.model';
import { FormGroup, FormBuilder, FormControl, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfessionalService } from 'src/services/professional.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SpecialtyService } from 'src/services/specialty.service';
import Specialty from 'src/models/specialty.model';
import { MatDialog } from '@angular/material/dialog';
import { MessageProfessionalAproveComponent } from 'src/components/message-professional-aprove/message-professional-aprove.component';
import { ShowImagesModalComponent } from 'src/components/show-images-modal/show-images-modal.component';
import { FileServive } from 'src/services/file.service';
import TermOfUse from 'src/models/term-of-use.model';
import UserRegister from 'src/models/auth/user-register.model';
import { UtilsService } from 'src/services/utils.service';
import { CepService } from 'src/services/cep.service';
import { AuthService } from 'src/services/auth.service';

export const confirmPasswordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  if (!control.parent || !control) {
    return null;
  }
  const password = control.parent.get('password');
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
  selector: 'app-professional-profile',
  templateUrl: './professional-profile.component.html',
  styleUrls: ['./professional-profile.component.scss']
})
export class ProfessionalProfileComponent implements OnInit {

  professional: Professional = new Professional();
  professionalFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;
  isNext: boolean = false;
  isCompleteNext: boolean = false;
  acceptTerms: boolean = false;
  specialties: Specialty[] = []
  termsOfUse: TermOfUse[] = []
  selectedTermOfUse: TermOfUse;
  states: string[] = [
    // tslint:disable-next-line:max-line-length
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];
  fileRG: any;
  fileCPF: any;
  fileCRM: any;
  fileEnd: any;
  filePhoto: any;
  userRegister: UserRegister = new UserRegister()
  fileTerm: any;
  changed: boolean = false
  snackbarRef: any;
  personTypes: any[] =
    [{ key: "CPF", value: "Pessoa Física" }
      , { key: "CNPJ", value: "Pessoa Jurídica" }
    ];

  constructor(

    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private professionalService: ProfessionalService,
    private specialtyService: SpecialtyService,
    public dialog: MatDialog,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private snackBarSucess: MatSnackBar,
    private fileService: FileServive,
    private utilService: UtilsService,
    private cepService: CepService,
    private router: Router
  ) {
    this.professionalFormGroup = this.formBuilder.group({
      name: new FormControl({ value: '', validator: [Validators.required] }),
      cpf: new FormControl('', [Validators.required, Validators.minLength(11), this.cpfValidator]),
      rg: new FormControl('', [Validators.required]),
      crm: new FormControl('', [Validators.required]),
      ufcrm: new FormControl('', [Validators.required]),
      phone: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
      specialty: new FormControl(new Specialty(), [Validators.required]),
      username: new FormControl('', [Validators.required, Validators.email]),
      documentType: new FormControl([Validators.required]),
      categoria: new FormControl(),
      nacionalidade: new FormControl('', [Validators.required, Validators.pattern('^[a-zA-Z ]*$')]),
      estadoCivil: new FormControl('', [Validators.required, Validators.pattern('^[a-zA-Z ]*$')]),
      logradouro: new FormControl('', [Validators.required]),
      numero: new FormControl('', [Validators.required]),
      bairro: new FormControl('', [Validators.required]),
      cidade: new FormControl('', [Validators.required]),
      uf: new FormControl('', [Validators.required]),
      url: new FormControl('', [Validators.maxLength(20), Validators.minLength(3)]),
      acceptTerms: new FormControl(false, [Validators.required]),
      ramal: new FormControl(null)
    })
    console.log(this.acceptTerms)
  }

  ngOnInit(): void {
    this.professionalFormGroup.controls.username.disable()
    this.getSpecialties()

  }

  get isOdontologia() { return this.professional.especialidade?.tipoAceite === "ODONTOLOGIA" };

  verificarObgCategoria() {
    let control = this.professionalFormGroup.controls['categoria'];
    if (this.isOdontologia) {
      if (this.professional.categoria)
        this.professional.categoria = "TODAS";

      control.setValidators([Validators.required]);
    }
    else
      control.clearValidators();

    control.updateValueAndValidity();
  }

  setFile(type: string, event: any) {
    const file = event.target.files[0]
    if (type === 'rg') {
      this.fileRG = file
    } else if (type === 'cpf') {
      this.fileCPF = file
    } else if (type === 'crm') {
      this.fileCRM = file
    } else if (type === 'end') {
      this.fileEnd = file
    } else if (type === 'photo') {
      this.filePhoto = file
    } else if (type === 'term') {
      this.fileTerm = file
    }
    this.changed = true;

  }


  cpfValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (!control.parent || !control) {
      return null;
    }
    const cpf = control.parent.get('cpf').value
    const cnpj = control.parent.get('cpf').value
    const tipoPessoa = control.parent.get('documentType').value
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

  getTermsOfUse() {
    this.isLoading = true;
    this.professionalService.getTermsOfUse().subscribe((terms: any[]) => {
      this.termsOfUse = terms;
      //this.selectedTermOfUse = terms.find(x => x.tipo === "PF_ODONTO")
      this.changeSpecialtyOrPersonType()
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
      console.log(error)
    })
  }

  changeSpecialtyOrPersonType() {
    const specialty = this.professional.especialidade
    const personType = this.professional.tipoDocumento

    this.verificarObgCategoria();
    if (specialty) {
      if (specialty.nome !== "Odontologia" && specialty.nome !== "Otorrino")
        this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PF_ODONTO")
      else if (personType === "CPF") {
        if (specialty.nome === "Odontologia")
          this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PF_ODONTO")

        else if (specialty.nome === "Otorrino")
          this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PF_MEDICINA")
        else if (specialty.nome === "Fonoaudiologia")
          this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PF_FONO")
      } else if (personType === "CNPJ") {
        if (specialty.nome === "Odontologia")
          this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PJ_ODONTO")

        else if (specialty.nome === "Otorrino")
          this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PJ_MEDICINA")
        else if (specialty.nome === "Fonoaudiologia")
          this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PJ_FONO")
      } else {
        this.selectedTermOfUse = this.termsOfUse.find(term => term.tipo === "PJ_MEDICINA")
      }

    }

    //  console.log(specialty, personType, this.selectedTermOfUse)
  }



  next() {
    if (this.professional.url === '') {
      this.professional.url = null;
    }
    const validation = (/^[0-9a-zA-Z-]+$/).test(this.professional.url)
    if (!validation) {
      const erroMessage = "URL não pode conter espaços, números e caracteres especiais"
      this.openSnackBar(erroMessage, 'Ok', 5000);
      this.isLoading = false;
      console.log(erroMessage)
      return
    }
    this.isNext = true
  }

  back() {
    this.isNext = false
  }

  validNext() {
    if (
      !this.professional.nome || !this.professional.cpf
      || !this.professional.rg || !this.professional.crm || !this.professional.ufcrm ||
      !this.professional.telefone || !this.professional.especialidade || !this.professional.cep ||
      !this.professional.nacionalidade || !this.professional.estadoCivil || !this.professional.logradouro || !this.professional.numero ||
      !this.professional.cidade || !this.professional.uf || !this.professional.bairro
    ) {
      this.isCompleteNext = false
      /* this.openSnackBar('Para realizar suas reservas é necessário o preenchimento de todos os dados de seu perfil, juntamente com as imagens dos documentos solicitados para que possamos aprová-lo.', 'Ok', 15000) */
    } else {
      this.isCompleteNext = true
    }
  }

  getMe() {
    this.professionalService.getMe().subscribe((professional: Professional) => {
      this.professional = professional
      if (professional.url !== null) {
        this.professionalFormGroup.controls.url.disable()
      }
      if (this.professional.termoUsoPrivacidade === 'ACEITO') {
        this.acceptTerms = true
      }
      this.professional.especialidade =
        this.professional.especialidade && this.specialties.find(specialty => specialty.id === this.professional.especialidade.id);
        
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }

  inputEvent($event) {
    this.changed = true;
    this.changeSpecialtyOrPersonType()
    this.validNext()
  }

  getCep() {
    const cep = this.professional.cep

    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.professional.logradouro = data.logradouro
          this.professional.bairro = data.bairro
          this.professional.cidade = data.localidade
          this.professional.uf = data.uf
        }
      }, _ => { })
    }
  }

  showMessage() {
    setTimeout(() => {
      if (
        !this.professionalFormGroup.valid || !this.professional.nome || !this.professional.username || !this.professional.cpf
        || !this.professional.imagecpf || this.professional.imagecpf === null || !this.professional.rg || !this.professional.imagerg
        || this.professional.imagerg === null || this.professional.imagecrm === null
        || !this.professional.crm || !this.professional.imagecrm || !this.professional.ufcrm ||
        !this.professional.telefone || !this.professional.especialidade || !this.professional.cep ||
        !this.professional.nacionalidade || !this.professional.estadoCivil || !this.professional.logradouro || !this.professional.numero ||
        !this.professional.cidade || !this.professional.uf || !this.professional.bairro
        || !this.professional.categoria
      ) {
        /* this.openSnackBar('Para realizar suas reservas é necessário o preenchimento de todos os dados de seu perfil, juntamente com as imagens dos documentos solicitados para que possamos aprová-lo.', 'Ok', 15000) */
      }
    }, 1000)
  }
  /*   showMessageSP(){
      setTimeout(()  => {
        if (this.professional.ufcrm !== 'SP') {
           this.openSnackBar('Para aprovação do seu cadastro, certifique-se de que seu documento fora avaliado pelo órgão competente do Estado de São Paulo', 'Ok', 15000) 
        }
      }, 1000)
    } */

  getSpecialties() {
    this.isLoading = true;
    this.specialtyService.getProfessionalSpecialties().subscribe((spacialties: Specialty[]) => {
      this.specialties = [...spacialties]
      this.isLoading = false;
      this.getMe()
      this.getTermsOfUse()
    }, error => {
      console.log(error);
      this.isLoading = false;

    })
  }

  formProfessionalHasError(control: any, error: string) {
    return this.professionalFormGroup.controls[control].hasError(error);
  }

  uploadFile(formData: FormData, type: string) {
    this.fileService.fileUpload(formData).subscribe(res => {
      if (!res.erro) {
        if (type === 'cpf') {
          this.fileCPF = undefined;
          this.professional.imagecpf = `${res.filename}`;
        } else if (type === 'rg') {
          this.fileRG = undefined
          this.professional.imagerg = `${res.filename}`;
        } else if (type === 'crm') {
          this.fileCRM = undefined
          this.professional.imagecrm = `${res.filename}`;
        } else if (type === 'end') {
          this.fileEnd = undefined
          this.professional.imageComprovante = `${res.filename}`;
        } else if (type === 'photo') {
          this.filePhoto = undefined;
          this.professional.imageFoto = `${res.filename}`;
        } else if (type === 'term') {
          this.fileTerm = undefined;
          this.professional.termoAceite = `${res.filename}`;
        }
        this.onSubmit();
      } else {
        this.openSnackBar(res.erro, 'Ok', 5000);

      }


    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }

  onSubmit() {

    if (this.professional.url === '') {
      this.professional.url = null;
    }
    const validation = (/^[0-9a-zA-Z-]+$/).test(this.professional.url)
    if (!validation) {
      const erroMessage = "URL não pode conter espaços, números e caracteres especiais"
      this.openSnackBar(erroMessage, 'Ok', 5000);
      this.isLoading = false;
      console.log(erroMessage)
      return
    }

    this.professionalFormGroup.markAllAsTouched();
    if (this.professionalFormGroup.valid) {
      this.isLoading = true;

      if (this.fileRG) {

        const formData: FormData = new FormData()

        formData.append('uploadfile', this.fileRG)
        this.uploadFile(formData, 'rg')

      } else if (this.fileCPF) {
        const formData: FormData = new FormData()

        formData.append('uploadfile', this.fileCPF)
        setTimeout(() => {
          this.uploadFile(formData, 'cpf')

        }, 300)

      } else if (this.fileCRM) {

        const formData: FormData = new FormData()

        formData.append('uploadfile', this.fileCRM)
        this.uploadFile(formData, 'crm')

      } else if (this.fileEnd) {

        const formData: FormData = new FormData()

        formData.append('uploadfile', this.fileEnd)
        this.uploadFile(formData, 'end')

      } else if (this.filePhoto) {

        const formData: FormData = new FormData()

        formData.append('uploadfile', this.filePhoto)
        this.uploadFile(formData, 'photo')

      } else if (this.fileTerm) {

        const formData: FormData = new FormData()

        formData.append('uploadfile', this.fileTerm)
        this.uploadFile(formData, 'term')

      } else {
        this.validarCRM().then(retorno => {
          if (retorno)
            this.updateProfessional();
        }).catch(err => this.openSnackBar(err.error.message, 'OK', 5000));
      }
    }
  }

  async validarCRM(): Promise<boolean> {
    if (this.isOdontologia) {
      let retornoCfo: any = await this.authService.getUserCFO(this.professional.crm, this.professional.ufcrm, this.professional.categoria).toPromise();
      if (retornoCfo.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.professional.especialidade.tipoAceite === "MEDICINA") {
      let retornoCfm: any = await this.authService.getUserCFM(this.professional.crm, this.professional.ufcrm).toPromise();
      if (retornoCfm.code === 200) {
        this.isLoading = false;
        return true;
      }
      else {
        this.isLoading = false;
        this.openSnackBar("O documento do profissional não foi encontrado", 'OK', 5000);
        return false;
      }

    } else if (this.professional.especialidade.tipoAceite === "FONOAUDIOLOGIA") {
      this.isLoading = false;
      return true;
    }
  }

  updateProfessional() {

    if (!this.isOdontologia)
      this.professional.categoria = null;

    if (this.professional.ufcrm !== 'SP') {
      this.openSnackBar('Para aprovação do seu cadastro, certifique-se de que seu documento fora avaliado pelo órgão competente do Estado de São Paulo', 'Ok', 15000)

      if (this.professional.especialidade.tipoAceite === 'ODONTOLOGIA') {
        this.openSnackBar('É obrigatória a inscrição secundária na jurisdição do Conselho Regional de Odontologia no Estado de São Paulo, caso seu exercício profissional se estenda por mais de 90 (noventa) dias, nos termos dos artigos 119, §1º e 134 da Resolução CFO n.º 63/2005.', 'Ok', 15000)
      } else if (this.professional.especialidade.tipoAceite === 'FONOAUDIOLOGIA') {
        this.openSnackBar('É obrigatória a inscrição secundária na jurisdição do Conselho Regional de Fonoaudiologia no Estado de São Paulo, caso seu exercício profissional se estenda por mais de 30 (trinta) dias por ano, fora da jurisdição de seu Conselho Regional de origem, nos termos dos artigos 10, 11 § 1, da Resolução do CFFa nº 532/1998.', 'Ok', 15000)
      } else if (this.professional.especialidade.tipoAceite === 'MEDICINA')
        this.openSnackBar('É obrigatória a inscrição secundária na jurisdição do Conselho Regional de Medicina no Estado de São Paulo, caso seu exercício profissional se estenda por mais de 90 (noventa) dias, nos termos do artigo 32, § 5º da Resolução CFM n.º 1.541/1998.', 'Ok', 15000)
    }


    this.isLoading = true;

    this.professional.termoUsoPrivacidade = this.acceptTerms == true ? "ACEITO" : "RECUSADO"
    this.professionalService.updateProfessional(this.professional.id, this.professional).subscribe(response => {
      this.isLoading = false;
      this.router.navigate(["meu-painel"], { replaceUrl: true })


      if (this.snackbarRef) {
        this.snackbarRef.afterDismissed().subscribe(res => {
          this.openSnackBar('Perfil atualizado com sucesso!', 'Ok', 3000);

        })

      } else {
        this.openSnackBar('Perfil atualizado com sucesso!', 'Ok', 3000);
      }
    }, error => {
      this.isLoading = false;
      if (this.snackbarRef) {
        this.snackbarRef.afterDismissed().subscribe(res => {
          this.openSnackBar(error.error.message, 'Ok', 5000);

        })

      } else {
        this.openSnackBar(error.error.message, 'Ok', 5000);
      }

      console.log(error)
    })
  }

  changeCheckboxUserData(event) {
    if (event.checked) {
      this.acceptTerms = true
    } else {
      this.acceptTerms = false

    }
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackbarRef = this.snackBar.open(message, action, {
      duration: timing,
    })
  }
  openSnackBarSucess(message: string, action: string, timing: number) {
    this.snackBarSucess.open(message, action, {
      duration: timing,
    })
  }

  reproveProfessional() {
    const dialogRef = this.dialog.open(MessageProfessionalAproveComponent, {
      width: '400px',
      data: {
        title: 'Reprovação de Profissional',
        message: `Deseja mesmo reprovar o profissional "${this.professional.nome}"?`, confirmLabel: 'Reprovar'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.professionalService.reproveProfessional(this.professional.id).subscribe(res => {
          this.getMe();
        }, error => {
          console.log(error)
        })
      }
    })
  }
  approveProfessional() {
    const dialogRef = this.dialog.open(MessageProfessionalAproveComponent, {
      width: '400px',
      data: { title: 'Aprovação de Profissional', message: `Deseja mesmo aprovar o profissional "${this.professional.nome}"?`, confirmLabel: 'Aprovar' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.professionalService.approveProfessional(this.professional.id).subscribe(res => {
          this.getMe();
        }, error => {
          console.log(error)
        })
      }
    })
  }
  showDocument(document: string) {
    let title: string;
    let image: string;
    const images: string[] = [];
    if (document === 'cpf') {
      title = 'Imagens do CPF'
      image = this.professional.imagecpf
    } else if (document === 'rg') {
      title = 'Imagens do RG'
      image = this.professional.imagerg
    } else if (document === 'crm') {
      title = 'Imagens do CRM'
      image = this.professional.imagecrm
    } else if (document === 'end') {
      title = 'Imagens do End'
      image = this.professional.imageComprovante
    } else if (document === 'photo') {
      title = 'Imagens do Foto'
      image = this.professional.imageFoto
    } else {
      title = 'Termo de Aceite'
      image = this.professional.termoAceite
    }

    this.isLoading = true;
    this.fileService.getUrlFile(image).subscribe(res => {
      images.push(res.url)
      this.isLoading = false
    }, error => {
      console.log(error);
      this.isLoading = false
    })

    const dialogRef = this.dialog.open(ShowImagesModalComponent, {
      width: '680px',
      maxWidth: '100%',
      data: { title, images }
    });
  }
  changeDocumentType() {
    this.professional.cpf = ""
    this.changeSpecialtyOrPersonType()
  }

  getprevia() {
    if (this.professional.url === null)
      return ''

    else
      return window.location.origin + '/#/auth/' + this.professional.url + '/login';
  }

  /* dowloadTermoAceitePdf() {
     this.professionalService.getDownloadPdfTerms().subscribe(response => {
      const blob = new Blob([response],{type: 'application/pdf'});
      if(window.navigator && window.navigator.msSaveOrOpenBlob){
        window.navigator.msSaveOrOpenBlob(blob);
        return
      }
      const data = window.URL.createObjectURL(blob)
      const link = document.createElement('a');
      link.href = data;
      link.download = 'termoDeAceite.pdf';
      link.dispatchEvent( new MouseEvent('click', {bubbles:true, cancelable: true, view: window }))

      setTimeout(function() {
        window.URL.revokeObjectURL(data);
        link.remove();
        
      }, 100);
     
    })

      
  }  */

}
