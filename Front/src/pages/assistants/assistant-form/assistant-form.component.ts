import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import Assistant from 'src/models/assistant.model';
import { AssistantService } from 'src/services/assistant.service';
import { UtilsService } from 'src/services/utils.service';
import { CepService } from 'src/services/cep.service';


export const confirmPasswordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  if (!control.parent || !control) {
    return null;
  }
  const password = control.parent.get('passwordRegisterFormControl');
  const confirmPassword = control.parent.get('confirmPasswordRegisterFormControl');
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
  selector: 'app-assistant-form',
  templateUrl: './assistant-form.component.html',
  styleUrls: ['./assistant-form.component.scss']
})
export class AssistantFormComponent implements OnInit {
  assistant: Assistant = new Assistant();
  assistantFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private assistantService: AssistantService,
    private snackBar: MatSnackBar,
    private utilService: UtilsService,
    private cepService: CepService,
    private router: Router

  ) {
    this.assistantFormGroup = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      cpf: new FormControl('', [Validators.required, Validators.minLength(11), this.cpfValidator]),
      rg: new FormControl('', [Validators.required]),
      phone: new FormControl('', [Validators.required]),
      address: new FormControl('', [Validators.required]),
      cep: new FormControl('', [Validators.required]),
      username: new FormControl('', [Validators.required]),
      // password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      // confirmPassword: new FormControl('', [Validators.required, confirmPasswordValidator]),
      conclusionYear: new FormControl('', [Validators.required]),
      college: new FormControl('', [Validators.required]),
      courseName: new FormControl('', [Validators.required]),
    })
  }
  ngOnInit(): void {

    this.route.params.subscribe(params => {

      if (params.id === 'new') {
        this.isEdit = false;
      } else {
        params.id && (this.isEdit = true)
        params.id && this.getAssistant(params.id)
      }
    })
  }

  getAssistant(id: string | number) {
    this.assistantService.getAssistantById(id).subscribe((assistant: Assistant) => {
      this.assistant = assistant
      this.assistantFormGroup.removeControl('password')
      this.assistantFormGroup.removeControl('confirmPassword')
    }, error => {
      console.log(error)
    })
  }


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
    const cep = this.assistant.cep

    if (cep) {
      this.cepService.getCep(cep).subscribe(data => {
        if (!data.erro) {
          this.assistant.endereco = data.logradouro
        }
      }, _ => {})
    }
  }


  formAssistantHasError(control: any, error: string) {
    return this.assistantFormGroup.controls[control].hasError(error);
  }
  onSubmit() {
    if (this.assistantFormGroup.valid) {
      this.isLoading = true;
      this.isEdit ? this.updateAssistant() : this.createAssistant();
    }

  }
  createAssistant() {
    this.assistantService.createAssistant(this.assistant).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Assistente criado(a) com sucesso!', 'Ok', 3000);
      this.router.navigate(["assistentes"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }
  updateAssistant() {
    this.assistantService.updateAssistant(this.assistant.id, this.assistant).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Assistente atualizado(a) com sucesso!', 'Ok', 3000);
      this.router.navigate(["assistentes"], { replaceUrl: true })
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

}
