import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CalendarDateFormatter, CalendarEvent, CalendarEventTitleFormatter, CalendarWeekViewBeforeRenderEvent } from 'angular-calendar';
import { EventColor } from 'calendar-utils';
import { addHours, addMinutes, endOfWeek, isBefore, isSameDay, setHours, startOfWeek } from 'date-fns';
import * as moment from 'moment-timezone';
import { fromEvent, Subject } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';
import { AssistantSchedule } from 'src/models/assistant-schedule.model';
import { ReservationType } from 'src/models/reservation-type.model';
import Room from 'src/models/room/room.model';
import Scheduling from 'src/models/scheduling.model';
import { AuthService } from 'src/services/auth.service';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { DateService } from 'src/services/date.service';
import { ProfessionalService } from 'src/services/professional.service';
import { RoomService } from 'src/services/room.service';
import { SchedulingService } from 'src/services/scheduling.service';
// tslint:disable-next-line:max-line-length
import { ChangeAssistantSchedulingDialogComponent } from './change-assistant-scheduling-dialog/change-assistant-scheduling-dialog.component';
import { CustomDateFormatter } from './custom-date-formatter';
import { CustomEventTitleFormatter } from './custom-event-title-formatter';


function ceilToNearest(amount: number, precision: number): number {
  return Math.ceil(amount / precision) * precision;
}
@Component({
  selector: 'app-rooms-scheduling',
  templateUrl: './rooms-scheduling.component.html',
  styleUrls: ['./rooms-scheduling.component.scss'],
  providers: [
    { provide: CalendarDateFormatter, useClass: CustomDateFormatter },
    { provide: CalendarEventTitleFormatter, useClass: CustomEventTitleFormatter }
  ],
})
export class RoomsSchedulingComponent implements OnInit {
  view = 'week';
  viewDate: Date = new Date();
  locale = 'pt';
  excludeDays: number[] = [0];
  weekStartsOn = 0
  refresh: Subject<any> = new Subject();

  refreshResumoCompra: Subject<any> = new Subject();

  events: CalendarEvent[] = [];
  dragToSelectEvent: CalendarEvent
  dragToCreateActive = false

  activeDayIsOpen = true;
  romms: Room[];

  selectedRoom: Room;
  messages: Message[] = [];

  rooms: Room[] = []
  isLoading: boolean = false;
  showLegendActive: boolean = false;

  constructor(
    private roomService: RoomService,
    private schedulingService: SchedulingService,
    private dateService: DateService,
    private cdr: ChangeDetectorRef,
    private calendarViewService: CalendarViewService,
    private matDialog: MatDialog,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private professionalService: ProfessionalService

  ) { }

  ngOnInit(): void {
    this.getRooms()
  }

  objectComparisonFunction(option, value): boolean {
    return option.id === value.id;
  }

  getFullScheduleByRoom() {
    const localeEndDate = endOfWeek(this.viewDate).toLocaleDateString().split('/');

    const params = {
      sala: this.selectedRoom.id,
      inicio: startOfWeek(this.viewDate).toISOString().split('T')[0],
      fim: `${localeEndDate[2]}-${localeEndDate[1].length === 2 ? localeEndDate[1] : '0' + localeEndDate[1]}-${localeEndDate[0].length === 2 ? localeEndDate[0] : '0' + localeEndDate[0]}`
    }
    this.schedulingService.getFullScheduleByRoom(params).subscribe((res: any) => {
      this.events = []

      res.agendamentos && res.agendamentos.forEach((scheduling: Scheduling) => {
        this.events.push(this.parseSchedulingToCalendarEvent(scheduling))
      })
      res.agendaAssistente && res.agendaAssistente.forEach((assistantSchedule: any) => {
        this.events.push(this.parseAssistantScheduleToCalendarEvent(assistantSchedule))
      })


    }, error => {
      console.log(error)
    })
  }

  getRooms() {
    this.isLoading = true;
    this.roomService.getRooms().subscribe((rooms: any[]) => {
      
      this.rooms = rooms;
      this.selectedRoom = rooms[0] && rooms[0];
      // this.getFullSchedules();
      this.isLoading = false;
      this.getFullScheduleByRoom()

    }, error => {
      this.isLoading = false;
      console.log(error)
    })
  }

