import { Component, OnInit, Inject, LOCALE_ID } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ReservaEventModel } from './reserva-event.model';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS } from '@angular/material-moment-adapter';
import { DateAdapter,MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';
import { addHours } from 'date-fns';
import * as moment from 'moment';
import { AlertModalComponent } from 'src/app/alert-modal/alert-modal.component';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';

export enum DiasSemana {

  SEGUNDA = 'SEGUNDA',
  TERCA = 'TERÇA',
  QUARTA = 'QUARTA',
  QUINTA = 'QUINTA',
  SEXTA = 'SEXTA',
  SABADO = 'SÁBADO',

}
@Component({
  selector: 'app-scheduling-modal',
  templateUrl: './scheduling-modal.component.html',
  styleUrls: ['./scheduling-modal.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },    
  ]
})
export class SchedulingModalComponent implements OnInit {

  isLoading: boolean = false;
  repeat: number = 0;
  diasSemana: DiasSemana[]
  eventForm: FormGroup;
  dataAtual: Date
  event: ReservaEventModel;
  dialogTitle: string;
  recorrenciaHabilitada = false
  horarios: Map<number, any[]>
  inicio: any;
  rangeStart: string;
  rangeEnd: string
  dataInicio: any;

  constructor(
    public dialogRef: MatDialogRef<SchedulingModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private matDialog: MatDialog,
    private adapter: DateAdapter<any>,
  ) {
    this.adapter.setLocale('pt-br');
    this.event = data;
    this.event = new ReservaEventModel({
      sala: data.sala,
      horaInicio: data.dataInicioReserva,
      horaFim: data.dataFimReserva
    });
    this.diasSemana = [DiasSemana.SEGUNDA, DiasSemana.TERCA, DiasSemana.QUARTA, DiasSemana.QUINTA, DiasSemana.SEXTA, DiasSemana.SABADO]

    const dataInicio: any = this.event.horaInicio
    this.dataInicio = dataInicio;
    this.eventForm = new FormGroup({
      sala: new FormControl(this.event.sala, Validators.required),
      dataInicio: new FormControl(dataInicio, Validators.required),
      periodo: new FormControl('SEMANAL'),
      repeticaoPeriodo: new FormControl(0, [Validators.required, Validators.min(0)]),
      horaInicio: new FormControl(this.event.horaInicio, Validators.required),
      horaFim: new FormControl(this.event.horaFim, Validators.required),
      diasSemana: new FormControl([dataInicio.getDay()], Validators.required),
      dataFim: new FormControl({ value: null, disabled: true }),
      maximoAgendamentos: new FormControl({ value: null, disabled: true }),
    }, {
      validators: this.validateForm()
    });

    this.dialogTitle = 'Novo Agendamento';

    this.dataAtual = data.dataInicioReserva
  }


  ngOnInit(): void {
    if (this.data.dataInicioReserva.getHours() >= 8 && this.data.dataInicioReserva.getHours() < 12) {
      this.rangeStart = '08:00'
      this.rangeEnd = '12:00'
    } else if (this.data.dataInicioReserva.getHours() >= 13 && this.data.dataInicioReserva.getHours() < 17) {
      this.rangeStart = '13:00'
      this.rangeEnd = '17:00'
    } else {
      this.rangeStart = '18:00'
      this.rangeEnd = '22:00'
    }
    this.inicio = this.data.dataInicioReserva
    this.horarios = new Map<number, any[]>()
    this.setupHorary()
  }
  validaIntevaloAgenda(inicio: any, fim: any): boolean {
    const diff = fim - inicio
    // tslint:disable-next-line:max-line-length
    if (diff === 1 && inicio !== 8 && inicio !== 11 && inicio !== 13 && inicio !== 16 && inicio !== 18 && inicio !== 21) {
      // tslint:disable-next-line:max-line-length
      this.showAlert('Agendamentos de 1 hora  são permitidos nos seguintes horários.\nManha  08:00 às 9:00 e 11:00 às 12:00\nTarde:  13:00 às 14:00 e 16:00 às 17:00\nNoite: 18:00 às 19:00 e 21:00 às 22:00')
      return false
    }
    if (diff === 2 && (inicio === 9 || inicio === 14 || inicio === 19)) {
      // tslint:disable-next-line:max-line-length
      this.showAlert('Agendamentos de 2 hora são permitidos nos seguintes horários.\nManha  08:00 às 10:00 e 10:00 às 12:00\nTarde:  13:00 às 15:00 e 16:00 às 18:00\nNoite: 18:00 às 20:00 e 20:00 às 22:00')
      return false
    }
    if (diff === 3) {
      this.showAlert('Não é possivel agendar 3 horas')
      return false
    }
    if(inicio >= fim){
      this.showAlert('Agendamentos precisam ter a hora inicial antes da hora final')
      this.eventForm.controls.horaInicio.setValue(this.event.horaInicio)
      this.eventForm.controls.horaFim.setValue(this.event.horaFim)
      return false
    }
    return true
  }
  changeHour(event) {
    this.validaIntevaloAgenda(this.eventForm.controls.horaInicio.value.getHours(), this.eventForm.controls.horaFim.value.getHours())
  }
  save() {
    if(!this.eventForm.controls.diasSemana.value.includes(this.data.dataInicioReserva.getDay())){
      this.showAlert("O dia que foi selecionado precisa estar incluso na área de 'repetir'")
      return false
    }
    if (this.validaIntevaloAgenda(this.eventForm.controls.horaInicio.value.getHours(), this.eventForm.controls.horaFim.value.getHours())) {
      this.dialogRef.close(this.eventForm)
    }
  }
  showAlert(msg: string) {
    this.matDialog.open(AlertModalComponent, { data: { title: 'Reserva de salas', message: msg } }).afterClosed().subscribe(ok => {
    })
  }
  validateForm(): any {
    return (formGroup: FormGroup) => {
      const dataFimControl = formGroup.controls['dataFim'];
      const maximoAgendamentosControl = formGroup.controls['maximoAgendamentos'];

      if (dataFimControl.disabled && maximoAgendamentosControl.disabled) {
        return;
      }

      if (maximoAgendamentosControl.errors) {
        // return if another validator has already found an error on the matchingControl
        return;
      }

      // set error on matchingControl if validation fails
      if (dataFimControl.value === null && maximoAgendamentosControl.value === null) {
        maximoAgendamentosControl.setErrors({ required: true });
      }
    }
  }
  dataFimChanged(): void {
    if (this.recorrenciaHabilitada) {
      if (this.eventForm.controls.dataFim.value !== null) {
        this.eventForm.controls.maximoAgendamentos.clearValidators()
        this.eventForm.controls.maximoAgendamentos.updateValueAndValidity()
      } else {
        this.eventForm.controls.maximoAgendamentos.setValidators(Validators.min(1))
        this.eventForm.controls.maximoAgendamentos.updateValueAndValidity()
      }
    }
  }
  repeticaoPeriodoChanged(): void {
    this.recorrenciaHabilitada = (this.eventForm.value.repeticaoPeriodo === null || this.eventForm.value.repeticaoPeriodo > 0)
    if (this.recorrenciaHabilitada) {
      this.eventForm.controls.periodo.setValidators(Validators.required)
      this.eventForm.controls.dataFim.enable()
      this.eventForm.controls.maximoAgendamentos.enable()
      this.eventForm.controls.maximoAgendamentos.setValidators(Validators.min(1))
    } else {
      this.eventForm.controls.periodo.clearValidators()
      this.eventForm.controls.dataFim.disable()
      this.eventForm.controls.maximoAgendamentos.clearValidators()
      this.eventForm.controls.maximoAgendamentos.disable()

      this.eventForm.controls.dataFim.setValue('')
      this.eventForm.controls.maximoAgendamentos.setValue('')
      this.eventForm.controls.diasSemana.setValue([this.dataInicio.getDay()]);
    }
  }



  close() {
    this.dialogRef.close();
  }

  isDiaSelecionado(diaSemana: DiasSemana): string {
    // tslint:disable-next-line:max-line-length
    const indexDiaSemana = this.diasSemana.indexOf(diaSemana) !== -1 ? (this.diasSemana.indexOf(diaSemana) + 1) : this.diasSemana.indexOf(diaSemana)
    return (this.eventForm.controls.diasSemana.value.indexOf(indexDiaSemana) > -1) ? 'primary' : 'accent'
    // return 'accent'
  }
  diaSemanaClicked(diaSemana: DiasSemana): void {
    if (this.recorrenciaHabilitada) {
      const indexDiaSemana =
        this.diasSemana.indexOf(diaSemana) !== -1
          ? this.diasSemana.indexOf(diaSemana) + 1
          : this.diasSemana.indexOf(diaSemana);
      const index = this.eventForm.value.diasSemana.indexOf(indexDiaSemana)

      if (index > -1) {
        this.eventForm.value.diasSemana.splice(index, 1)
      } else {
        this.eventForm.value.diasSemana.push(indexDiaSemana)
      }
      this.eventForm.controls.diasSemana.updateValueAndValidity();
    }
  }
  getHorarios(index: number): any[] {
    if (!index && index !== 0) {
      return
    }
    return this.horarios.get(index).slice(0, -1)
  }

  getHorariosFinais(index: number): any[] {
    if (!index && index !== 0) {
      return
    }
    const horaInicialIndex = this.horarios.get(0).findIndex(h => h === this.eventForm.controls.dataInicio.value)
    return this.horarios.get(index).slice(horaInicialIndex + 1)
  }
  setupHorary() {
    this.horarios.set(0, [])
    let ini = moment(this.rangeStart, 'HH:mm').toDate()
    ini.setFullYear(this.inicio.getFullYear(), this.inicio.getMonth(), this.inicio.getDate())
    const fim = moment(this.rangeEnd, 'HH:mm').toDate()
    fim.setFullYear(this.inicio.getFullYear(), this.inicio.getMonth(), this.inicio.getDate())

    while (ini < fim) {
      this.horarios.get(0).push(ini)
      ini = addHours(ini, 1)
    }
    this.horarios.get(0).push(fim)

    this.horarios.get(0).forEach(h => {
      if (new Date(h).getHours() === new Date(this.eventForm.controls.horaInicio.value).getHours()) {
        this.eventForm.controls.horaInicio.setValue(h)
      }
    })
    this.horarios.get(0).forEach(h => {
      if (new Date(h).getHours() === new Date(this.eventForm.controls.horaFim.value).getHours()) {
        this.eventForm.controls.horaFim.setValue(h)
      }
    })
  }

}
