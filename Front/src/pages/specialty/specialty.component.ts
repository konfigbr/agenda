import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { SpecialtyService} from 'src/services/specialty.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import Specialty from 'src/models/specialty.model';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';

@Component({
  selector: 'app-specialty',
  templateUrl: './specialty.component.html',
  styleUrls: ['./specialty.component.scss']
})
export class SpecialtyComponent implements OnInit {

  displayedColumns: string[] = ['nome','tabela-de-preco','actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private specialtyService : SpecialtyService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getSpecialties()
  }

    getSpecialties() {
    this.isLoading = true
    this.dataSource = new MatTableDataSource([])
    this.specialtyService. getSpecialties().subscribe((specialtyType: Specialty[]) => {
      this.dataSource = new MatTableDataSource(specialtyType)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false
    }, error => {
      this.isLoading = false
      this.errorTableData = true
      console.log(error)
    })
  } 
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  
  deleteElement(specialty: Specialty): void {

    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar a Especialidade "${specialty.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deletSpecialty(specialty.id)
    });
  }

  deletSpecialty(id: string | number) {
    this.specialtyService.deleteSpecialty(id).subscribe((res: any) => {
      this.openSnackBar('Especialidade apagada com sucesso!', 'Ok', 3000);
      this.getSpecialties();
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 3000);
      console.log(error)
    })
  } 
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

}
