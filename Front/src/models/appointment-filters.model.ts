import Room from 'src/models/room/room.model';
import Patient from './patient.model'
import Professional from './professional.model'
import { AppointmentStatus } from './appointment-Status.model'

export default class AppointmentFilters {

    public paciente?: Patient
    public profissional?: Professional
    public sala?: Room
    public status?: string
    public dataInicio?: Date
    public dataFim?: Date
}