import { RequestStatus } from './request-status.model';
import { ReservationType } from './reservation-type.model';
import { SchedulingStatus } from './scheduling-status.model';
import { TypeEquipmentTime } from './type-equipment-time.model';
import Professional from './professional.model';
import Assistant from './assistant.model';
import { SchedulingPeriod } from './scheduling-period.model';

export default class Scheduling {

  public id?: number
  public status!: any
  public solicitacaoReservaId!: number
  public sala!: any
  public medico!: Professional
  public assistente!: Assistant
  public inicio!: string | Date
  public fim!: string | Date
  public tipoReserva!: ReservationType
  public equipamentos!: TypeEquipmentTime[]
  public agendamentoStatus!: SchedulingStatus
  public periodo!: SchedulingPeriod
  public user?: any;
  public agendamentoStatusAtual?: any
  public tiposEquipamentos?: any
}
