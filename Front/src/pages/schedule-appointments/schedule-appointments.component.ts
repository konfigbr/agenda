import { ActivatedRoute, Router } from '@angular/router';
import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CalendarDateFormatter, CalendarEvent, CalendarEventTitleFormatter, CalendarWeekViewBeforeRenderEvent } from 'angular-calendar';
import { endOfWeek, startOfWeek, addHours, differenceInMinutes, isEqual, isSameHour } from 'date-fns';
import * as moment from 'moment-timezone';
import { Subject, Observable } from 'rxjs';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { ReservationType } from 'src/models/reservation-type.model';
import Room from 'src/models/room/room.model';
import Scheduling from 'src/models/scheduling.model';
import { AuthService } from 'src/services/auth.service';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { CustomDateFormatter } from '../rooms-scheduling/custom-date-formatter';
import { CustomEventTitleFormatter } from '../rooms-scheduling/custom-event-title-formatter';
import { EventAction } from 'calendar-utils';
import { ConsultaService } from 'src/services/consulta.service';
import { UtilsService } from 'src/services/utils.service';
import { SchedulingAppointmentModalComponent } from './scheduling-appointment-modal/scheduling-appointment-modal.component';
import Professional from 'src/models/professional.model';
import Appointment from 'src/models/appointment-model';
import { AppointmentHour } from 'src/models/appointment-hour-model';
import { SchedulingPeriod } from 'src/models/scheduling-period.model';
import { DisponibilidadeAgenda } from './disponibilidade-agenda.model'
@Component({
  selector: 'app-schedule-appointments',
  templateUrl: './schedule-appointments.component.html',
  styleUrls: ['./schedule-appointments.component.scss'],
  providers: [
    { provide: CalendarDateFormatter, useClass: CustomDateFormatter },
    { provide: CalendarEventTitleFormatter, useClass: CustomEventTitleFormatter }
  ],
})
export class ScheduleAppointmentsComponent implements OnInit {
  view = 'week';
  viewDate: Date = new Date();
  locale = 'pt';
  excludeDays: number[] = [0];
  weekStartsOn = 0
  refresh: Subject<any> = new Subject();

  refreshResumoCompra: Subject<any> = new Subject();

  events: CalendarEvent<any>[] = [];
  dragToSelectEvent: CalendarEvent
  dragToCreateActive = false

  activeDayIsOpen = true;
  romms: Room[];

  selectedRoom: Room;
  messages: Message[] = [];

  rooms: Room[] = []
  isLoading: boolean = false;

  horariosOcupados: AppointmentHour[] = []
  horariosDisponiveis: AppointmentHour[] = []

  avaliableTimes: AppointmentHour[] = []
  scheduleDataSelected = { date: null, status: null, avaliableTimes: null }

  constructor(
    private router: Router,
    private schedulingService: SchedulingService,
    private calendarViewService: CalendarViewService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private route: ActivatedRoute,
    private authService: AuthService,
    private consultaService: ConsultaService,
    private utilsService: UtilsService) { }

  ngOnInit(): void {
    const professionalKey = this.route.snapshot.params.professionalKey
    if (this.getProfessionalKey() !== professionalKey)
      return this.authService.logoutPatient(professionalKey)

    this.getProfessionalSchedule()
  }

  novaConsulta(agendamento: Scheduling): void {
    this.matDialog.open(SchedulingAppointmentModalComponent, {
      panelClass: 'event-form-dialog',
      width: '500px',
      maxWidth: '100%',
      data: {
        action: 'new',
        agendamento,
        profissional: this.getProfessionalObj(),
        dataInicioReserva: agendamento.inicio,
        dataFimReserva: agendamento.fim,
        horariosOcupados: this.horariosOcupados.filter(a => {
          const date = new Date(a.dataInicio)
          const agendamentoDate = new Date(agendamento.inicio)

          return date.getDate() === agendamentoDate.getDate()
        }),
        horariosDisponiveis: this.horariosDisponiveis.filter(a => {
          const date = new Date(a.dataInicio)
          const agendamentoDate = new Date(agendamento.inicio)

          return date.getDate() === agendamentoDate.getDate()
        }),
      }
    }).afterClosed().subscribe((consulta: Appointment) => {
      if (!consulta) {
        return
      }

      this.openSnackBar('Consulta agendada com sucesso!', 'Ok', 5000)

      const dialogRef = this.matDialog.open(MessageModalComponent, {
        width: '480px',
        maxWidth: '100%',
        data: {
          cancelText: 'Sim',
          apagarText: 'Não',
          message: 'Deseja realizar um novo agendamento?'
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.router.navigate([`${this.getProfessionalKey()}/minhas-consultas`], { replaceUrl: true })
        } else {
          this.getProfessionalSchedule()
        }
      });
    })
  }

