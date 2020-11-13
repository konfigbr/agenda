import { Component, OnInit, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ConsultaService } from 'src/services/consulta.service';
import { AuthService } from 'src/services/auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';

@Component({
  selector: 'app-professionals-discount-modal',
  templateUrl: './professionals-discount-modal.component.html',
  styleUrls: ['./professionals-discount-modal.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
  ]
})
export class ProfessionalsDiscountModalComponent implements OnInit {
  functionSelected:string;
  errorTableData: boolean = false;
  step: string = 'SELECTION'
  status: string = ''
  statusSelected: string =''
  justify: string =''
  id: string = ''
  refreshUsers:boolean = false;
  statusList = [
    { label: 'Solicitada', value: 'SOLICITADA' },
    { label: 'Aprovada', value: 'APROVADA' },
    { label: 'Confirmada', value: 'CONFIRMADA' },
    { label: 'Não confirmada', value: 'NAO_CONFIRMADA' },
    { label: 'Cancelada', value: 'CANCELADA' },
  ]
  statusDisponiveis = []

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  discountFormGroup: FormGroup

  constructor(
    private router: Router,
    private consultaService: ConsultaService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private adapter: DateAdapter<any>,
    public dialogRef: MatDialogRef<ProfessionalsDiscountModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.adapter.setLocale('pt-br');
    this.status = data.status
    this.id = data.id
    this.statusDisponiveis = this.getStatus(this.status)
    this.discountFormGroup = this.formBuilder.group({
      statusSelected: new FormControl('', [Validators.nullValidator]),
      justify: new FormControl('', [Validators.nullValidator]),
    })
  }

  ngOnInit(): void { }

  close(confirm: boolean) {
    this.dialogRef.close(confirm);
    
  }

  onSubmit() {
    
      if (this.discountFormGroup.valid) {
      const status = {
        status: this.statusSelected
      }
      this.consultaService.updateStatus(this.id, status).subscribe((res: any) => {
        this.openSnackBar('Consulta editada com sucesso !', 'Ok', 3000);
        this.close(true);
      }, error => {
        /* this.openSnackBar(error.error.message, 'Ok', 3000); */
        this.openSnackBar('Falha ao editar o status selecione uma opção !', 'Ok', 15000);
        this.close(false);
        
      })
    }
  }


  formSelectedStatuHasError(control: any, error: string) {
    return this.discountFormGroup.controls[control].hasError(error);
  }

  getAccessType() {
    return this.authService.getAccessType()
  }

  goToStep(step:string){
    this.functionSelected = step;
    this.step=step
  }

  receiveStep($event: string){
    this.step = $event;
  }

  receiveRefreshUsers($event: boolean){
    this.refreshUsers = $event;
  }

  getStatus(status) {
    const accessType = this.authService.getAccessType()

    if (status === 'SOLICITADA') {

      return [
        {label: 'Aprovada', value: 'APROVADA'},
        {label: 'Não Aprovada', value: 'NAO_APROVADA'},
        {label: 'Cancelada', value: 'CANCELADA'}
            
      ]

    } else if (status === 'APROVADA') {
      return [
        {label: 'Confirmada', value: 'CONFIRMADA'},
        {label: 'Não confirmada', value: 'NAO_CONFIRMADA'}
      ]
    }else if (status === 'NAO_CONFIRMADA') {
      return [
        {label: 'Reagendada', value: 'REAGENDADA'},
        {label: 'Cancelada', value: 'CANCELADA'}
      ]
    } else if (status === 'NAO_APROVADA' && (accessType === 'ROLE_ATENDENTE' || accessType === 'ROLE_PROFISSIONAL')) {
      return [
        {label: 'Reagendada', value: 'REAGENDADA'},
        {label: 'Cancelada', value: 'CANCELADA'}
      ]
    }else {
      return []
    }
    
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

}


