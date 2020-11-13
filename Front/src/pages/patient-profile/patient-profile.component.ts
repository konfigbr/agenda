import { AuthService } from 'src/services/auth.service';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import Specialty from 'src/models/specialty.model';
import { MatDialog } from '@angular/material/dialog';
import { MessageProfessionalAproveComponent } from 'src/components/message-professional-aprove/message-professional-aprove.component';
import { ShowImagesModalComponent } from 'src/components/show-images-modal/show-images-modal.component';
import { FileServive } from 'src/services/file.service';
import Patient from 'src/models/patient.model';
import { PatientService } from 'src/services/patient.service';
import { UtilsService } from 'src/services/utils.service';
import { CepService } from 'src/services/cep.service';


@Component({
  selector: 'app-patient-profile',
  templateUrl: './patient-profile.component.html',
  styleUrls: ['./patient-profile.component.scss']
})
export class PatientProfileComponent implements OnInit {

  patient: Patient = new Patient();
  patientFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;
  specialties: Specialty[] = []
  changed: boolean = false

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    private utilService: UtilsService,
    private cepService: CepService,
    private snackBar: MatSnackBar,
    private patientService: PatientService,
    private authService: AuthService,
    private router: Router
  ) {
    this.patientFormGroup = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      cpf: new FormControl('', [Validators.required, Validators.minLength(11),this.cpfValidator]),
      rg: new FormControl('', [Validators.required]),
      address: new FormControl('', [Validators.required]),
      telefone: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
      username: new FormControl('', [ Validators.email]),

    })
  }

  ngOnInit(): void {
    const professionalKey = this.route.snapshot.params.professionalKey
    if (this.getProfessionalKey() !== professionalKey)
      return this.authService.logoutPatient(professionalKey)
      
    this.patientFormGroup.controls.username.disable()
    this.getMe()
  }


  getMe() {
    this.isLoading = true;
    this.patientService.getMe().subscribe((patient: Patient) => {
      this.patient = patient
      this.isLoading = false;         

    }, error => {
      this.isLoading = false;
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }

  inputEvent($event) {
    this.changed = true;
  }
  showMessage() { }

  cpfValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (!control.parent || !control) {
      return null;
    }
    const cpf = control.parent.get('cpf').value


    if (this.utilService.validaCPF(cpf)) {
      return null;
    }
    return { invalidCpf: true };


  };
  getCep() {
    const cep = this.patient.cep
    console.log(cep)

    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.patient.endereco = data.logradouro
         
        }
      }, _ => {})
    }
  }


  formPatientHasError(control: any, error: string) {
    return this.patientFormGroup.controls[control].hasError(error);
  }

  onSubmit() {

    if (this.patientFormGroup.valid) {    
       this.updateProfessional()     
    }


  }
   updateProfessional() {
    this.isLoading = true;
    this.patientService.updatePatient(this.patient.id, this.patient).subscribe(response => {
      this.isLoading = false;
      this.router.navigate([`${this.getProfessionalKey()}/minhas-consultas`], { replaceUrl: true })
      this.openSnackBar('Perfil atualizado com sucesso!', 'Ok', 3000);
      
    }, error => {
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
      console.log(error)
    })
  } 

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
  
  getProfessionalKey() {
    return localStorage.getItem("professionalKey")
  }

  getProfessionalObj() {
    return JSON.parse(localStorage.getItem("professionalObj"))
  }
}


