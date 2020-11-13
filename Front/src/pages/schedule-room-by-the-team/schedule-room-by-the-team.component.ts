// tslint:disable-next-line:max-line-length
import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { CalendarDateFormatter, CalendarEvent, CalendarEventTitleFormatter, CalendarWeekViewBeforeRenderEvent } from 'angular-calendar';
import { EventAction } from 'calendar-utils';
import { addHours, addMinutes, endOfWeek, isBefore, isSameDay, setHours, startOfWeek } from 'date-fns';
import * as moment from 'moment-timezone';
import { fromEvent, Subject } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';
import { AlertModalComponent } from 'src/app/alert-modal/alert-modal.component';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import Professional from 'src/models/professional.model';
import { ReservationType } from 'src/models/reservation-type.model';
import Scheduling from 'src/models/scheduling.model';
import { AuthService } from 'src/services/auth.service';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { CartService } from 'src/services/cart.service';
import { DateService } from 'src/services/date.service';
import { InvoicesService } from 'src/services/invoices.service';
import { ProfessionalService } from 'src/services/professional.service';
import { RoomService } from 'src/services/room.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { CustomDateFormatter } from '../rooms-scheduling/custom-date-formatter';
import { CustomEventTitleFormatter } from '../rooms-scheduling/custom-event-title-formatter';
import { EquipmentModalComponent } from './equipment-modal/equipment-modal.component';
import { FinishSaleComponent } from './finish-sale/finish-sale.component';
import { PricesTableModalComponent } from './prices-table-modal/prices-table-modal.component';
import { SchedulingModalComponent } from './scheduling-modal/scheduling-modal.component';
import { ReservationModalComponent } from '../reservation-modal/reservation-modal.component';
import { DiscountService } from 'src/services/discount.service';
import { DiscountType } from 'src/models/discount-type-model';


function ceilToNearest(amount: number, precision: number): number {
  return Math.ceil(amount / precision) * precision;
}
export interface ErrosValidacaoReserva {

  inicio: string | Date
  fim: string | Date
  erro: string
}


@Component({
  selector: 'app-schedule-room-by-the-team',
  templateUrl: './schedule-room-by-the-team.component.html',
  styleUrls: ['./schedule-room-by-the-team.component.scss'],
  providers: [
    { provide: CalendarDateFormatter, useClass: CustomDateFormatter },
    { provide: CalendarEventTitleFormatter, useClass: CustomEventTitleFormatter }
  ],
})
export class ScheduleRoomByTheTeamComponent implements OnInit {
  displayedColumnsSale: string[] = ['month', 'total'];

  @ViewChild('sale', { static: true }) sale: MatSort;

  view = 'week';
  viewDate: Date = new Date();
  locale = 'pt';
  excludeDays: number[] = [0];
  weekStartsOn = 0
  refresh: Subject<any> = new Subject();

  isProfessional: boolean = false;
  events: CalendarEvent[] = [];
  dragToSelectEvent: CalendarEvent
  dragToCreateActive = false
  credit: number = 0;

  activeDayIsOpen = true;
  errosValidacaoReserva: ErrosValidacaoReserva[] = []
  months: any = {
    JANUARY: 'Janeiro',
    FEBRUARY: 'Fevereiro',
    MARCH: 'Março',
    APRIL: 'Abril',
    MAY: 'Maio',
    JUNE: 'Junho',
    JULY: 'Julho',
    AUGUST: 'Agosto',
    SEPTEMBER: 'Setembro',
    OCTOBER: 'Outubro',
    NOVEMBER: 'Novembro',
    DECEMBER: 'Dezembro'
  }
  descontoTotal: number = 0
  selectedRoom: any = {};
  messages: Message[];
  roomsAvailable: any[] = []
  idScheduling: Subject<any> = new Subject();
  indexDetails: Subject<any> = new Subject();
  refreshResumoCompra: Subject<any> = new Subject();
  dataTableDetails: Subject<any> = new Subject();
  discountAvulso: any = {};
  discountPeriodo: any = {};
  isLoading: boolean = false;
  selectedProfessional: Professional;
  professionals: Professional[] = []
  dataSourceSale: any;
  solicitacaoReservaId: any[] = [];
  total: number = 0
  cartNumber: any;

  showLegendActive: boolean = false;

