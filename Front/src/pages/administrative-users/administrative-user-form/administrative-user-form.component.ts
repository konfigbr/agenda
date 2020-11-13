import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from 'src/services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

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
  selector: 'app-administrative-user-form',
  templateUrl: './administrative-user-form.component.html',
  styleUrls: ['./administrative-user-form.component.scss']
})
export class AdministrativeUserFormComponent implements OnInit {

  // equipmentType$: Observable<EquipmentType> = this.store.select(selectEquipmentType)
  administrativeUser: any = {};
  administrativeUserFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;
  confirmPassword: string = ""

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.administrativeUserFormGroup = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      username: new FormControl('', [Validators.required, Validators.email]),
      accessType: new FormControl('', [Validators.required]),
      // password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      // confirmPassword: new FormControl('', [Validators.required, confirmPasswordValidator])
    })
  }

  ngOnInit(): void {
    this.isLoading = true

    this.route.params.subscribe(params => {
      if (params.id === 'new') {
        this.isEdit = false;
        setTimeout(() => {
          this.isLoading = false

        }, 3000)
      } else {
        this.isEdit = true
        params.id && this.getAdministrativeUser(params.id)
        this.administrativeUserFormGroup.removeControl('password')
        this.administrativeUserFormGroup.removeControl('confirmPassword')
      }
    })
  }

  getAdministrativeUser(id: string | number) {
    this.userService.getAdministrativeUserById(id).subscribe(res => {
      this.administrativeUser = res
      this.isLoading = false
    }, error => {
      this.isLoading = false
      console.log(error)
    })
  }

  formAdministrativeUserHasError(control: any, error: string) {
    return this.administrativeUserFormGroup.controls[control].hasError(error);
  }
  onSubmit() {
    if (this.administrativeUserFormGroup.valid) {
      this.isLoading = true;
      this.isEdit ? this.updateAdministrativeUser() : this.createAdministrativeUser();
    }

  }
  createAdministrativeUser() {
    this.userService.createAdministrativeUsers(this.administrativeUser).subscribe(res => {
      this.isLoading = false;
      this.openSnackBar('Usuário administrativo criado(a) com sucesso!', 'Ok', 3000);
      this.router.navigate(["usuarios-administrativos"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }
  updateAdministrativeUser() {
    this.userService.updateAdministrativeUsers(this.administrativeUser).subscribe(res => {
      this.isLoading = false;
      this.openSnackBar('Atendente atualizado(a) com sucesso!', 'Ok', 3000);
      this.router.navigate(["usuarios-administrativos"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
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
