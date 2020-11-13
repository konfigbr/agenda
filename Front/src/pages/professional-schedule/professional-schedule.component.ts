// tslint:disable-next-line:max-line-length
import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CalendarDateFormatter, CalendarEvent, CalendarEventTitleFormatter, CalendarWeekViewBeforeRenderEvent } from 'angular-calendar';
import { endOfWeek, startOfWeek } from 'date-fns';
import * as moment from 'moment-timezone';
import { Subject } from 'rxjs';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { ReservationType } from 'src/models/reservation-type.model';
import Room from 'src/models/room/room.model';
import Scheduling from 'src/models/scheduling.model';
import { AuthService } from 'src/services/auth.service';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { DateService } from 'src/services/date.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { CustomDateFormatter } from '../rooms-scheduling/custom-date-formatter';
import { CustomEventTitleFormatter } from '../rooms-scheduling/custom-event-title-formatter';
import { EquipmentModalComponent } from '../schedule-room-by-the-team/equipment-modal/equipment-modal.component';
import { EventAction } from 'calendar-utils';

function ceilToNearest(amount: number, precision: number): number {
  return Math.ceil(amount / precision) * precision;
}

@Component({
  selector: 'app-professional-schedule',
  templateUrl: './professional-schedule.component.html',
  styleUrls: ['./professional-schedule.component.scss'],
  providers: [
    { provide: CalendarDateFormatter, useClass: CustomDateFormatter },
    { provide: CalendarEventTitleFormatter, useClass: CustomEventTitleFormatter }
  ],
})
export class ProfessionalScheduleComponent implements OnInit {

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
    private schedulingService: SchedulingService,
    private dateService: DateService,
    private cdr: ChangeDetectorRef,
    private calendarViewService: CalendarViewService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.getProfessionalSchedule()
  }
  getProfessionalSchedule() {
    this.isLoading = true;
    const params = {
      inicio: startOfWeek(this.viewDate).toISOString().split('Z').join().split('.')[0],
      fim: endOfWeek(this.viewDate).toISOString().split('Z').join().split('.')[0],
      medico: this.authService.getUserId()
    }
    this.schedulingService.getProfessionalSchedule(params).subscribe(schedulings => {
      this.events = [];
      schedulings.forEach(scheduling => {
        this.events.push(this.parseSchedulingToCalendarEvent(scheduling))
      })
      this.isLoading = false;
    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000)
      this.isLoading = false;
    })
  }
  showLegends() {
    this.showLegendActive = !this.showLegendActive
  }
  parseSchedulingToCalendarEvent(scheduling: Scheduling): CalendarEvent {
    return {
      start: new Date(scheduling.inicio),
      end:  new Date(scheduling.fim),
      title: this.getDescricaoPorAgendamentoComSala(scheduling),
      color: this.calendarViewService.getCollorByScheduling(scheduling),
      meta: {
        agendamento: scheduling
      },
      actions: this.getActions(scheduling)
    }
  }
  private getActions(scheduling: Scheduling): EventAction[] {
    const actions: EventAction[] = []
    if (scheduling.agendamentoStatusAtual === 'NAO_INICIADO') {
      actions.push({
        label: '<span class="delete-icon-calendar">&#x2716;</span>',
        onClick: ({ event }: { event: CalendarEvent }): void => {
          this.removeScheduling(event);
        }
      })
      if (scheduling.tiposEquipamentos && scheduling.tiposEquipamentos.length) {
        actions.push(
          {
            label: `<span class="opt-gear-icon opt-gear-icon-ajt">⊕</span>`,
            onClick: ({ event }: { event: CalendarEvent }): void => {
              this.showEquipments(event.meta.agendamento)
            }
          }
        )
      }
    }
    return actions
  }
  getDescricaoPorAgendamentoComSala(agendamento: any): string {
    if (agendamento.tipoReserva === ReservationType.MANUTENCAO) {
      return 'MANUTENÇÃO'
    } else if (agendamento.user.id === parseInt(this.authService.getUserId())) {
      let titulo = `<span class="opt-room-indicator">${agendamento.sala.nome}</span><br><span class="opt-assistant-indicator">${agendamento.assistente.nome}</span>`
      agendamento.tiposEquipamentos && agendamento.tiposEquipamentos.forEach(equipamento => {
        titulo += '<br><span class="opt-equipment-indicator">' + equipamento.nomeTipoEquipamento +
          '<span class="opt-equipment-time-indicator"> (' + moment(equipamento.inicio).format("HH") + "hrs -"
          + moment(equipamento.fim).format('HH') + "hrs)</span></span>"
      })
      return titulo
    } else {
      return agendamento.user.nome
    }
  }
  showEquipments(scheduling: any) {
    const dialogRef = this.matDialog.open(EquipmentModalComponent, {
      width: '400px',
      data: { scheduling, justCancel: true }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'equipCancel') {
        this.getProfessionalSchedule()
      }
    });
  }
  removeScheduling(event: CalendarEvent<any>): void {
    const dialogRef = this.matDialog.open(MessageModalComponent, {
      width: '480px',
      maxWidth: '100%',
      data: { message: `Deseja mesmo apagar este agendamento?` }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cancelScheduling(event.meta.agendamento.id)
      }
      // this.cancelScheduling(event.meta.sh);
      // result && this.deleteRoom(room.id)
    });
  }
  cancelScheduling(schedulingId) {
    this.schedulingService.cancelScheduling({ agendamento: schedulingId }).subscribe(res => {
      if(res.creditoRecebido && res.creditoRecebido > 0 ) {
        this.openSnackBar(`Este agendamento já estava pago, vc terá um credito de R$ ${(res.creditoRecebido as number).toFixed(2).replace('.', ',')} reais na sua conta.`, 'Ok', 5000)

      }else{
        this.openSnackBar('Cancelamento realizado com sucesso!', 'Ok', 5000)
      }
      this.getProfessionalSchedule()
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000)
      console.log(error)
    })
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


  beforeWeekViewRender(renderEvent: CalendarWeekViewBeforeRenderEvent): void {
    this.calendarViewService.desabilitar(renderEvent)
  }
  closeOpenMonthViewDay(): void {
    this.events = []
    this.activeDayIsOpen = false;
    this.getProfessionalSchedule()
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
