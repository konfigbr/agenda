
import { RequestStatus } from './request-status.model';

export interface TypeEquipmentTime {
  id: number
  nome: string
  status: any
  inicio: string | Date
  fim: string | Date
}
