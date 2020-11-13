import AppointmentFilters from 'src/models/appointment-filters.model';
import Patient from 'src/models/patient.model';
import Professional from 'src/models/professional.model';
import Appointment from 'src/models/appointment-model';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  public badgeCount: number = 0;
  public badgeCount1: number = 0;
  public appointmentParam: Appointment | null = null;
  public professionalParam: Professional | null = null;
  public patientParam: Patient | null = null;
  public isReagendamento: boolean = false;
  public appointmentFilters: AppointmentFilters | null = null;
}
