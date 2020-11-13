import { DataService } from 'src/services/data.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import Appointment from 'src/models/appointment-model';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from 'src/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { UtilsService } from 'src/services/utils.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConsultaService } from 'src/services/consulta.service';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ProfessionalService } from 'src/services/professional.service';
import Professional from 'src/models/professional.model';
import Room from 'src/models/room/room.model';
import Patient from 'src/models/patient.model';
import { AppointmentStatus } from 'src/models/appointment-Status.model';
import { PatientService } from 'src/services/patient.service';
import { RoomService } from 'src/services/room.service';
import { endOfDay, startOfDay } from 'date-fns';
import { AppointmentAdministrativeModalComponent } from './appointment-administrative-modal/appointment-administrative-modal.component';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import AppointmentFilters from 'src/models/appointment-filters.model';

@Component({
  selector: 'app-appointment-administrative',
  templateUrl: './appointment-administrative.component.html',
  styleUrls: ['./appointment-administrative.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
  ]
})
export class AppointmentAdministrativeComponent implements OnInit {
  professional: Professional = null
  room: Room = null
  patient: Patient = null
  status: string = ''
  startDate: Date = null
  endDate: Date = null

  displayedColumns: string[] = ['status', 'paciente', 'telpaciente', 'profissional', 'telprofissional', 'data', 'inicio', 'fim', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  statusList = [
    { label: 'Solicitada', value: 'SOLICITADA' },
    { label: 'Aprovada', value: 'APROVADA' },
    { label: 'Confirmada', value: 'CONFIRMADA' },
    { label: 'Não confirmada', value: 'NAO_CONFIRMADA' },
    { label: 'Cancelada', value: 'CANCELADA' },
  ]

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  
  professionals: Professional[] = []
  rooms: Room[] = []
  patients: Patient[] = []
  appointmentAdministrativeFormGroup: FormGroup

  constructor(
    private consultaService: ConsultaService,
    private professionalService: ProfessionalService,
    private roomService: RoomService,
    private patientService: PatientService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private utilsService: UtilsService,
    private router: Router,
    private authService: AuthService,
    private adapter: DateAdapter<any>,
    private dataService: DataService,
  ) {
    this.adapter.setLocale('pt-br');
    this.appointmentAdministrativeFormGroup = this.formBuilder.group({
      professional: new FormControl('', [Validators.nullValidator]),
      room: new FormControl('', [Validators.nullValidator]),
      patient: new FormControl('', [Validators.nullValidator]),
      status: new FormControl('', [Validators.nullValidator]),
      startDate: new FormControl('', [Validators.required]),
      endDate: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    this.getPatients()
  }

  getFilters() {
    this.startDate = this.dataService.appointmentFilters && this.dataService.appointmentFilters?.dataInicio
    this.endDate = this.dataService.appointmentFilters && this.dataService.appointmentFilters?.dataFim
    this.status = this.dataService.appointmentFilters && this.dataService.appointmentFilters?.status

    if (this.dataService.appointmentFilters) {
      this.dataService.appointmentFilters.dataInicio = null
      this.dataService.appointmentFilters.dataFim = null
      this.dataService.appointmentFilters.status = null
    }
    this.getProfessionals()
  }

  onSubmit() {
    if(this.startDate !== null && this.endDate !== null){
      const accessType = this.authService.getAccessType()

      const start: any =  this.startDate
      const end: any =  this.endDate

      const params = {
        medicoId: this.professional?.id || '',
        dataInicio: this.formatDate(startOfDay(start._d)),
        dataFim: this.formatDate(endOfDay(end._d)),
        salaId: this.room?.id || '',
        status: this.status || '',
       pacienteId: this.patient?.id || ''
      }

      if (accessType === 'ROLE_ADMINISTRADOR' || accessType === 'ROLE_ATENDENTE') {
       this.consultaService.getAppointments(params).subscribe((consultas: Appointment[]) => this.loadTable(consultas))
      } else {
        this.professionalService.getMyAppointments(params).subscribe((consultas: Appointment[]) => this.loadTable(consultas))
      }
    }
  }

  getMyAppointment() {
    this.isLoading = true;

    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {

      this.consultaService.getWithLimitProf().subscribe((consultations: Appointment[]) => {
        this.loadTable(consultations)
      }, error => {
        this.isLoading = false;
        this.errorTableData = true

      })
    } else if (this.authService.getAccessType() === 'ROLE_ADMINISTRADOR' || this.authService.getAccessType() === 'ROLE_ATENDENTE') {

      this.consultaService.getWithLimit().subscribe((consultations: Appointment[]) => {

        this.loadTable(consultations)
      }, error => {
        this.isLoading = false;
        this.errorTableData = true

      })
    }
  }

  private loadTable(data: Appointment[]): void {
    this.dataSource = new MatTableDataSource(data)
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.isLoading = false;
    this.errorTableData = false
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  getAccessType() {
    return this.authService.getAccessType()
  }

  deletConsult(id: string | number) {
    this.consultaService.deleteAppointment(id).subscribe((res: any) => {
      this.openSnackBar('Consulta cancelada com sucesso !', 'Ok', 3000);
      this.getMyAppointment();
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 3000);
      console.log(error)
    })
  }
  deleteElement(consultations: Appointment): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar a Consulta?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deletConsult(consultations.id)
    });
  }

