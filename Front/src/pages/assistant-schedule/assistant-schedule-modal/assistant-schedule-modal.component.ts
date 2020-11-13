import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ReservaEventModel } from 'src/pages/schedule-room-by-the-team/scheduling-modal/reserva-event.model';
import Room from 'src/models/room/room.model';
import Assistant from 'src/models/assistant.model';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { RoomService } from 'src/services/room.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AlertModalComponent } from 'src/app/alert-modal/alert-modal.component';

export enum DiasSemana {

  SEGUNDA = 'SEGUNDA',
  TERCA = 'TERÇA',
  QUARTA = 'QUARTA',
  QUINTA = 'QUINTA',
  SEXTA = 'SEXTA',
  SABADO = 'SÁBADO',

}

@Component({
  selector: 'app-assistant-schedule-modal',
  templateUrl: './assistant-schedule-modal.component.html',
  styleUrls: ['./assistant-schedule-modal.component.scss']
})
export class AssistantScheduleModalComponent implements OnInit {

  isLoading: boolean = false;
  repeat: number = 0;
  diasSemana: DiasSemana[]
  eventForm: FormGroup;
  dataAtual: Date
  event: ReservaEventModel;
  dialogTitle: string;
  recorrenciaHabilitada = false

  rooms: Room[] = [];
  roomSelected: Room;
  assistant: Assistant
  diasIndisponiveis = []


  constructor(
    public matDialog: MatDialog,
    public dialogRef: MatDialogRef<AssistantScheduleModalComponent>,
    public schedulingService: SchedulingService,
    public roomService: RoomService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.event = data;
    this.event = new ReservaEventModel({
      assistente: data.assistente,
      horaInicio: data.dataInicioReserva,
      horaFim: data.dataFimReserva
    });
    this.diasSemana = [DiasSemana.SEGUNDA, DiasSemana.TERCA, DiasSemana.QUARTA, DiasSemana.QUINTA, DiasSemana.SEXTA, DiasSemana.SABADO]



  }


  ngOnInit(): void {
    this.getRooms()
  }

  getRooms() {
    this.isLoading = true;
    this.roomService.getRooms().subscribe(rooms => {
      this.rooms = rooms;
      this.roomSelected = rooms[0]
      this.getDaysUnavailable()
      this.setupInit();
      this.isLoading = false;

    }, error => {
      this.isLoading = false;
      console.log(error)
    })
  }
  getDaysUnavailable() {
    const params: any = {
      assistente: this.data.assistente.id,
      sala: this.roomSelected.id,
      horaInicio: (this.data.dataInicioReserva as Date).toLocaleTimeString().split(':')[0],
      horaFim: (this.data.dataFimReserva as Date).toLocaleTimeString().split(':')[0]
    }
    this.schedulingService.getDaysUnavailable(params).subscribe(res => {
      this.diasIndisponiveis = res;
    }, error => {
      this.openSnackBar(error.error.error, 'Ok', 5000)
    })
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
  setupInit() {
    const dataInicio: any = new Date(this.event.horaInicio)
    this.eventForm = new FormGroup({
      assistente: new FormControl(this.event.assistente.id, Validators.required),
      sala: new FormControl(this.roomSelected, Validators.required),
      horaInicio: new FormControl(new Date(this.event.horaInicio).getHours(), Validators.required),
      horaFim: new FormControl(new Date(this.event.horaFim).getHours(), Validators.required),
      diasDaSemana: new FormControl([dataInicio.getDay()], Validators.required)
    });
  }

  close() {
    this.dialogRef.close();
  }

  diaSemanaClicked(diaSemana: DiasSemana): void {
    const indexDiaSemana = this.diasSemana.indexOf(diaSemana) + 1
    if (!this.diasIndisponiveis.includes(indexDiaSemana)) {
      const index = this.eventForm.value.diasDaSemana.indexOf(indexDiaSemana)
      if (index > -1) {
        this.eventForm.value.diasDaSemana.splice(index, 1)
      } else {
        this.eventForm.value.diasDaSemana.push(indexDiaSemana)
      }
      this.eventForm.controls.diasDaSemana.updateValueAndValidity()
    } else {
      // tslint:disable-next-line:max-line-length
      this.matDialog.open(AlertModalComponent, { data: { title: 'Agenda assistente', message: 'Sala indisponivel para data solicitada.' } }).afterClosed()
    }
  }

  isDiaSelecionado(diaSemana: DiasSemana): string {
    // tslint:disable-next-line:max-line-length
    const indexDiaSemana = this.diasSemana.indexOf(diaSemana) !== -1 ? (this.diasSemana.indexOf(diaSemana) + 1) : this.diasSemana.indexOf(diaSemana)
    // const indexDiaSemana = this.diasSemana.indexOf(diaSemana)
    return (this.eventForm.controls.diasDaSemana.value.indexOf(indexDiaSemana) > -1) ? 'primary' : 'accent'
  }



  changeRoom($event) {
    const selectSala = $event;
    this.getDaysUnavailable()
  }

}
