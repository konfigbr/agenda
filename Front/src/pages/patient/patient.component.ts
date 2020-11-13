import { AuthService } from './../../services/auth.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import Patient from 'src/models/patient.model';
import { PatientService } from 'src/services/patient.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ConsultaService } from 'src/services/consulta.service';
import Appointment from 'src/models/appointment-model';
import { UtilsService } from 'src/services/utils.service';

@Component({
  selector: 'app-patient',
  templateUrl: './patient.component.html',
  styleUrls: ['./patient.component.scss']
})
export class PatientComponent implements OnInit {
  
  displayedColumns: string[] = ['status', 'profissional', 'data', 'inicio', 'fim', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private consultaService: ConsultaService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private utilsService: UtilsService,
    private router: Router,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    const professionalKey = this.route.snapshot.params.professionalKey
    if (this.getProfessionalKey() !== professionalKey)
      return this.authService.logoutPatient(this.getProfessionalKey())
    
    this.getMyAppointment()
  }

  getMyAppointment() {
    this.isLoading = true;
    const obj = this.getProfessionalObj()
    this.consultaService.getMyAppointments(obj.id).subscribe((consultations: Appointment[]) => {
      this.dataSource = new MatTableDataSource(consultations)
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false;
      this.errorTableData = false


    }, error => {
      this.isLoading = false;
      this.errorTableData = true

    })
  }
  public formatDate(date: string | Date) {
    return new Date(date).toLocaleDateString();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

   deletConsult(id: string | number) {
    this.consultaService.deleteAppointment(id).subscribe((res: any) => {
      this.openSnackBar('Consulta cancelada com sucesso !', 'Ok', 3000);
      this. getMyAppointment();
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
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

  getProfessionalKey() {
    return localStorage.getItem("professionalKey")
  }

  getProfessionalObj() {
    return JSON.parse(localStorage.getItem("professionalObj"))
  }

  formatDateWithPattern(date: string, pattern: string): string {
    return this.utilsService.formatDate(date, pattern)
  }
}
