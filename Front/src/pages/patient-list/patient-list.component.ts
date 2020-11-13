import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { PatientService } from 'src/services/patient.service';

@Component({
  selector: 'app-patient-list',
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.scss']
})
export class PatientListComponent implements OnInit {

  displayedColumns: string[] = ['nome', 'username','cpf','profissional'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private patientService: PatientService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getAdministrativeUsers()
  }
  getAdministrativeUsers() {
    this.isLoading = true

      this.patientService.getPatients().subscribe(patient => {
      this.dataSource = new MatTableDataSource(patient)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false
    }, error => {
      console.log(error)
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  deleteElement(paciente: any): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar o paciente "${paciente.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deletePatient(paciente.id)
    });
  }

  deletePatient(id: string) {
    this.patientService.deletePatient(id).subscribe(res => {
      this.openSnackBar('Paciente apagado com sucesso!', 'Ok', 3000);
      this.getAdministrativeUsers();
    }, error => {
      console.log(error)
      this.openSnackBar(error.erro.message, 'Ok', 5000)
    })

  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
  
  


}
