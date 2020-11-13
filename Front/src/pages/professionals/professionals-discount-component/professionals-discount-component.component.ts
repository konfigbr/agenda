import { Component, OnInit, Inject, Input, Output, EventEmitter } from '@angular/core';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { FormControl, Validators, FormBuilder, FormGroup } from '@angular/forms';
import { DiscountService } from 'src/services/discount.service';
import Discount from 'src/models/discount-model';
import {MessageModalComponent} from '../../../components/message-modal/message-modal.component';

@Component({
  selector: 'app-professionals-discount-component',
  templateUrl: './professionals-discount-component.component.html',
  styleUrls: ['./professionals-discount-component.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
  ]
})
export class ProfessionalsDiscountComponentComponent implements OnInit {
  @Input() selectedFunction:string;
  @Input() editDiscount:Discount;
  discountRegister: Discount = new Discount();
  @Input() discountEditId: number;
  @Output() stepEvent = new EventEmitter<string>();
  @Output() refreshUsersEvent = new EventEmitter<boolean>();

  discountType = new FormControl('', [
    Validators.required
  ]);
  discountValue = new FormControl('', [
    Validators.required,
  ]);
  discountStart = new FormControl('', [
    Validators.required,
  ]);
  discountEnd = new FormControl('', [
    Validators.required,
  ]);

  formGroupDiscountRegister: FormGroup;
  constructor(
    private discountService: DiscountService,
    private formBuilder: FormBuilder,
    private adapter: DateAdapter<any>,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.adapter.setLocale('pt-br');
    this.formGroupDiscountRegister = this.formBuilder.group({
      type: new FormControl('', [Validators.required]),
      value: new FormControl('', [Validators.required, Validators.min(0), Validators.max(100)]),
      startDate: new FormControl('', [Validators.required]),
      endDate: new FormControl('', [Validators.required])
    });
  };

  ngOnInit(): void {
    if(this.selectedFunction==="EDIT_DISCOUNT"){
      console.log(this.editDiscount);
      this.discountRegister = this.editDiscount;

    }
  }

  sendStep(step:string) {
    this.stepEvent.emit(step);
  }

  sendRefreshUsers(){
    this.refreshUsersEvent.emit(true);
  }

  onSubmitRegisterDiscount() {
    let dataInicio = this.discountRegister.dataInicio;
    let dataFim = this.discountRegister.dataFim;
    let newDiscount: Discount = {
      tipo: this.discountRegister.tipo,
      percentual: this.discountRegister.percentual,
      dataInicio: dataInicio,
      dataFim: dataFim,
      ativo: true,
      profissional: {id:this.data.professional}
    }
    this.discountService.getCurrentDiscounts(newDiscount).subscribe((discountFound)=>{
      if(discountFound){
        const dialogRef = this.dialog.open(MessageModalComponent,{
          width:'300px',
          data:{apagarText:'Editar', message:'Já existe um registro de desconto nesse periodo de tempo, deseja edita-lo?'}
        });

        dialogRef.afterClosed().subscribe(result =>{
          if(result){
            this.selectedFunction = "EDIT_DISCOUNT";
            this.discountRegister = discountFound;
          }else{
            this.sendStep('SELECTION');
          }
        });
        
      }else{
        this.discountService.createDiscount(newDiscount).subscribe((discount:Discount) =>{
          this.openSnackBar('Cadastro Realizado com sucesso', 'OK', 3000);
          this.sendRefreshUsers();
          this.sendStep('LIST');
        }, error =>{
          this.openSnackBar(error.error.message, 'OK', 3000);
        });
      }
    }, error=>{
      this.openSnackBar(error.error.message, 'OK', 3000);
    }); 
  }

  onSubmitEditDiscount(){
    console.log(this.discountRegister);

    this.discountService.updateDiscount(this.discountRegister.id, this.discountRegister).subscribe((discount:Discount)=>{
      this.openSnackBar('Desconto atualizado com sucesso', 'OK', 3000);
      this.sendRefreshUsers();
      this.sendStep('SELECTION');
    }, error =>{
      this.openSnackBar(error.error.message, 'OK', 3000);
    });
    
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

}