  formatDate(date: any) {
    const dt = new Date(date).toLocaleDateString().split('/');
    const dateFormated = `${dt[2]}-${dt[1].length === 2 ? dt[1] : '0' + dt[1]}-${dt[0].length === 2 ? dt[0] : '0' + dt[0]}`;
    const hourFormated = new Date(date).toLocaleTimeString().length === 8
      ? new Date(date).toLocaleTimeString()
      : '0' + new Date(date).toLocaleTimeString();

    return `${dateFormated}T${hourFormated}`
  }
  getProfessionalSchedule() {
    this.isLoading = true;

    const params = {
      inicio: startOfWeek(this.viewDate).toISOString().split('Z').join().split('.')[0],
      fim: this.formatDate(endOfWeek(this.viewDate)),
      medico: this.getProfessionalObj().id
    }

    this.loadHorariosOcupados().subscribe(consultas => {
      this.horariosOcupados = consultas

      this.schedulingService.getProfessionalSchedule(params).subscribe(schedulings => {
        this.events = [];
        schedulings.forEach(scheduling => {
          this.events = this.events.concat(this.parseSchedulingToCalendarEvent(scheduling))
        })
        this.isLoading = false;
      }, error => {
        console.log(error)
        this.openSnackBar(error.error.message, 'Ok', 5000)
        this.isLoading = false;
      })
    }, _ => this.isLoading = false)

    this.loadHorariosDisponiveis()
  }

  loadHorariosOcupados(): Observable<AppointmentHour[]> {
    this.isLoading = true

    const inicio = startOfWeek(this.viewDate).toISOString().split('Z').join().split('.')[0]
    const fim = this.formatDate(endOfWeek(this.viewDate))

    return this.consultaService.getBusyTimes(Number(this.getProfessionalObj().id), inicio, fim)
  }

  eventClicked(event: any): void {
    console.log(event)
  }

  loadHorariosDisponiveis() {
    this.isLoading = true

    const inicio = startOfWeek(this.viewDate).toISOString().split('Z').join().split('.')[0]
    const fim = this.formatDate(endOfWeek(this.viewDate))

    this.consultaService.getAvailableTimes(Number(this.getProfessionalObj().id), inicio, fim).subscribe(horariosDisponiveis => {
      this.isLoading = false
      this.horariosDisponiveis = horariosDisponiveis
      this.avaliableTimes = horariosDisponiveis
    }, _ => this.isLoading = false)
  }

  parseSchedulingToCalendarEvent(scheduling: Scheduling): CalendarEvent<any>[] {
    let schedulingHours: number = 0

    switch (scheduling.periodo) {
      case SchedulingPeriod.UMA_HORA:
        schedulingHours = 1
        break
      case SchedulingPeriod.DUAS_HORAS:
        schedulingHours = 2
        break
      case SchedulingPeriod.QUATRO_HORAS:
        schedulingHours = 4
        break

    }

    return Array(schedulingHours).fill(1).map((hour, index) => {
      const start = addHours(new Date(scheduling.inicio), index)
      const end = addHours(new Date(scheduling.inicio), index + 1)
      const situacaoAgenda = this.hasHorarioDisponivel(start, end)
      const title = situacaoAgenda === DisponibilidadeAgenda.DISPONIVEL ? 'Horário disponível' :
        situacaoAgenda === DisponibilidadeAgenda.PARCIALMENTE_DISPONIVEL ? 'Horário parcial' : 'Horário ocupado'

      return {
        start,
        end,
        title,
        color: this.calendarViewService.getColorByDisponibilidadeAgenda(situacaoAgenda),
        meta: {
          agendamento: scheduling
        },
        actions: this.getActions(scheduling, situacaoAgenda, start)
      }
    })
  }

