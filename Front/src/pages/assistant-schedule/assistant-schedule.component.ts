import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CalendarDateFormatter, CalendarEvent, CalendarEventTitleFormatter, CalendarWeekViewBeforeRenderEvent } from 'angular-calendar';
import { addHours, addMinutes, isBefore, isSameDay, setHours, startOfWeek } from 'date-fns';
import { fromEvent, Subject } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import Assistant from 'src/models/assistant.model';
import Room from 'src/models/room/room.model';
import { AssistantService } from 'src/services/assistant.service';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { DateService } from 'src/services/date.service';
import { RoomService } from 'src/services/room.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { CustomDateFormatter } from '../rooms-scheduling/custom-date-formatter';
import { CustomEventTitleFormatter } from '../rooms-scheduling/custom-event-title-formatter';
import { AssistantScheduleModalComponent } from './assistant-schedule-modal/assistant-schedule-modal.component';


function ceilToNearest(amount: number, precision: number): number {
  return Math.ceil(amount / precision) * precision;
}
@Component({
  selector: 'app-assistant-schedule',
  templateUrl: './assistant-schedule.component.html',
  styleUrls: ['./assistant-schedule.component.scss'],
  providers: [
    { provide: CalendarDateFormatter, useClass: CustomDateFormatter },
    { provide: CalendarEventTitleFormatter, useClass: CustomEventTitleFormatter }
  ],
})
export class AssistantScheduleComponent implements OnInit {
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
  assistants: Assistant[] = []

  activeDayIsOpen = true;
  romms: Room[];

  // selectedRoom: Room;
  assistantSelected: any;

  rooms: Room[] = []
  isLoading: boolean = false;
  showLegendActive: boolean = false;

  constructor(
    private roomService: RoomService,
    private schedulingService: SchedulingService,
    private dateService: DateService,
    private cdr: ChangeDetectorRef,
    private calendarViewService: CalendarViewService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private assistantService: AssistantService
  ) { }

  ngOnInit(): void {
    this.getAssistants()
  }

  getAssistants() {
    this.isLoading = true
    this.assistantService.getAssistants().subscribe(assistants => {
      this.assistants = assistants;
      this.isLoading = false
      this.assistantSelected = assistants && assistants[0] && assistants[0]
      this.getAssistantSchedule();
    }, error => {
      console.log(error)
      this.isLoading = false
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }
  showLegends() {
    this.showLegendActive = !this.showLegendActive
  }
  getAssistantSchedule() {
    this.assistantService.getAssistantSchedule(this.assistantSelected.id).subscribe((schedulings: any) => {
      this.events = []
      schedulings.forEach(scheduling => {
        this.events.push(this.parseSchedulingToCalendarEvent(scheduling))
      })
    }, error => {
      this.events = []
      this.openSnackBar(error.error.message, 'Ok', 5000)
      console.log(error)
    })
  }
  parseSchedulingToCalendarEvent(scheduling: any): CalendarEvent {
    return {
      start: this.getStartDate(scheduling),
      end: this.getEndDate(scheduling),
      title: `<span class="opt-room-indicator">${scheduling.nomeSala}</span>`,
      color: {
        primary: 'rgb(14, 52, 106)',
        secondary: 'rgb(14, 52, 106, 0.3)'
      },
      actions: [
        {
          label: '<span class="delete-icon-calendar">&#x2716;</span>',
          onClick: ({ event }: { event: CalendarEvent }): void => {
            this.eventClicked(event)
          }
        }
      ]
    }
  }

  eventClicked(event: CalendarEvent): void {
    const date: Date = event.start
    this.matDialog.open(MessageModalComponent,
      {
        data: {
          title: 'Agenda Assistente',
          message: 'Deseja mesmo apagar essa agenda?'
        }
      }).afterClosed().subscribe(response => {
        if (response) {
          const assistantSchedule = {
            assistente: this.assistantSelected.id,
            horaInicio: event.start.getHours(),
            diaDaSemana: event.start.getDay()
          }
          this.schedulingService.deleteAssistantSchedule(assistantSchedule).subscribe(res => {
            this.openSnackBar('Agenda apagada com sucesso!', 'Ok', 3000);
            this.getAssistantSchedule()
          }, error => {
            this.openSnackBar(error.error.message, 'Ok', 5000);
            console.log(error)
          })
        }
      })

  }
  changeAssistant(event: any) {
    this.getAssistantSchedule()
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


  startDragToCreate(segment: any, mouseDownEvent: MouseEvent, segmentElement: HTMLElement): void {
    if (mouseDownEvent.buttons !== 1) {
      return;
    }

    // if (!this.schedulingService.validDateForReservation(segment.date)) {
    //   return
    // }

    this.dragToSelectEvent = {
      id: this.events.length,
      title: '',
      start: segment.date,
      color: { primary: 'rgba(180, 140, 64)', secondary: 'rgba(180, 140, 64, 0.1)' },
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

    if (datas.length > 0 && isBefore(datas[0].start, endOfView)) {
      endOfView = datas[0].start
    }

    fromEvent(document, 'mousemove')
      .pipe(
        finalize(() => {
          delete this.dragToSelectEvent.meta.tmpEvent;
          this.dragToCreateActive = false;
          this.dragToSelectEvent.end = this.dragToSelectEvent.end ? this.dragToSelectEvent.end : addHours(this.dragToSelectEvent.start, 1)
          this.selectAssistantRoom(this.dragToSelectEvent.start, this.dragToSelectEvent.end)
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

  selectAssistantRoom(dataInicioReserva: Date, dataFimReserva: Date): void {
    this.matDialog.open(AssistantScheduleModalComponent, {
      panelClass: 'event-form-dialog',
      width: '360px',
      maxWidth: '100%',
      data: {
        action: 'new',
        assistente: this.assistantSelected,
        dataInicioReserva,
        dataFimReserva,
      }
    }).afterClosed().subscribe(response => {
      this.events.splice(this.events.indexOf(this.dragToSelectEvent), 1)
      if (!response) {
        this.refresh.next(true);
        return;
      }
      this.assistantService.createAssistantSchedule(this.assistantSelected.id, { ...response.value, sala: response.value.sala.id })
        .subscribe(res => {
          this.openSnackBar('Assistente agendado(a) com sucesso!', 'Ok', 3000);
          this.getAssistantSchedule()
        }, error => {
          this.openSnackBar(error.error.message, 'Ok', 5000);
          this.getAssistantSchedule()
          console.log(error)
        })


    })
  }


  private refreshCalendar(): void {
    this.events = [...this.events];
    this.cdr.detectChanges();
    setTimeout(() => {
      this.closeOpenMonthViewDay()

    }, 2000)

  }

  beforeWeekViewRender(renderEvent: CalendarWeekViewBeforeRenderEvent): void {
    this.calendarViewService.desabilitar(renderEvent)
  }
  closeOpenMonthViewDay() {

  }



  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}

