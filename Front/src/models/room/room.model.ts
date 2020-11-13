import Local from '../local.model';
import Specialty from '../specialty.model';

export default class Room {
  public id?: string;
  public nome: string = "";
  public especialidade!: Specialty;
  public local!: Local;
  public reserva?: boolean;
  public status!: string;
}
