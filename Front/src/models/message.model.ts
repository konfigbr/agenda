import { User } from './user.model';
import Room from './room/room.model';

export interface Mensagem {
    dateCreated: string | Date
    remetente: User
    sala: Room
    mensagem: string
}