  editConsulta(consultations: Appointment): void {
    if (consultations.status.toString() == 'CANCELADA' ||
      consultations.status.toString() === 'NAO_CONFIRMADA' ||
      consultations.status.toString() === 'CONFIRMADA') {

      this.openSnackBar('Não é possivel alterar o Status!', 'Ok', 3000);
    } else {
      const dialogRef = this.dialog.open(AppointmentAdministrativeModalComponent, {
        width: '400px',
        data: { message: `Deseja Editar Consulta?`, status: consultations.status, id: consultations.id }
      }).afterClosed().subscribe((result: boolean) => {
        
        if (result)
          this.getMyAppointment()

      })
    }
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

  getProfessionals() {

    if (this.authService.getAccessType() !== 'ROLE_PROFISSIONAL') {
      this.professionalService.getProfessionals().subscribe(professionals => {
        this.professionals = professionals

        if (this.dataService.appointmentFilters) {
          this.professional = this.professionals.find(x => x.id == this.dataService.appointmentFilters?.profissional.id)
          this.dataService.appointmentFilters.profissional = null
        }

        this.getRooms()
      }, error => {
        console.log(error)
      })
    } else {
      this.getRooms()
    }

  }

  block() {
    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {
      this.appointmentAdministrativeFormGroup.controls.professional.disable()
    }
  }

  getPatients() {
    this.patientService.getPatients().subscribe(patients => {
      this.patients = patients

      if (this.dataService.appointmentFilters) {
        this.patient = this.patients.find(x => x.id == this.dataService.appointmentFilters?.paciente.id)
        this.dataService.appointmentFilters.paciente = null
      }

      this.getFilters()
    }, error => {
      console.log(error)
    })
  }

  getRooms() {
    this.roomService.getRooms().subscribe(rooms => {
      this.rooms = rooms

      if (this.dataService.appointmentFilters != null && this.startDate != null && this.endDate != null) {
        this.room = this.rooms.find(x => x.id == this.dataService.appointmentFilters?.sala?.id)
        this.dataService.appointmentFilters.sala = null
      }

      if (this.dataService.appointmentFilters != null && this.startDate != null && this.endDate != null) {
        this.onSubmit()
      } else {
        this.getMyAppointment()
      }
    }, error => {
      console.log(error)
    })
  }

  formatDateWithPattern(date: string, pattern: string): string {
    return this.utilsService.formatDate(date, pattern)
  }

  formatDate(date: any) {
    const dt = new Date(date).toLocaleDateString().split('/');

    const dateFormated = `${dt[2]}-${dt[1].length === 2 ? dt[1] : '0' + dt[1]}-${dt[0].length === 2 ? dt[0] : '0' + dt[0]}`;
    const hourFormated = new Date(date).toLocaleTimeString().length === 8
      ? new Date(date).toLocaleTimeString()
      : '0' + new Date(date).toLocaleTimeString();

    return `${dateFormated}T${hourFormated}`
  }

  rescheduling(appointment: Appointment): void {
    this.dataService.appointmentParam = appointment
    this.dataService.professionalParam = appointment.medico
    this.dataService.patientParam = appointment.paciente
    this.dataService.isReagendamento = true

    if (this.startDate != null && this.endDate != null) {
      const filters: AppointmentFilters = {
        profissional: this.professional,
        paciente: this.patient,
        sala: this.room,
        dataInicio: this.startDate,
        dataFim: this.endDate,
        status: this.status,
      }
      this.dataService.appointmentFilters = filters
    }

    this.router.navigate(["/agendamento-consulta", appointment.id], { replaceUrl: false })
  }

  schedulingAppointment(): void {
    localStorage.setItem("isReagendamento", "false")
    this.router.navigate(["/agendamento-consulta/new"], { replaceUrl: false })
  }
}
