import Patient from './patient.model'
import Professional from './professional.model'
import Room from './room/room.model';
import { AppointmentStatus } from './appointment-Status.model'

export default class Appointment {

    public id?: number
    public version?: number
    public paciente?: Patient
    public medico?: Professional
    public dataInicio?: string | Date
    public dataFim?: string | Date
    public motivo?: string
    public justify?: string
    public status?: AppointmentStatus
    public sala?: Room
}