  changeRoom(event) {
    this.getFullScheduleByRoom()
  }

  parseSchedulingToCalendarEvent(scheduling: Scheduling): CalendarEvent {
    const calendarEvent: CalendarEvent = {
      start: new Date(scheduling.inicio),
      end: new Date(scheduling.fim),
      title: this.getDescriptionBySchedulingWithStatus(scheduling),
      color: scheduling.id ? this.getCollorsBySchedulling(scheduling) : this.getCollorsAssistantSchedule(),
      meta: {
        agendamento: scheduling,
        assistente: scheduling.assistente
      }
    }

    if (!moment.tz(calendarEvent.start, 'America/Sao_Paulo').isBefore() && scheduling.tipoReserva !== "MANUTENCAO") {
      calendarEvent.actions = [{
        label: '<i class="opt-icon-edit-calendar">&#9998;</i>',
        onClick: ({ event }: { event: CalendarEvent }): void => {
          this.changeAssistant(event)
        }
      }]
    }

    return calendarEvent
  }

  getDescriptionBySchedulingWithStatus(scheduling: any): string {
    if (scheduling.tipoReserva === ReservationType.MANUTENCAO) {
      return 'MANUTENÇÃO'
    } else if (this.authService.getAccessType() === 'ROLE_ATENDENTE'
      || this.authService.getAccessType() === 'ROLE_ADMINISTRADOR'
      || scheduling.user.id === this.authService.getUserId()) {

      let titulo = ""

      if (scheduling.user && scheduling.user.nome) {
        titulo += `<span class="opt-professional-indicator">${scheduling.user.nome}</span><br>`
      }
      if (scheduling.assistente) {
        titulo += `<span class="opt-assistant-indicator">${scheduling.assistente.nome}</span>`
      }
      if (scheduling.status) {
        titulo += `<br>${
          scheduling.agendamentoStatusAtual === 'NAO_INICIADO'
            ? '<span class="opt-status-warning">Não iniciado<span>'
            : scheduling.agendamentoStatusAtual === 'OK'
              ? '<span class="opt-status-confirm">Iniciado<span>'
              : scheduling.agendamentoStatusAtual === 'LIBERADO'
                ? '<span class="opt-status-danger">Sala liberada<span>'
                : ''
          }`
      }

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
  parseAssistantScheduleToCalendarEvent(assistantSchedule: AssistantSchedule): CalendarEvent {
    const calendarEvent: CalendarEvent = {
      start: this.getStartDate(assistantSchedule),
      end: this.getEndDate(assistantSchedule),
      title: `<span class="opt-assistant-indicator">${assistantSchedule.assistente.nome}</span>`,
      color: this.getCollorsAssistantSchedule(),
      meta: {
        assistente: assistantSchedule.assistente
      }
    }

    if (!moment.tz(calendarEvent.start, 'America/Sao_Paulo').isBefore()) {
      calendarEvent.actions = [{
        label: '<i class="opt-icon-edit-calendar">&#9998;</i>',
        onClick: ({ event }: { event: CalendarEvent }): void => {
          this.changeAssistant(event)
        }
      }]
    }

    return calendarEvent
  }

  getCollorsBySchedulling(scheduling: Scheduling): EventColor {
    return this.calendarViewService.getCollorByScheduling(scheduling)
  }

  getCollorsAssistantSchedule(): EventColor {
    return {
      primary: 'rgb(14, 52, 106)',
      secondary: 'rgb(14, 52, 106, 0.1)'
    }
  }

  getStartDate(scheduling): Date {
    const startDate: Date = startOfWeek(this.viewDate)
    startDate.setDate(startDate.getDate() + (scheduling.diaDaSemana))
    startDate.setHours(scheduling.horaInicio, 0);
    return startDate
  }

  getEndDate(scheduling): Date {
    const endDate: Date = startOfWeek(this.viewDate)
    endDate.setDate(endDate.getDate() + (scheduling.diaDaSemana))
    endDate.setHours(scheduling.horaFim + 1, 0);
    return endDate
  }

  formatDate(date: any) {
    const dt = new Date(date).toLocaleDateString().split('/');
    const dateFormated = `${dt[2]}-${dt[1].length === 2 ? dt[1] : '0' + dt[1]}-${dt[0].length === 2 ? dt[0] : '0' + dt[0]}`;
    const hourFormated = new Date(date).toLocaleTimeString().length === 8
      ? new Date(date).toLocaleTimeString()
      : '0' + new Date(date).toLocaleTimeString();

    return `${dateFormated}T${hourFormated}`
  }
  changeAssistant(event: CalendarEvent<any>): void {
    this.matDialog.open(ChangeAssistantSchedulingDialogComponent, {
      panelClass: 'event-form-dialog',
      width: '360px',
      maxWidth: '100%',
      data: {
        scheduling: (event.meta.agendamento && event.meta.agendamento.id) ? event.meta.agendamento : null,
        assistant: event.meta.assistente,
        room: this.selectedRoom,
        start: this.formatDate(event.start),
        end: this.formatDate(event.end)
      }
    }).afterClosed().subscribe(response => {
      if (response) {
        this.getFullScheduleByRoom()
      } else {
        if (this.dragToSelectEvent) {
          this.events.splice(this.events.indexOf(this.dragToSelectEvent), 1)
          this.refresh.next(true)
        }
      }
    })
  }

  startDragToCreate(segment: any, mouseDownEvent: MouseEvent, segmentElement: HTMLElement): void {
    if (mouseDownEvent.buttons !== 1) {
      return;
    }

    if (!this.schedulingService.validDateForReservation(segment.date)) {
      return
    }
    this.dragToSelectEvent = {
      id: this.events && this.events.length,
      title: '',
      start: segment.date,
      color: this.calendarViewService.getCorPorTipoReserva(ReservationType.USO),
      meta: {
        tmpEvent: true
      }
    };
    this.events = [...this.events, this.dragToSelectEvent];
    const segmentPosition = segmentElement.getBoundingClientRect();
    this.dragToCreateActive = true;
    let endOfView: Date

    const datas = this.events.filter(e => isSameDay(e.start, segment.date) && e.start.getHours() > segment.date.getHours())
      .sort((a, b) => a.start > b.start ? 1 : (b.start > a.start ? -1 : 0))

    if (segment.date.getHours() < 12) {
      endOfView = setHours(segment.date, 12)
    } else if (segment.date.getHours() < 17) {
      endOfView = setHours(segment.date, 17)
    } else if (segment.date.getHours() < 22) {
      endOfView = setHours(segment.date, 22)
    }

    if (datas && datas.length > 0 && isBefore(datas[0].start, endOfView)) {
      endOfView = datas[0].start
    }

    fromEvent(document, 'mousemove')
      .pipe(
        finalize(() => {
          delete this.dragToSelectEvent.meta.tmpEvent;
          this.dragToCreateActive = false;
          this.dragToSelectEvent.end = this.dragToSelectEvent.end ? this.dragToSelectEvent.end : addHours(this.dragToSelectEvent.start, 1)
          this.changeAssistant(this.dragToSelectEvent)
        }),
        takeUntil(fromEvent(document, 'mouseup'))
      )
      .subscribe((mouseMoveEvent: MouseEvent) => {
        const minutesDiff = ceilToNearest(
          mouseMoveEvent.clientY - segmentPosition.top,
          30
        );

        const newEnd = addMinutes(segment.date, minutesDiff * 2)
        if (newEnd > segment.date && newEnd <= endOfView) {
          this.dragToSelectEvent.end = newEnd;
        }
        this.refreshCalendar();
      });
  }

  refreshCalendar(): void {
    this.events = [...this.events];
    this.cdr.detectChanges();
    // setTimeout(() => {
    //   this.closeOpenMonthViewDay()

    // }, 2000)

  }

  beforeWeekViewRender(renderEvent: CalendarWeekViewBeforeRenderEvent): void {
    this.calendarViewService.desabilitar(renderEvent)
  }

  closeOpenMonthViewDay() {
    this.getFullScheduleByRoom()
  }

}
