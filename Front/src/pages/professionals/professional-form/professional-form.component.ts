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
import { DataService } from 'src/services/data.service';
import { environment } from 'src/environments/environment';
import { AuthService } from 'src/services/auth.service';
import { UtilsService } from 'src/services/utils.service';
import { CepService } from 'src/services/cep.service';
import { stringify } from 'querystring';


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
  selector: 'app-professional-form',
  templateUrl: './professional-form.component.html',
  styleUrls: ['./professional-form.component.scss'],
})
export class ProfessionalFormComponent implements OnInit {
  professional: Professional = new Professional();
  professionalFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;
  specialties: Specialty[] = []
  states: string[] = [
    // tslint:disable-next-line:max-line-length
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];
  fileRG: any;
  fileCPF: any;
  fileCRM: any;
  fileEnd: any;
  filePhoto: any;  
  fileTerm: any;
  changed: boolean = false
  personTypes: any[] =
    [ { key: "CPF", value: "Pessoa Física" }
    , { key: "CNPJ", value: "Pessoa Jurídica" }
    ];
    
  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private professionalService: ProfessionalService,
    private specialtyService: SpecialtyService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    private fileService: FileServive,
    private dataService: DataService,
    private router: Router,
    private utilService: UtilsService,
    private cepService: CepService
    

  ) {
    this.professionalFormGroup = this.formBuilder.group({
      name: new FormControl({ value: '', validator: [Validators.required] }),
      cpf: new FormControl('', [Validators.required, Validators.minLength(11),this.cpfValidator]),
      rg: new FormControl('', [Validators.required]),
      crm: new FormControl('', [Validators.required]),
      ufcrm: new FormControl('', [Validators.required]),
      phone: new FormControl('', [Validators.required]),
      address: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
      specialty: new FormControl(new Specialty(), [Validators.required]),
      username: new FormControl('', [Validators.required, Validators.email]),
      documentType: new FormControl([Validators.required]),
      nacionalidade: new FormControl([Validators.required]),
      estadoCivil: new FormControl([Validators.required]),
      logradouro: new FormControl([Validators.required]),
      numero: new FormControl([Validators.required]),
      bairro: new FormControl([Validators.required]),
      cidade: new FormControl([Validators.required]),
      uf: new FormControl([Validators.required]),
      url: new FormControl('',[Validators.required, Validators.minLength(11)]),
      // password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    //confirmPassword: new FormControl('', [Validators.required, confirmPasswordValidator]),
    })
  }

  ngOnInit(): void {
    this.professional.tipoDocumento = 'CPF'
    this.getSpecialties()
      }
  setFile(type: string, event: any) {
    const file = event.target.files[0]
    if (type === 'rg') {
      this.fileRG = file
    } else if (type === 'cpf') {
      this.fileCPF = file
    }else if (type === 'crm') {
      this.fileCRM = file
    }else if (type === 'end') {
      this.fileEnd = file
    } else if(type === 'foto'){
      this.filePhoto = file
    }else {
      this.fileTerm = file
    }
    this.changed = true;
      
    }
  getProfessional(id: string | number) {
    this.professionalService.getProfessionalById(id).subscribe((professional: Professional) => {
      this.professional = professional
      this.professionalFormGroup.removeControl('password')
      this.professionalFormGroup.removeControl('confirmPassword')
      this.isLoading = false;
      // tslint:disable-next-line:max-line-length
      this.professional.especialidade = this.professional.especialidade && this.specialties.find(specialty => specialty.id === this.professional.especialidade.id)
    }, error => {
      this.isLoading = false;
      console.log(error)
    })
  }
  getSpecialties() {
    this.isLoading = true;
    this.specialtyService.getProfessionalSpecialties().subscribe((spacialties: Specialty[]) => {
      this.specialties = [...spacialties]
      this.route.params.subscribe(params => {
        if (params.id === 'new') {
          this.isLoading = false;
          this.isEdit = false;
        } else {
          params.id && (this.isEdit = true)
          params.id && this.getProfessional(params.id)
          this.professionalFormGroup.disable()
        }
      })
    }, error => {
      console.log(error);
      this.isLoading = false;

    })
  }
  formProfessionalHasError(control: any, error: string) {
    return this.professionalFormGroup.controls[control].hasError(error);
  }
  canDelete() {
    return this.authService.getAccessType() === 'ROLE_ADMINISTRADOR';
  }
  getProfessionals() {
    this.professionalService.getProfessionals().subscribe((professionals: any[]) => {
      this.dataService.badgeCount = professionals.filter(professional => professional.status === 'AGUARDANDO_APROVAÇÃO').length
    }, error => {
      console.log(error)
    })
  }
  uploadFile(formData: FormData, type: string) {
    this.fileService.fileUpload(formData).subscribe(res => {
      if (!res.erro) {
        if (type === 'cpf') {
          this.fileCPF = undefined;
          this.professional.imagecpf = `${res.filename}`;
        } else if (type === 'rg') {
          this.fileRG = undefined
          this.professional.imagerg = `/${res.filename}`;
        } else if (type === 'crm') {
          this.fileCRM= undefined
          this.professional.imagecrm = `${res.filename}`;
        } else if (type === 'end') {
          this.fileEnd = undefined
          this.professional.imageComprovante = `${res.filename}`;
        } else if(type === 'foto'){
          this.filePhoto = undefined;
          this.professional.imageFoto = `${res.filename}`;
        }else {
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
  inputEvent($event) {
    this.changed = true;
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
      }, _ => {})
    }
  }

  onSubmit() {

       const validation = (/^[0-9a-zA-Z-]+$/).test(this.professional.url)
      if (!validation){
        const erroMessage = "URL não pode conter espaços, e caracteres especiais"
        this.openSnackBar(erroMessage, 'Ok', 5000);
        this.isLoading = false;
        console.log(erroMessage)
        return
      } 
     
     
     
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
  
        }else if (this.fileEnd) {
  
          const formData: FormData = new FormData()
  
          formData.append('uploadfile', this.fileEnd)
          this.uploadFile(formData, 'end')
  
        }else if (this.filePhoto) {
  
          const formData: FormData = new FormData()
  
          formData.append('uploadfile', this.filePhoto)
          this.uploadFile(formData, 'photo')
  
        }else if (this.fileTerm) {
  
          const formData: FormData = new FormData()
  
          formData.append('uploadfile', this.fileTerm)
          this.uploadFile(formData, 'term')
  
        } else {
          this.isEdit ? this.updateProfessional() : this.createProfessional();
        }
      
  
    }
    getprevia() {
      if (this.professional.url === null)
        return ''
  
      else
        return window.location.origin + '/#/auth/' + this.professional.url + '/login';
    }
  createProfessional() {
    
    const professional = this.professional
    delete professional.confirmPassword
    this.professional.url = this.professional.nome.replace(" ","-") + this.professional.crm   
    this.professionalService.createProfessional(
      { ...professional, especialidade: { id: professional.especialidade.id }, tipoDocumento: professional.tipoDocumento }
    ).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Profissional criado com sucesso!', 'Ok', 3000);
      this.router.navigate(["profissionais"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }
  updateProfessional() {
    this.professional.url = this.professional.nome.replace(" ","-") + this.professional.crm   
    this.professionalService.updateProfessional(this.professional.id, this.professional).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Profissional atualizado com sucesso!', 'Ok', 3000);
      this.router.navigate(["profissionais"], { replaceUrl: true })
    }, error => {
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
      console.log(error)
    })
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
      panelClass: 'teste'

    });
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
          this.getProfessional(this.professional.id);
          this.getProfessionals()
        }, error => {
          console.log(error);
          this.getProfessionals()
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
          this.getProfessional(this.professional.id);
          this.router.navigate(["profissionais"], { replaceUrl: true })
          this.getProfessionals()
        }, error => {
          console.log(error)
          this.getProfessionals()
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
  changeDocumentType(){
    this.professional.cpf = ""
  }

  dowloadTermoAceitePdf() {
    this.professionalService.getDownloadPdfTerms(this.professional.id).subscribe(response => {
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

     
 } 


 cpfValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  if (!control.parent || !control) {
    return null;
  }
  const cpf = control.parent.get('cpf').value
  const cnpj = control.parent.get('cpf').value
  const tipoPessoa = control.parent.get('documentType').value
  if(tipoPessoa === 'CPF'){
    if(this.utilService.validaCPF(cpf)){
      return null;
    }
    return { invalidCpf: true };
  }else{
    if(this.utilService.validaCNPJ(cnpj)){
      return null;
    }
    return { invalidCnpj: true };

  }
  
   
};
}