  /* Icon do calendario componente */
  private getActions(scheduling: Scheduling, situacaoAgenda: DisponibilidadeAgenda, start: Date): EventAction[] {
    const actions: EventAction[] = []
    const status = scheduling.agendamentoStatusAtual
    const data = new Date()
    if ((situacaoAgenda === DisponibilidadeAgenda.DISPONIVEL || situacaoAgenda === DisponibilidadeAgenda.PARCIALMENTE_DISPONIVEL) &&
      status === 'NAO_INICIADO') {

      if (start < data) {
        
      } else {
        actions.push({
          label: '<i class="opt-icon-edit-calendar">✎</i>',
          onClick: (): void => {
            this.novaConsulta(scheduling)
          }
        })
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

  getProfessionalKey() {
    return localStorage.getItem("professionalKey")
  }

  getProfessionalObj(): Professional {
    return JSON.parse(localStorage.getItem("professionalObj"))
  }

  formatDateWithPattern(date: string, pattern: string): string {
    return this.utilsService.formatDate(date, pattern)
  }

  private hasHorarioDisponivel(dataInicio: Date, dataFim: Date): DisponibilidadeAgenda {
    let isHorarioParcial = false
    let leftMinutes = 60 // 1h

    const horariosAgendamento = this.horariosOcupados.filter(a => {
      const date = new Date(a.dataInicio)
      const agendamentoDate = new Date(dataInicio)

      return date.getDate() === agendamentoDate.getDate()
    })

    const isDisponivel = !(horariosAgendamento.filter(a => {
      const inicio = new Date(a.dataInicio)
      const fim = new Date(a.dataFim)
      const isOcupado = (dataInicio >= inicio && dataFim <= fim) || (dataFim > inicio && dataInicio < fim)

      if (!isOcupado) {
        return false
      }

      if (isOcupado && (dataFim >= fim || dataInicio <= inicio)) {
        isHorarioParcial = true

        if (isEqual(fim, dataFim)) {
          leftMinutes -= Math.abs(differenceInMinutes(dataFim, inicio))
        } else {
          if (inicio < dataInicio && fim < dataFim) {
            leftMinutes -= Math.abs(differenceInMinutes(dataInicio, fim))
          } else {
            leftMinutes -= Math.abs(differenceInMinutes(inicio, fim))
          }

          if (inicio >= dataInicio && fim > dataFim) {
            leftMinutes = 0
          }
        }

        return false
      }

      return true
    }).length > 0)

    // tratativa para 'corrigir' cálculo do horário parcial
    if (isHorarioParcial && leftMinutes <= 0) {
      return DisponibilidadeAgenda.OCUPADO
    }

    if (isDisponivel && !isHorarioParcial) {
      return DisponibilidadeAgenda.DISPONIVEL
    } else if (isDisponivel && isHorarioParcial) {
      return DisponibilidadeAgenda.PARCIALMENTE_DISPONIVEL
    } else {
      return DisponibilidadeAgenda.OCUPADO
    }
  }
  loadAvaliableTimes({ event }: { event: CalendarEvent }): void {
    const inicioPeriodo = new Date(event.start)
    const fimPeriodo = new Date(event.end)

    const inicioAgendamento = new Date(event.meta?.agendamento?.inicio)
    const fimAgendamento = new Date(event.meta?.agendamento?.fim)

    this.scheduleDataSelected.date = moment(event.start).format('DD/MM')+" "+
      moment(event.start).format('HH:mm')+" ás "+moment(event.end).format('HH:mm')

    const horarios = this.horariosDisponiveis.filter(x => new Date(x.dataInicio) >= inicioAgendamento && new Date(x.dataFim) <= fimAgendamento)

    this.avaliableTimes = []

    horarios.forEach(horario => {
      const inicioHorario = new Date(horario.dataInicio)
      const fimHorario = new Date(horario.dataFim)

      if (!(inicioHorario < fimPeriodo && fimHorario > inicioPeriodo))
        return

      if (inicioHorario <= inicioPeriodo && fimHorario >= fimPeriodo) {
        const hora: AppointmentHour = {
          idAgendamento: null,
          idConsulta: null,
          dataInicio: moment(inicioPeriodo).format("YYYY-MM-DD HH:mm:ss"),
          dataFim: moment(fimPeriodo).format("YYYY-MM-DD HH:mm:ss")
        }
        return this.avaliableTimes.push(hora)
      }

      if (inicioHorario <= inicioPeriodo && fimHorario <= fimPeriodo) {
        const hora: AppointmentHour = {
          idAgendamento: null,
          idConsulta: null,
          dataInicio: moment(inicioHorario).format("YYYY-MM-DD HH:mm:ss"),
          dataFim: moment(fimHorario).format("YYYY-MM-DD HH:mm:ss")
        }
        return this.avaliableTimes.push(hora)
      }

      if (inicioHorario >= inicioPeriodo && fimHorario >= fimPeriodo) {
        const hora: AppointmentHour = {
          idAgendamento: null,
          idConsulta: null,
          dataInicio: moment(inicioHorario).format("YYYY-MM-DD HH:mm:ss"),
          dataFim: moment(fimPeriodo).format("YYYY-MM-DD HH:mm:ss")
        }
        return this.avaliableTimes.push(hora)
      }

      if (inicioHorario >= inicioPeriodo && fimHorario <= fimPeriodo) {
        const hora: AppointmentHour = {
          idAgendamento: null,
          idConsulta: null,
          dataInicio: moment(inicioHorario).format("YYYY-MM-DD HH:mm:ss"),
          dataFim: moment(fimHorario).format("YYYY-MM-DD HH:mm:ss")
        }
        return this.avaliableTimes.push(hora)
      }
    })
  }
}
