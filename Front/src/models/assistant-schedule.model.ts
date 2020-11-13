import Room from './room/room.model';
import Assistant from './assistant.model';

export interface AssistantSchedule {
  id: number;
  assistente: Assistant;
  sala: Room;
  diaDaSemana: number;
  horaDoDia: number;
}
