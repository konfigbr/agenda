import { Component, OnInit, Inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { ConsultaService } from 'src/services/consulta.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/services/auth.service';

@Component({
  selector: 'app-appointment-administrative-modal',
  templateUrl: './appointment-administrative-modal.component.html',
  styleUrls: ['./appointment-administrative-modal.component.scss']
})
export class AppointmentAdministrativeModalComponent implements OnInit {

  errorTableData: boolean = false;

  status: string = ''
  statusSelected: string =''
  observacao: string =''
  id: string = ''
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

  apointmentAdministrativeFormGroup: FormGroup

  constructor(
    private router: Router,
    private consultaService: ConsultaService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<AppointmentAdministrativeModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.status = data.status
    this.id = data.id
    this.statusDisponiveis = this.getStatus(this.status)
    this.apointmentAdministrativeFormGroup = this.formBuilder.group({
      statusSelected: new FormControl('', [Validators.nullValidator]),
      observacao: new FormControl('', [Validators.nullValidator]),
    })
  }

  ngOnInit(): void { }

  close(confirm: boolean) {
    this.dialogRef.close(confirm);
   
  }

  onSubmit() {
    
      if (this.apointmentAdministrativeFormGroup.valid) {
      const status = {
        status: this.statusSelected,
        observacao: this.observacao
      }
      this.consultaService.updateStatus(this.id, status).subscribe((res: any) => {
        this.openSnackBar('Consulta editada com sucesso !', 'Ok', 3000);
               this.close(true)
                  }, error => {
        /* this.openSnackBar(error.error.message, 'Ok', 3000); */
        this.openSnackBar('Falha ao editar o status selecione uma opção !', 'Ok', 15000);
        this.close(false)
        
      })
    }
  }


  formSelectedStatuHasError(control: any, error: string) {
    return this.apointmentAdministrativeFormGroup.controls[control].hasError(error);
  }

  getAccessType() {
    return this.authService.getAccessType()
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
