import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {
  CalendarDateFormatter,
  CalendarEvent,
  CalendarEventTitleFormatter,
  CalendarView,
  CalendarWeekViewBeforeRenderEvent,
  DAYS_OF_WEEK
} from 'angular-calendar';
import * as moment from 'moment-timezone';
import { EventColor } from 'calendar-utils';

import { Subject, fromEvent } from 'rxjs';
import { endOfWeek, startOfWeek, isSameDay, setHours, isBefore, addHours, addMinutes } from 'date-fns';
import { RoomService } from 'src/services/room.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { DateService } from 'src/services/date.service';
import { finalize, takeUntil } from 'rxjs/operators';
import Scheduling from 'src/models/scheduling.model';
import { AssistantSchedule } from 'src/models/assistant-schedule.model';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { ReservationType } from 'src/models/reservation-type.model';
import { MatDialog } from '@angular/material/dialog';
// tslint:disable-next-line:max-line-length
import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import Room from 'src/models/room/room.model';
import { ChangeAssistantSchedulingDialogComponent } from '../rooms-scheduling/change-assistant-scheduling-dialog/change-assistant-scheduling-dialog.component';
import { MessageService } from 'src/services/message.service';
import { AuthService } from 'src/services/auth.service';
import { SchedulingStatus } from 'src/models/scheduling-status.model';
import { UserService } from 'src/services/user.service';
import { ProfileRoles } from 'src/models/profile-roles.model';
import { CustomDateFormatter } from '../rooms-scheduling/custom-date-formatter';
import { CustomEventTitleFormatter } from '../rooms-scheduling/custom-event-title-formatter';
import Assistant from 'src/models/assistant.model';
import { MatSnackBar } from '@angular/material/snack-bar';

function ceilToNearest(amount: number, precision: number): number {
  return Math.ceil(amount / precision) * precision;
}


@Component({
  selector: 'app-assistant-schedule-panel',
  templateUrl: './assistant-schedule-panel.component.html',
  styleUrls: ['./assistant-schedule-panel.component.scss'],
  providers: [
    { provide: CalendarDateFormatter, useClass: CustomDateFormatter },
    { provide: CalendarEventTitleFormatter, useClass: CustomEventTitleFormatter }
  ],
})
export class AssistantSchedulePanelComponent implements OnInit {

  messages: string[]
  assistant: Assistant

  view = 'week';
  viewDate: Date = new Date();
  locale = 'pt';
  excludeDays: number[] = [0];
  activeDayIsOpen = true;
  weekStartsOn = 0
  refresh: Subject<any> = new Subject();

  events: CalendarEvent[] = [];

  isLoading: boolean = false;
  showLegendActive: boolean = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private schedulingService: SchedulingService,
    private calendarViewService: CalendarViewService
  ) {
    this.messages = []
  }

  ngOnInit(): void {
    this.getAssistantSchedule()
    // this.carregarDados()
  }


  beforeWeekViewRender(renderEvent: CalendarWeekViewBeforeRenderEvent): void {
    this.calendarViewService.desabilitar(renderEvent)
  }

  closeOpenMonthViewDay(): void {
    this.events = []
    this.activeDayIsOpen = false;
    this.getAssistantSchedule()
  }
  showLegends() {
    this.showLegendActive = !this.showLegendActive
  }
  private getAssistantSchedule(): void {
    this.isLoading = true
    const params: any = {
      id: this.authService.getUserId(),
      inicio: startOfWeek(this.viewDate).toISOString().split('Z').join().split('.')[0],
      fim: endOfWeek(this.viewDate).toISOString().split('Z').join().split('.')[0]
    }
    this.schedulingService.getAssistantScheduleList(params).subscribe(schedulings => {
      this.events = []
      schedulings.forEach(agendamento => {
        this.events.push(this.parseAgendamentoToCalendarEvent(agendamento))
      })
      this.isLoading = false

    }, error => {
      console.log(error)
      this.isLoading = false
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }
  private parseAgendamentoToCalendarEvent(scheduling: Scheduling): CalendarEvent {
    return {
      start: new Date(scheduling.inicio),
      end: new Date(scheduling.fim),
      title: this.getDescriptionAssistantScheduling(scheduling),
      color: this.calendarViewService.getCollorByScheduling(scheduling)
    }
  }
  getDescriptionAssistantScheduling(scheduling: Scheduling): string {
    let titulo = `<span class="opt-room-indicator">${scheduling.sala.nome}</span><br><span class="opt-professional-indicator">${scheduling.user.nome}</span>`
    scheduling.tiposEquipamentos && scheduling.tiposEquipamentos.forEach(equipamento => {
      titulo += '<br><span class="opt-equipment-indicator">' + equipamento.nomeTipoEquipamento +
        '<span class="opt-equipment-time-indicator"> (' + moment(equipamento.inicio).format("HH") + "hrs -"
        + moment(equipamento.fim).format('HH') + "hrs)</span></span>"
    })
    return titulo
  }
  // private parseAgendamentoToCalendarEvent(agendamento: Agendamento): CalendarEvent {
  //   return {
  //     start: new Date(agendamento.inicio),
  //     end: new Date(agendamento.fim),
  //     title: this.calendarViewService.getDescricaoAgendamentoAssistente(agendamento),
  //     color: this.calendarViewService.getCorPorAgendamento(agendamento)
  //   }
  // }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
