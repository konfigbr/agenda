import { Component, OnInit, ViewChild } from '@angular/core';
import { InvoicesService } from 'src/services/invoices.service';
import { AuthService } from 'src/services/auth.service';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { EquipmentService } from 'src/services/equipment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import EquipmentType from 'src/models/equipment/equipment-type.model';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';

@Component({
  selector: 'app-invoices',
  templateUrl: './invoices.component.html',
  styleUrls: ['./invoices.component.scss']
})
export class InvoicesComponent implements OnInit {
  displayedColumns: string[] = ['dataCobranca', 'valor', 'status', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  constructor(
    private invoicesService: InvoicesService,
    private authService: AuthService,
    private equipmentService: EquipmentService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getInvoicesByProfessional(this.authService.getUserId());
  }

  getInvoicesByProfessional(professionalId: string | number) {
    this.isLoading = true;
    this.invoicesService.getInvoicesByProfessional(professionalId).subscribe(res => {
      this.dataSource = new MatTableDataSource(res)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false
    }, error => {
      this.isLoading = false
      this.errorTableData = true
      this.openSnackBar(error.error.message, 'Ok', 5000)
      console.log(error)
    })
  }

  getEquipmentTypes() {
    this.isLoading = true
    this.dataSource = new MatTableDataSource([])
    this.equipmentService.getEquipmentTypes().subscribe((equipmentTypes: EquipmentType[]) => {
      this.dataSource = new MatTableDataSource(equipmentTypes)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false
    }, error => {
      this.isLoading = false
      this.errorTableData = true
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000)
    })
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  deleteElement(equipmentType: EquipmentType): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar o tipo de equipamento "${equipmentType.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deletEquipmentType(equipmentType.id)
    });
  }

  deletEquipmentType(id: string) {
    this.equipmentService.deleteEquipmentType(id).subscribe((res: any) => {
      this.openSnackBar('Tipo de equipamento apagado com sucesso!', 'Ok', 3000);
      this.getEquipmentTypes();
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
