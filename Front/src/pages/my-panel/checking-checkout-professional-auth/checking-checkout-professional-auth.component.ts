import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { AuthService } from 'src/services/auth.service';
import { CheckinService } from 'src/services/checkin.service';

@Component({
  selector: 'app-checking-checkout-professional-auth',
  templateUrl: './checking-checkout-professional-auth.component.html',
  styleUrls: ['./checking-checkout-professional-auth.component.scss']
})
export class CheckingCheckoutProfessionalAuthComponent implements OnInit {

  checkingCheckoutAuthFormGroup: FormGroup
  authProfessional: any = {}
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false

  constructor(
    public dialogRef: MatDialogRef<CheckingCheckoutProfessionalAuthComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private checkingService: CheckinService
  ) {
    this.checkingCheckoutAuthFormGroup = this.formBuilder.group({
      username: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
  }
  formCheckingCheckoutAuthHasError(control: any, error: string) {
    return this.checkingCheckoutAuthFormGroup.controls[control].hasError(error);
  }

  onSubmit() {
    if (this.checkingCheckoutAuthFormGroup.valid) {
      this.login()
    }
  }
  cancel() {
    this.dialogRef.close(false)
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
  login() {
    this.authService.login(this.authProfessional).subscribe(res => {
      const check: any = {
        token: res.token,
        agendamentoId: this.data.currentScheduling.id,
      }
      if (this.data.status === 'checkin') {
        this.submitChecking(check)
      } else if (this.data.status === 'checkout') {
        this.submitCheckout(check)
      }
    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }

  submitChecking(checkin: any) {
    this.checkingService.submitAssistantCheckinProfessional(checkin).subscribe(res => {
      this.openSnackBar('Check-in realizado com sucesso!', 'Ok', 3000)
      this.dialogRef.close()
    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }
  submitCheckout(checkout: any) {
    this.checkingService.submitAssistantCheckoutProfessional(checkout).subscribe(res => {
      this.openSnackBar('Check-out realizado com sucesso!', 'Ok', 3000)
      this.dialogRef.close()
    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }

}
