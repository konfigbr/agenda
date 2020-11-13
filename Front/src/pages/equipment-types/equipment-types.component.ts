import { Component, OnInit, ViewChild } from '@angular/core';
import { EquipmentService } from 'src/services/equipment.service';
import EquipmentType from 'src/models/equipment/equipment-type.model';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-equipment-types',
  templateUrl: './equipment-types.component.html',
  styleUrls: ['./equipment-types.component.scss']
})
export class EquipmentTypesComponent implements OnInit {

  displayedColumns: string[] = ['nome', 'valorCobravel', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private equipmentService: EquipmentService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getEquipmentTypes()
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