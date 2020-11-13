import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { ActivatedRoute, Router } from '@angular/router';
import Professional from 'src/models/professional.model';
import { ProfessionalService } from 'src/services/professional.service';
import { MessageProfessionalAproveComponent } from 'src/components/message-professional-aprove/message-professional-aprove.component';
import { DataService } from 'src/services/data.service';
import { AuthService } from 'src/services/auth.service';

@Component({
  selector: 'app-professionals',
  templateUrl: './professionals.component.html',
  styleUrls: ['./professionals.component.scss'],
})
export class EmpresaProfissionaisComponent implements OnInit {
  displayedColumns: string[] = ['status', 'nome','crm', 'rg', 'cpf', 'telefone' , 'actions'];
  dataSource: any;
  responsavel:any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private route: ActivatedRoute,
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
    this.route.params.subscribe(params => {
            this.getResponsavelTecnicoByEmpresa (params.id)
          })

  }
  getResponsavelTecnicoByEmpresa (empresaId) {
      this.professionalService.getResponsavelTecnicoByEmpresa(empresaId).subscribe((responsavel: Professional) => {
               this.responsavel=responsavel
               this.getProfissionaisByEmpresa(empresaId)
            }, error => {
              this.isLoading = false
              this.errorTableData = true
              console.log(error)
            })
  }
  getProfissionaisByEmpresa (empresaId) {
    this.professionalService.getProfessionalByEmpresa(empresaId).subscribe((professionals: Professional[]) => {
        this.dataService.badgeCount = professionals.filter(professional => professional.status === 'AGUARDANDO_APROVAÇÃO').length
        this.dataSource = new MatTableDataSource(professionals)
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
}
