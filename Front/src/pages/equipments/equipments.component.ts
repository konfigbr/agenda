import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import Equipment from 'src/models/equipment/equipment.model';
import { EquipmentService } from 'src/services/equipment.service';

@Component({
  selector: 'app-equipments',
  templateUrl: './equipments.component.html',
  styleUrls: ['./equipments.component.scss']
})
export class EquipmentsComponent implements OnInit {
  displayedColumns: string[] = ['dataEntrada', 'nome', 'tipoEquipamento', 'cobravel', 'valorCobravel', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private equipmentService: EquipmentService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.getEquipments()
  }

  getEquipments() {
    this.isLoading = true;
    this.equipmentService.getEquipments().subscribe((equipments: Equipment[]) => {
      this.dataSource = new MatTableDataSource(equipments)
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

  deletEquipment(id: string | number) {
    this.equipmentService.deleteEquipment(id).subscribe((res: any) => {
      this.openSnackBar('Equipamento apagado com sucesso!', 'Ok', 3000);
      this.getEquipments();
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 3000);
      console.log(error)
    })
  }
  deleteElement(equipment: Equipment): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar o equipamento "${equipment.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deletEquipment(equipment.id)
    });
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