  constructor(
    private roomService: RoomService,
    private schedulingService: SchedulingService,
    private dateService: DateService,
    private discountPeriod: DiscountService,
    private cdr: ChangeDetectorRef,
    private calendarViewService: CalendarViewService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private authService: AuthService,
    private invoiceService: InvoicesService,
    private professionalService: ProfessionalService,
    private cartService: CartService
  ) { }

  ngOnInit(): void {

    this.messages = []
    this.dataSourceSale = new MatTableDataSource([])
    this.getRoomsAvailable()


  }
  showLegends() {
    this.showLegendActive = !this.showLegendActive
  }
  clearCart() {
    this.isLoading = true;

    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {
      /*  this.isProfessional = true
       this.getShedulings() */
      this.schedulingService.clearCart().subscribe(res => {
        this.isProfessional = true
        this.getShedulings()
        this.getPeriod()
      }, error => {
        this.isLoading = false;
        console.log(error)
        this.isProfessional = true
        this.getShedulings()
        this.openSnackBar(error.error.message, 'Ok', 5000);
      })
    } else {
      this.isProfessional = false
      this.getProfessionals()
    }
  }

  getCredit() {
    let professionalId: any;
    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {
      professionalId = this.authService.getUserId()
    } else {
      professionalId = this.selectedProfessional.id
    }
    this.schedulingService.getCredit(professionalId).subscribe(res => {
      this.credit = res
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }
  clearCartAttendant() {
    this.isLoading = true;
    this.schedulingService.clearCartAttendant(this.selectedProfessional.id).subscribe(res => {
      this.getShedulings()
    }, error => {
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }
  loadDiscounts() {

    this.discountPeriod.getCurrentDiscountsPeriod(this.selectedProfessional.id).subscribe(res => {
      /* console.log(res)  */
      this.discountAvulso = res.filter(x => x.tipo == DiscountType.HORA_AVULSA);

      this.discountPeriodo = res.filter(x => x.tipo == DiscountType.PERIODO);
      /* console.log(this.discountPeriodo) */


    }, error => {
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }
  getPeriod() {
    let idProfessional = null
    if (this.isProfessional) {
      idProfessional = localStorage.getItem('userId');
    } else {
      idProfessional = this.selectedProfessional.id
    }
    console.log(idProfessional)
    this.discountPeriod.getCurrentDiscountsPeriod(idProfessional).subscribe(res => {
      /* console.log(res)  */
      this.discountAvulso = res.filter(x => x.tipo == DiscountType.HORA_AVULSA);

      this.discountPeriodo = res.filter(x => x.tipo == DiscountType.PERIODO);
      /* console.log(this.discountPeriodo) */


    }, error => {
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }

  openPrices() {
    /* console.log(this.selectedRoom?.sala.especialidade.tabelaPreco.faixaPrecos) */
    /* console.log(discountAvulso) */
    this.matDialog.open(PricesTableModalComponent, {
      width: '300px',
      data: {
        faixaPrecos: this.selectedRoom?.sala.especialidade.tabelaPreco.faixaPrecos,
        discountAvulso: this.discountAvulso,
        discountPeriodo: this.discountPeriodo,
      }

    });

  }
  changeProfessional($event) {
    this.clearCartAttendant();
    this.loadDiscounts();
  }
  objectComparisonFunction(option, value): boolean {
    return option && value && option.id === value.id;
  }

  getProfessionals() {
    this.professionalService.getProfessionals().subscribe(professionals => {
      this.professionals = professionals;
      this.selectedProfessional = professionals[0] && professionals[0]
      this.getPeriod()
      if (!this.isProfessional) {
        this.getRoomsAvailableAssistant()
      }
    }, error => {
      console.log(error)
      this.isLoading = false;
    })
  }

  getRoomsAvailableAssistant() {
    this.roomService.getRoomsAvailable().subscribe(roomsAvailable => {
      this.roomsAvailable = roomsAvailable
      this.selectedRoom = roomsAvailable[0]
      this.refreshResumoCompra.next();
      this.clearCartAttendant()
    }, error => {
      console.log(error)
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }
  getRoomsAvailable() {
    this.roomService.getRoomsAvailable().subscribe(roomsAvailable => {
      this.roomsAvailable = roomsAvailable
      this.selectedRoom = roomsAvailable[0]
      this.refreshResumoCompra.next();
      this.clearCart()
    }, error => {
      console.log(error)
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }
  getShedulings() {
    this.getCredit()
    this.errosValidacaoReserva = [];
    this.isLoading = true;
    const params = {
      sala: this.selectedRoom.sala.id,
      inicio: this.dateService.formatToAPITimezone(startOfWeek(this.viewDate)),
      fim: this.dateService.formatToAPITimezone(endOfWeek(this.viewDate))
    }
    this.schedulingService.getSchegulingList(params).subscribe((agendamentos: any[]) => {
      this.events = [];
      if (agendamentos.length) {
        agendamentos.forEach((agendamento, index) => {
          this.events.push(this.parseSchedulingToCalendarEvent(agendamento))
          if (index === agendamentos.length - 1) {
            setTimeout(() => {
              this.isLoading = false
            }, 1000)
          }
        })
      } else {
        this.isLoading = false
      }
    }, error => {
      console.log(error)
      this.events = []
      this.events.push(this.parseSchedulingToCalendarEvent(new Scheduling()))
      this.openSnackBar(error.error.message, 'Ok', 5000)
      this.isLoading = false
    })

  }

  private parseSchedulingToCalendarEvent(scheduling: Scheduling): CalendarEvent {
    return {
      start: new Date(scheduling.inicio),
      end: new Date(scheduling.fim),
      title: this.getDescriptionByScheduling(scheduling),
      color: this.calendarViewService.getCollorByScheduling(scheduling),
      actions: this.getActions(scheduling),
      meta: {
        agendamento: scheduling
      }
    }
  }
  getDescriptionByScheduling(scheduling: Scheduling): string {
    if (scheduling.tipoReserva === ReservationType.MANUTENCAO) {
      return 'MANUTENÇÃO'
    } else if (this.authService.getAccessType() === 'ROLE_ATENDENTE'
      || scheduling.user.id === parseInt(this.authService.getUserId())) {
      let titulo = `<span class="opt-assistant-indicator">${scheduling && scheduling.assistente && scheduling.assistente.nome}</span>`
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
      return scheduling && scheduling.medico && scheduling.medico.nome
    }
  }
  private getActions(scheduling: Scheduling): EventAction[] {
    const actions: EventAction[] = []

    if (this.isAgendamentoProprio(scheduling)) {
      // tslint:disable-next-line:max-line-length
      const equipamentosPendentes = (scheduling.tiposEquipamentos) ? scheduling.tiposEquipamentos.filter(e => e.status === 'PENDENTE').length : 0
      if (scheduling.agendamentoStatusAtual === 'NAO_INICIADO') {
        if (scheduling.status === 'PENDENTE' || equipamentosPendentes > 0) {
          actions.push({
            label: '<span class="delete-icon-calendar">&#x2716;</span>',
            onClick: ({ event }: { event: CalendarEvent }): void => {
              this.cancelElement(event.meta.agendamento)
            }
          })
        }
        let today = new Date().getTime()
        let dataSchedule = new Date(scheduling.inicio).getTime()
        if(dataSchedule > today){
          actions.push({
            // tslint:disable-next-line:max-line-length
            label: `<span class="opt-gear-icon ${(scheduling.status === 'PENDENTE' || equipamentosPendentes > 0) ? 'opt-gear-icon-ajt' : ''}">⊕</span>`,
            onClick: ({ event }: { event: CalendarEvent }): void => {
              this.addEquipment(event)
            }
          })
        }
      }

    }

    return actions
  }
  addEquipment(event: any) {
    const dialogRef = this.matDialog.open(EquipmentModalComponent, {
      width: '400px',
      data: { scheduling: event.meta.agendamento }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'equipCancel') {
        this.getShedulings()
        this.dataSourceSale = new MatTableDataSource([])
        this.dataTableDetails.next([])
        this.refreshResumoCompra.next()
        this.refresh.next(true);
        this.idScheduling.next(undefined)
        this.total = 0
      } else if (result) {
        this.errosValidacaoReserva = result.erros.concat(this.errosValidacaoReserva)

        this.schedulingService.getListaEquipamentos(event.meta.agendamento.id).subscribe((tipoEquipamentos: any[]) => {
          event.meta.agendamento.tiposEquipamentos = tipoEquipamentos
          event.title = this.getDescriptionByScheduling(event.meta.agendamento)
          this.refreshResumoCompra.next()
          this.refresh.next();
        }, (erro) => {
          console.error(erro)
        })


        if (result.carrinho) {
          this.cartNumber = result.carrinho
          this.getDetails(result.carrinho)
        } else {
          this.getDetails(this.cartNumber)
        }
      }

    });
  }
  cancelElement(scheduling: any): void {
    const dialogRef = this.matDialog.open(MessageModalComponent, {
      width: '480px',
      maxWidth: '100%',
      data: { message: `Deseja mesmo apagar este agendamento?` }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cancelScheduling(scheduling.id)
      }
    });
  }
  cancelScheduling(schedulingId) {
    const id = this.solicitacaoReservaId.find(el => {
      return !!el.agendamentos.find(e => e.id === schedulingId)
    }).solicitacaoReservaId

    this.schedulingService.cancelSchedulingReservation({ solicitacaoReserva: id }).subscribe(res => {
      this.getShedulings()
      // this.dataSourceSale = new MatTableDataSource([])
      // this.dataTableDetails.next([])
      // this.refreshResumoCompra.next()
      // this.refresh.next(true);
      // this.idScheduling.next(undefined)
      // this.total = 0
      this.getDetails(this.cartNumber)
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000)
      console.log(error)
    })
  }

  isAgendamentoProprio(scheduling: Scheduling): boolean {
    return ((scheduling.tipoReserva === 'USO' || scheduling.tipoReserva === 'RESERVA') &&
      (this.authService.getAccessType() === 'ROLE_ATENDENTE' || scheduling.user.id === parseInt(this.authService.getUserId())))
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
    if (this.authService.getAccessType() === 'ROLE_ATENDENTE' && !this.selectedProfessional) {
      return;
    }
    if (mouseDownEvent.buttons !== 1) {
      return;
    }

    if (!this.schedulingService.validDateForReservation(segment.date)) {
      return
    }

    this.dragToSelectEvent = {
      id: this.events.length,
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

    if (datas.length > 0 && isBefore(datas[0].start, endOfView)) {
      endOfView = datas[0].start
    }

    fromEvent(document, 'mousemove')
      .pipe(
        finalize(() => {
          delete this.dragToSelectEvent.meta.tmpEvent;
          this.dragToCreateActive = false;
          this.dragToSelectEvent.end = this.dragToSelectEvent.end ? this.dragToSelectEvent.end : addHours(this.dragToSelectEvent.start, 1)

          if (this.validaIntevaloAgenda(this.dragToSelectEvent.start, this.dragToSelectEvent.end)) {
            this.novaReserva(this.dragToSelectEvent.start, this.dragToSelectEvent.end)
          }
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

  getDetails(id: any) {
    this.cartService.getPurchaseDetails(id).subscribe(resp => {
      this.dataTableDetails.next(resp)
      this.dataSourceSale = new MatTableDataSource(resp)
      this.total = resp[0] && resp[0].custo
      // this.total = resp[0] && resp[0].custo
    }, error => {
      console.log(error)
    })
  }
  private refreshCalendar(): void {
    this.events = [...this.events];
    this.cdr.detectChanges();
    // setTimeout(() => {
    //   this.closeOpenMonthViewDay()
    // }, 2000)

  }
  openDetailsByIndex(index: any, $element: any) {
    this.indexDetails.next(index);
    // this.router.navigateByUrl('agendar-salas#purchase-details');
    setTimeout(() => {
      this.scrollToElement($element)
    }, 300)
  }
  scrollToElement($element): void {
    $element.scrollIntoView({ behavior: "smooth", block: "start", inline: "nearest" });
  }
  finishSale() {
    const dialogRef = this.matDialog.open(FinishSaleComponent, {
      width: '480px',
      maxWidth: '100%',
      data: { cartNumber: this.cartNumber }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === 'error') {
        // this.cancelScheduling(scheduling.id)
      } else if (result) {
        this.getShedulings()
        this.dataSourceSale = new MatTableDataSource([])
        this.dataTableDetails.next([])
        this.refreshResumoCompra.next()
        this.refresh.next(true);
        this.idScheduling.next(undefined)
        this.total = 0
      } else {
        // this.data
      }
      // this.cancelScheduling(event.meta.sh);
      // result && this.deleteRoom(room.id)
    });
  }

  openAssitantsScheduleModal() {
    const dialogRef = this.matDialog.open(ReservationModalComponent, {
      width: '480px',
      maxWidth: '100%',
      data: { selectedRoom: this.selectedRoom }
    });

  }
  changeRoom(event: any) {

    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {
      this.isProfessional = true
      this.clearCart()
      this.dataSourceSale = new MatTableDataSource([])
      this.dataTableDetails.next([])
      this.refreshResumoCompra.next()
      this.refresh.next(true);
      this.idScheduling.next(undefined)
      this.total = 0
      this.getShedulings()      
    } else {
      this.clearCartAttendant()
    }
    // isThisSecond.
  }
  novaReserva(dataInicioReserva: Date, dataFimReserva: Date): void {
    this.matDialog.open(SchedulingModalComponent, {
      panelClass: 'event-form-dialog',
      width: '480px',
      maxWidth: '100%',
      data: {
        action: 'new',
        sala: this.selectedRoom.sala.id,
        profissional: this.selectedProfessional,
        // sala: this.salaSelecionada,
        dataInicioReserva,
        dataFimReserva
      }
    }).afterClosed().subscribe(response => {
      this.events.splice(this.events.indexOf(this.dragToSelectEvent), 1)

      if (!response) {
        this.refresh.next(true);
        return;
      }
      if (this.selectedProfessional) {
      }
      response.value.periodoAgendamento = dataFimReserva.getHours() - dataInicioReserva.getHours()
      this.invoiceService.createReservation(
        {
          ...response.value,
          medicoId: this.authService.getAccessType() === 'ROLE_PROFISSIONAL'
            ? parseInt(this.authService.getUserId())
            : this.selectedProfessional.id
        }
      ).subscribe((solicitacaoReserva) => {


        this.solicitacaoReservaId.push({ solicitacaoReservaId: solicitacaoReserva.id, agendamentos: solicitacaoReserva.agendamentoDTOs });

        solicitacaoReserva && solicitacaoReserva.agendamentoDTOs.forEach(agendamento => {
          this.events.push(this.parseSchedulingToCalendarEvent(agendamento))
        })
        this.errosValidacaoReserva = []
        this.errosValidacaoReserva = solicitacaoReserva.erros.concat(this.errosValidacaoReserva)
        this.refreshResumoCompra.next()
        this.refresh.next(true);
        this.idScheduling.next(solicitacaoReserva.carrinhoCompra)
        this.getDetails(solicitacaoReserva.carrinhoCompra)
        this.cartNumber = solicitacaoReserva.carrinhoCompra
        this.descontoTotal = solicitacaoReserva.descontoTotal
      }, error => {
        console.log(error)
        this.openSnackBar(error.error.message, 'Ok', 5000);
      })
    })

  }

  validaIntevaloAgenda(dataInicioReserva: Date, dataFimReserva: Date): boolean {
    const diff = dataFimReserva.getHours() - dataInicioReserva.getHours()
    // tslint:disable-next-line:max-line-length
    if (diff === 1 && dataInicioReserva.getHours() !== 8 && dataInicioReserva.getHours() !== 11 && dataInicioReserva.getHours() !== 13 && dataInicioReserva.getHours() !== 16 && dataInicioReserva.getHours() !== 18 && dataInicioReserva.getHours() !== 21) {
      // tslint:disable-next-line:max-line-length
      this.showAlert('Agendamentos de 1 hora  são permitidos nos seguintes horários.\nManha  08:00 às 9:00 e 11:00 às 12:00\nTarde:  13:00 às 14:00 e 16:00 às 17:00\nNoite: 18:00 às 19:00 e 21:00 às 22:00')
      return false
    }
    if (diff === 2 && (dataInicioReserva.getHours() === 9 || dataInicioReserva.getHours() === 14 || dataInicioReserva.getHours() === 19)) {
      // tslint:disable-next-line:max-line-length
      this.showAlert('Agendamentos de 2 hora são permitidos nos seguintes horários.\nManha  08:00 às 10:00 e 10:00 às 12:00\nTarde:  13:00 às 15:00 e 16:00 às 18:00\nNoite: 18:00 às 20:00 e 20:00 às 22:00')
      return false
    }
    if (diff === 3) {
      this.showAlert('Não é possivel agendar 3 horas')
      return false
    }
    return true
  }



  showAlert(msg: string) {
    this.matDialog.open(AlertModalComponent, { data: { title: 'Reserva de salas', message: msg } }).afterClosed().subscribe(ok => {
      this.events = this.events.filter(e => e !== this.dragToSelectEvent);
    })
  }
  beforeWeekViewRender(renderEvent: CalendarWeekViewBeforeRenderEvent): void {
    this.calendarViewService.desabilitar(renderEvent)
  }
  closeOpenMonthViewDay() {
    // this.setupInit()
    this.getShedulings()
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
