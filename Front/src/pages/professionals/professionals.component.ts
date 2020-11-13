import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import Professional from 'src/models/professional.model';
import { ProfessionalService } from 'src/services/professional.service';
import { MessageProfessionalAproveComponent } from 'src/components/message-professional-aprove/message-professional-aprove.component';
import { DataService } from 'src/services/data.service';
import { AuthService } from 'src/services/auth.service';
import { ProfessionalsDiscountModalComponent } from './professionals-discount-modal/professionals-discount-modal.component';

@Component({
  selector: 'app-professionals',
  templateUrl: './professionals.component.html',
  styleUrls: ['./professionals.component.scss'],
})
export class ProfessionalsComponent implements OnInit {
  displayedColumns: string[] = ['status', 'nome', 'username', 'tipoDocumento', 'cpf', 'rg', 'especialidade', 'crm', 'telefone', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private authService: AuthService,
    private professionalService: ProfessionalService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    this.getProfessionals()
  }

  getProfessionals() {
    this.isLoading = true
    this.dataSource = new MatTableDataSource([])
    this.professionalService.getProfessionalsWithDiscountAttribute().subscribe((professionals: any[]) => {
      professionals.map(professional =>{
        professional.nomeEspecialidade = professional.especialidade?.nome
      })
      this.dataService.badgeCount = professionals.filter(professional => professional.status === 'AGUARDANDO_APROVAÇÃO').length
      this.dataSource = new MatTableDataSource<Professional>(professionals)
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
  applyFilter(filter: string) {
    this.dataSource.filter = filter.trim().toLowerCase();
  }
  deleteElement(professional: any): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar o profissional "${professional.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deleteProfessional(professional.id)
    })
  }
  toInt(element: any) {
    return parseInt(element);
  }
  canDelete() {
    return this.authService.getAccessType() === 'ROLE_ADMINISTRADOR';
  }
  preView(){
    return this.authService.getAccessType() === 'ROLE_ATENDENTE';
  }
  deleteProfessional(id: string) {
    this.professionalService.deleteProfessional(id).subscribe((res: any) => {
      this.openSnackBar('Profissional apagado com sucesso!', 'Ok', 3000);
      this.getProfessionals();
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

  aproveProfessional(idProfessional): void {
    const dialogRef = this.dialog.open(MessageProfessionalAproveComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar o equipamento ""?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      // result && this.deletEquipment(equipment.id)
    });

}
discountProfessional(professional: any): void {
  const dialogRef = this.dialog.open(ProfessionalsDiscountModalComponent, {
    width: '500px',
    data: { message: `Ações` ,
    professional: professional.id,
  }
  });

  dialogRef.afterClosed().subscribe(result => {
    if(result){
      this.getProfessionals();
    }
    
  });
}



}
