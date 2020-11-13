import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators, FormBuilder, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SchedulingService } from 'src/services/scheduling.service';
import { AuthService } from 'src/services/auth.service';
import { UtilsService } from 'src/services/utils.service';

@Component({
  selector: 'app-finish-sale',
  templateUrl: './finish-sale.component.html',
  styleUrls: ['./finish-sale.component.scss']
})
export class FinishSaleComponent implements OnInit {
  finishSaleFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  finishSale: any = {
    carrinho: this.data.cartNumber,
    cardHolderName: "",
    number: "",
    expirationMonth: "",
    expirationYear: "",
    securityCode: "",
    brandName: "",
    holderName: "",
    holderCpf: "",
    holderEmail: "",
    holderPhone: "",
    mesmosDadosMedico: true,
    isAtendente: this.authService.getAccessType() === 'ROLE_ATENDENTE'
  };
  months: any[] = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
  years: any[] = []
  cardMaxLength: number = 16
  loaded: boolean = false;
  myMaskCardNumber = '0000 0000 0000 0000'
  securityCodeMask = '000'


  constructor(
    public dialogRef: MatDialogRef<FinishSaleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private schedulingService: SchedulingService,
    private utilService: UtilsService,
    private authService: AuthService
  ) {
    this.finishSaleFormGroup = this.formBuilder.group({
      cardholder: new FormControl('', [Validators.required]),
      cardNumber: new FormControl('', [Validators.required]),
      brandName: new FormControl('', [Validators.required, Validators.minLength(3)]),
      expirationMonth: new FormControl('', [Validators.required]),
      expirationYear: new FormControl('', [Validators.required]),
      securityCode: new FormControl('', [Validators.required]),
      holderCpf: new FormControl('', [Validators.required,Validators.minLength(11), this.cpfValidator]),
      mesmosDadosMedico: new FormControl('', [Validators.nullValidator]),
    })
    this.finishSaleFormGroup.controls.brandName.disable()
  } 1

  ngOnInit(): void {
    this.setupYears()
  }
  setupYears() {
    const thisYear = new Date().getFullYear();
    for (let i = 0; i < 11; i++) {
      this.years[i] = thisYear + i;
    }
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
      panelClass: 'teste'

    });
  }

  cpfValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (!control.parent || !control) {
      return null;
    }
    const cpf = control.parent.get('holderCpf').value


    if (this.utilService.validaCPF(cpf)) {
      return null;
    }
    return { invalidCpf: true };


  };
  formFinishSaleHasError(control: any, error: string) {
    return this.finishSaleFormGroup.controls[control].hasError(error);
  }

  onSubmit() {
    if (this.finishSaleFormGroup.valid) {
      this.isLoading = true;
      this.schedulingService.finishSale(this.finishSale).subscribe(res => {
        this.dialogRef.close(true)
        this.openSnackBar('Reservas pagas com sucesso!', 'Ok', 5000)
        this.isLoading = false;
      }, error => {
        console.log(error)
        this.dialogRef.close('error')
        this.openSnackBar(error.error.message, 'Ok', 5000)
        this.isLoading = false;
      })
    }
  }
  changeCreditCardNumber(event) {
    this.finishSale.brandName = this.selectBrandName(this.finishSale.number)
  }
  changeCheckboxUserData(event) {
    if (event.checked) {
      this.finishSaleFormGroup.removeControl('holderName')
      this.finishSaleFormGroup.removeControl('holderEmail')
      this.finishSaleFormGroup.removeControl('holderPhone')
    } else {
      this.finishSaleFormGroup.addControl(
        'holderName',
        new FormControl(this.finishSale.holderName, [Validators.required])
      );
      this.finishSaleFormGroup.addControl(
        'holderEmail',
        new FormControl(this.finishSale.holderEmail, [Validators.required, Validators.email])
      );
      this.finishSaleFormGroup.addControl(
        'holderPhone',
        new FormControl(this.finishSale.holderPhone, [Validators.required])
      );
    }
  }
  selectBrandName(cardNumber): string {
    this.cardMaxLength = 16;
    this.myMaskCardNumber = '0000000000000000'
    this.securityCodeMask = '000'
    if (cardNumber === '') {
      return '';
    }
    if (/^(4026|417500|4405|4508|4844|4913|4917)\d+$/.test(cardNumber)) {
      return 'ELECTRON';
    }
    if (/^(5018|5020|5038|5612|5893|6304|6759|6761|6762|6763|0604|6390)\d+$/.test(cardNumber)) {
      return 'MAESTRO';
    }
    if (/^(5019)\d+$/.test(cardNumber)) {
      return 'DANKORT';
    }
    if (/^(636)\d+$/.test(cardNumber)) {
      return 'INTERPAYMENT';
    }
    if (/^(62|88)\d+$/.test(cardNumber)) {
      return 'UNIONPAY';
    }
    if (/^((((636368)|(438935)|(504175)|(451416)|(636297))\d{0,10})|((5067)|(4576)|(4011))\d{0,12})$/.test(cardNumber)) {
      return 'ELO';
    }
    if (/^4[0-9]{6,}$/.test(cardNumber)) {
      return 'VISA';
    }
    if (/^5[1-5][0-9]{5,}$|^(222[1-9]|2[3-6][0-9][0-9]|27[0-1][0-9]|2720)[0-9]{12}$/.test(cardNumber)) {
      return 'MASTER';
    }
    if (/^3[47][0-9]{5,}$/.test(cardNumber)) {
      this.cardMaxLength = 15;
      this.myMaskCardNumber = '000000000000000'
      this.securityCodeMask = '0000'
      return 'AMEX';
    }
    if (/^3(?:0[0-5]|[68][0-9])[0-9]{11}$/.test(cardNumber)) {
      return 'DINERS';
    }
    if (/^6(?:011|5[0-9]{2})[0-9]{12}$/.test(cardNumber)) {
      return 'DISCOVER';
    }
    if (/^(?:2131|1800|35\d{3})\d{11}$/.test(cardNumber)) {
      return 'jcb';
    }
    return undefined;
  }
}
