import { ConsultaService } from './../../../services/consulta.service';
import Appointment from 'src/models/appointment-model';
import Patient from 'src/models/patient.model';
import { Component, OnInit, Inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { DateAdapter } from 'angular-calendar';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS } from '@angular/material-moment-adapter';
import { MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';

import { getYear, getMonth, getDate } from 'date-fns';

import { AlertModalComponent } from 'src/app/alert-modal/alert-modal.component';
import { UtilsService } from 'src/services/utils.service';
import { AppointmentHour } from 'src/models/appointment-hour-model';
import { MatTableDataSource } from '@angular/material/table';
import { CreateAppointmentModel } from 'src/models/create-appointment.model';
import { DateService } from 'src/services/date.service';
import { ProfessionalService } from 'src/services/professional.service';
import { MatPaginator } from '@angular/material/paginator';

import Professional from 'src/models/professional.model';
import Scheduling from 'src/models/scheduling.model';

@Component({
  selector: 'app-scheduling-appointment-modal',
  templateUrl: './scheduling-appointment-modal.component.html',
  styleUrls: ['./scheduling-appointment-modal.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
  ]
})
export class SchedulingAppointmentModalComponent implements OnInit {
  profissional: Professional
  patient: Patient = null
  isReagendamento: boolean = false
  agendamento: Scheduling
  appointment: Appointment = null

  horasDisponiveisSelecao: string[] = [];

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
    public dialogRef: MatDialogRef<SchedulingAppointmentModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private matDialog: MatDialog,
    private utilsService: UtilsService,
    private dateService: DateService,
    private professionalService: ProfessionalService,
    private consultaService: ConsultaService
  ) {
    this.dataAtual = data.dataInicioReserva
    this.horariosOcupados = data.horariosOcupados
    this.horariosDisponiveis = data.horariosDisponiveis
    this.profissional = data.profissional
    this.agendamento = data.agendamento
    this.isReagendamento = data.isReagendamento
    this.patient = data.paciente
    this.appointment = data.appointment

    this.eventForm = new FormGroup({
      horaInicio: new FormControl('', Validators.required),
      horaFim: new FormControl('', Validators.required),
      motivo: new FormControl('', Validators.required),
    });

    this.dataSourceHorariosOcupados = new MatTableDataSource(this.horariosOcupados)
    this.dataSourceHorariosDisponiveis = new MatTableDataSource(this.horariosDisponiveis)
    this.dialogTitle = this.isReagendamento ? `Reagendamento de consulta com Doutor(a) ${this.profissional.nome}` : `Nova consulta com Doutor(a) ${this.profissional.nome}`
  }

  ngOnInit(): void {
    this.listarHorariosDisponiveisSelect();
  }

  listarHorariosDisponiveisSelect() {
    this.horariosDisponiveis.forEach(h => {
      let dateTimeInicio = new Date(h.dataInicio);
      let dateTimeFim = new Date(h.dataFim);

      let horaInicio = dateTimeInicio.getHours();
      let horaFim = dateTimeFim.getHours();

      this.horasDisponiveisSelecao.push(dateTimeInicio.getHours().toPrecision(2) + ':00');

      for (let index = 0; index < (horaFim - horaInicio) - 1; index++) {
        dateTimeInicio.setHours(dateTimeInicio.getHours() + 1);
        this.horasDisponiveisSelecao.push(dateTimeInicio.getHours().toPrecision(2) + ':00');
      }

    });

    this.eventForm.controls['horaInicio'].valueChanges.subscribe((h: string) => {
      let hora = h.split(":");
      this.eventForm.controls['horaFim'].setValue(Number(hora[0]) + 1 + ':00');
    });
  }

  save() {
    if (this.eventForm.invalid) {
      this.showAlert('Preencha todos os dados corretamente!')
      return
    }

    this.isLoading = true

    const newAppointment: CreateAppointmentModel = {
      dataInicio: this.extractDateFromFormControl('horaInicio'),
      dataFim: this.extractDateFromFormControl('horaFim'),
      agendamentoId: this.agendamento?.id,
      motivo: this.eventForm.get('motivo').value,
      pacienteId: Number(this.patient?.id)
    }

    if (this.isReagendamento) {
      console.log(this.appointment.id);
      console.log(newAppointment);
      this.consultaService.rescheulingMedicalAppointment(Number(this.appointment.id), newAppointment).subscribe(consulta => {
        this.isLoading = false
        this.close(consulta)
      }, error => {
        this.isLoading = false
        this.showAlert(error.error.message)
      })

    } else if (this.patient != null) {

      this.consultaService.createMedicalAppointmentWithPatient(Number(this.profissional.id), newAppointment).subscribe(consulta => {
        this.isLoading = false
        this.close(consulta)
      }, error => {
        this.isLoading = false
        this.showAlert(error.error.message)
      })
    } else {

      this.professionalService.createMedicalAppointment(Number(this.profissional.id), newAppointment).subscribe(consulta => {
        this.isLoading = false
        this.close(consulta)
      }, error => {
        this.isLoading = false
        this.showAlert(error.error.message)
      })
    }
  }

  showAlert(msg: string) {
    this.matDialog.open(AlertModalComponent, { data: { title: 'Agendamento de consultas', message: msg } }).afterClosed().subscribe(ok => { })
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
    let horasMinutos = this.eventForm.get(formControlName).value.split(':');
    const hour = Number(horasMinutos[0])
    const minute = Number(horasMinutos[1])
    const today = new Date(this.dataAtual)

    return this.dateService.formatToAPITimezone(new Date(getYear(today), getMonth(today), getDate(today), hour, minute))
      .substring(0, 19)
  }
}
