import { Component, OnInit, Inject } from '@angular/core';
import Professional from 'src/models/professional.model';
import Scheduling from 'src/models/scheduling.model';
import { DateAdapter } from 'angular-calendar';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS } from '@angular/material-moment-adapter';
import { MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';
import { AppointmentHour } from 'src/models/appointment-hour-model';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { ProfessionalService } from 'src/services/professional.service';
import { DateService } from 'src/services/date.service';
import { UtilsService } from 'src/services/utils.service';
import { MatTableDataSource } from '@angular/material/table';
import { CreateAppointmentModel } from 'src/models/create-appointment.model';
import { AlertModalComponent } from 'src/app/alert-modal/alert-modal.component';
import { getYear, getMonth, getDate } from 'date-fns';

@Component({
  selector: 'app-rescheduling-modal',
  templateUrl: './rescheduling-modal.component.html',
  styleUrls: ['./rescheduling-modal.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
  ]
})
export class ReschedulingModalComponent implements OnInit {

  profissional: Professional
  agendamento: Scheduling

  isLoading: boolean = false;
  dialogTitle: string
  dataAtual: string

  horariosOcupados: AppointmentHour[]

  horariosDisponiveis: AppointmentHour[]
  displayedColumns: string[] = ['dataInicio', 'dataFim']
  dataSourceHorariosOcupados = null
  dataSourceHorariosDisponiveis = null

  eventForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<ReschedulingModalComponent>, 
    @Inject(MAT_DIALOG_DATA) public data: any,
    private matDialog: MatDialog,
    private utilsService: UtilsService,
    private dateService: DateService,
    private professionalService: ProfessionalService
  ) {
    this.dataAtual = data.dataInicioReserva
    this.horariosOcupados = data.horariosOcupados
    this.horariosDisponiveis = data.horariosDisponiveis
    this.profissional = data.profissional
    this.agendamento = data.agendamento

    this.eventForm = new FormGroup({
      horaInicio: new FormControl(this.formatDate(data.dataInicioReserva, 'HHmm'), Validators.required),
      horaFim: new FormControl(this.formatDate(data.dataFimReserva, 'HHmm'), Validators.required),
      motivo: new FormControl('', Validators.required),
    });

    this.dataSourceHorariosOcupados = new MatTableDataSource(this.horariosOcupados)
    this.dataSourceHorariosDisponiveis = new MatTableDataSource(this.horariosDisponiveis)
    this.dialogTitle = `Nova consulta com Doutor(a) ${this.profissional.nome}`;
  }

  ngOnInit(): void {
  }

  save() {
    if (this.eventForm.invalid) {
      this.showAlert('Preencha todos os dados corretamente!')
      return
    }

    this.isLoading = true

    const appointment: CreateAppointmentModel = {
      dataInicio: this.extractDateFromFormControl('horaInicio'),
      dataFim: this.extractDateFromFormControl('horaFim'),
      agendamentoId: this.agendamento?.id,
      motivo: this.eventForm.get('motivo').value,
      pacienteId: null
    }

    this.professionalService.createMedicalAppointment(Number(this.profissional.id), appointment).subscribe(consulta => {
      this.isLoading = false
      this.close(consulta)
    }, error => {
      this.isLoading = false
      this.showAlert(error.error.message)
    })
  }

  showAlert(msg: string) {
    this.matDialog.open(AlertModalComponent, { data: { title: 'Agendamento de consultas', message: msg } }).afterClosed().subscribe(ok => {})
  }

  close(data: any = null) {
    this.dialogRef.close(data);
  }

  formatDate(value: string | Date, pattern: string): string {
    const data = typeof value === 'string' ? value : value.toJSON()
    return this.utilsService.formatDate(data, pattern)
  }

  extractDateFromFormControl(formControlName: string): string {
    if (!formControlName) {
      return
    }

    const hour = Number(this.eventForm.get(formControlName).value.padStart(4, '0').substr(0, 2))
    const minute = Number(this.eventForm.get(formControlName).value.padStart(4, '0').substr(2, 2))
    const today = new Date(this.dataAtual)

    return this.dateService.formatToAPITimezone(new Date(getYear(today), getMonth(today), getDate(today), hour, minute))
      .substring(0, 19)
  }

}
