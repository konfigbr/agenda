import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import UserPatientRegister from 'src/models/auth/user-patient-register.model';
import { MatDialog } from '@angular/material/dialog';
import { UserTermsModalComponent } from 'src/components/user-terms-modal/user-terms-modal.component';
import TermOfUse from 'src/models/term-of-use.model';
import { ProfessionalService } from 'src/services/professional.service';
import Professional from 'src/models/professional.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PatientService } from 'src/services/patient.service';
import Patient from 'src/models/patient.model';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { CepService } from 'src/services/cep.service';

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
  selector: 'app-professional-register',
  templateUrl: './professional-register.component.html',
  styleUrls: ['./professional-register.component.scss']
})
export class ProfessionalRegisterComponent implements OnInit {
 isLoading: boolean = false
 userRegister: UserPatientRegister = new UserPatientRegister()
 selectedTermOfUse: TermOfUse;
 termsOfUse: TermOfUse[] = []
 professional: Professional = new Professional();
 patient: Patient = new Patient();
 matcher = new MyErrorStateMatcher();

 formGroupUserRegister: FormGroup;
  constructor(
    private router: Router, 
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private matDialog: MatDialog,
    private professionalService: ProfessionalService,
    private snackBar: MatSnackBar,
    private patientService: PatientService,
    private cepService: CepService,
  ) {
    this.formGroupUserRegister = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]), 
      document: new FormControl('', [Validators.required]),
      rg: new FormControl('', [Validators.required]),
      endereco: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
    })
  }

 

  ngOnInit(): void {
      this.verifyProfessionalKey()     
  }
  
 verifyProfessionalKey(){    
    this.isLoading = true 
    const professionalKey = this.route.snapshot.params.professionalKey
   this.professionalService.getProfessionalByUrl(professionalKey).subscribe((professional: Professional) => {
      this.professional = professional   
      
      if(this.professional.url === professionalKey){
        this.isLoading = false         
      }
    }, error => {
      this.router.navigate(["auth/login"], { replaceUrl: true })
      this.openSnackBar('Profissional não encontrado!', 'Ok', 5000);
    })    
    
  }

  getProfessionalByUrl(url: string | number) {
    this.professionalService.getProfessionalByUrl(url).subscribe((professional: Professional) => {
      this.professional = professional   
    
    })
    
  }

  getCep() {
    const cep = this.professional.cep

    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.patient.endereco = data.logradouro
        }
      }, _ => {})
    }
  }

  onSubmitRegister() {
    this.isLoading = true;
    this.patientService.createPatient({...this.patient,idProfissional: this.professional.id}).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Perfil de paciente criado com sucesso!', 'Ok', 3000);
      this.router.navigate(["auth/login"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }


  formRegisterHasError(control: any, error: string) {
    return this.formGroupUserRegister.controls[control].hasError(error);
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

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
      panelClass: 'teste'

    });
  }

}
