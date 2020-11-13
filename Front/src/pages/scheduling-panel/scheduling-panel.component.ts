import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CalendarDateFormatter, CalendarEvent, CalendarEventTitleFormatter, CalendarWeekViewBeforeRenderEvent } from 'angular-calendar';
import { endOfWeek, startOfWeek } from 'date-fns';
import * as moment from 'moment-timezone';
import { Subject } from 'rxjs';
import Room from 'src/models/room/room.model';
import Scheduling from 'src/models/scheduling.model';
import { AuthService } from 'src/services/auth.service';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { DateService } from 'src/services/date.service';
import { RoomService } from 'src/services/room.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { CustomDateFormatter } from '../rooms-scheduling/custom-date-formatter';
import { CustomEventTitleFormatter } from '../rooms-scheduling/custom-event-title-formatter';

@Component({
  selector: 'app-scheduling-panel',
  templateUrl: './scheduling-panel.component.html',
  styleUrls: ['./scheduling-panel.component.scss'],
  providers: [
    { provide: CalendarDateFormatter, useClass: CustomDateFormatter },
    { provide: CalendarEventTitleFormatter, useClass: CustomEventTitleFormatter }
  ],
})
export class SchedulingPanelComponent implements OnInit {
  view = 'week';
  viewDate: Date = new Date();

  locale = 'pt';
  // exclude weekends
  excludeDays: number[] = [0];
  weekStartsOn = 0;
  refresh: Subject<any> = new Subject();
  refreshResumoCompra: Subject<any> = new Subject();
  events: CalendarEvent[] = [];

  activeDayIsOpen = true;
  rooms: Room[]
  selectedRoom: Room
  isLoading: boolean = false
  showLegendActive: boolean = false;

  messages: any[]

  constructor(
    private roomService: RoomService,
    private schedulingService: SchedulingService,
    private calendarViewService: CalendarViewService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private dateService: DateService
  ) { }

  ngOnInit(): void {
    this.getRooms()
  }

  getRooms() {
    this.isLoading = true;
    this.roomService.getRooms().subscribe(rooms => {
      this.rooms = rooms;
      this.selectedRoom = rooms[0] && rooms[0]
      this.getShedulings()
    }, error => {
      console.log(error)
    })
  }

  beforeWeekViewRender(renderEvent: CalendarWeekViewBeforeRenderEvent): void {
    this.calendarViewService.desabilitar(renderEvent)
  }

  getShedulings() {
    this.isLoading = true;

    const params = {
      sala: this.selectedRoom.id,
      inicio: this.dateService.formatToAPITimezone(startOfWeek(this.viewDate)),
      fim: this.dateService.formatToAPITimezone(endOfWeek(this.viewDate))
    }
    this.schedulingService.getSchegulingList(params).subscribe((agendamentos: any[]) => {
      this.events = [];

      agendamentos.forEach(agendamento => {
        this.events.push(this.parseSchedulingToCalendarEvent(agendamento))
      })
      this.isLoading = false;
    }, error => {
      console.log(error)
      this.events = []
      this.events.push(this.parseSchedulingToCalendarEvent(new Scheduling()))
      this.openSnackBar(error.error.message, 'Ok', 5000)
      this.isLoading = false;
    })
  }


  closeOpenMonthViewDay(): void {
    this.events = []
    this.activeDayIsOpen = false;
    if (this.selectedRoom) {
      this.getShedulings()
    }
  }

  changeRoom(event: any) {
    this.getShedulings()
  }


  private parseSchedulingToCalendarEvent(scheduling: Scheduling): CalendarEvent {
    return {
      start: new Date(scheduling.inicio),
      end: new Date(scheduling.fim),
      title: this.getDescriptionBySchedulingWithStatus(scheduling),
      color: this.calendarViewService.getCollorByScheduling(scheduling),
      // actions: this.getActions(agendamento),
      meta: {
        agendamento: scheduling
      }
    }
  }
  getDescriptionBySchedulingWithStatus(scheduling: Scheduling): string {
    if (scheduling.tipoReserva === 'MANUTENCAO') {
      return 'MANUTENÇÃO'
    } else if (this.authService.getAccessType() === 'ROLE_ATENDENTE'
      || this.authService.getAccessType() === 'ROLE_ADMINISTRADOR'
      || scheduling.user.id === parseInt(this.authService.getUserId())) {

      let titulo = ""
      // if (scheduling.sala && scheduling.sala.nome) {
      //   titulo += `<span class="opt-room-indicator">${scheduling.sala.nome}</span><br>`
      // }
      if (scheduling.user && scheduling.user.nome) {
        titulo += `<span class="opt-professional-indicator">${scheduling.user.nome}</span><br>`
      }
      if (scheduling.assistente) {
        titulo += `<span class="opt-assistant-indicator">${scheduling.assistente.nome}</span>`
      }
      titulo += `<br>${
        scheduling.agendamentoStatusAtual === 'NAO_INICIADO'
          ? '<span class="opt-status-warning">Não iniciado<span>'
          : scheduling.agendamentoStatusAtual === 'OK'
            ? '<span class="opt-status-confirm">Iniciado<span>'
            : scheduling.agendamentoStatusAtual === 'LIBERADO'
              ? '<span class="opt-status-danger">Sala liberada<span>'
              : ''
        }`

      scheduling.tiposEquipamentos && scheduling.tiposEquipamentos.forEach(equipamento => {
        titulo += '<br><span class="opt-equipment-indicator">' + equipamento.nomeTipoEquipamento +
          '<span class="opt-equipment-time-indicator"> (' + moment(equipamento.inicio).format("HH") + "hrs -"
          + moment(equipamento.fim).format('HH') + "hrs)</span></span>"
      })

      return titulo
    } else {
      return ''
    }
  }
  showLegends() {
    this.showLegendActive = !this.showLegendActive
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
