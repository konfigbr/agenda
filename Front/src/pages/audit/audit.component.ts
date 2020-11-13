import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { AuditService } from 'src/services/audit.service';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import Audit from 'src/models/audit.model';

@Component({
  selector: 'app-audit',
  templateUrl: './audit.component.html',
  styleUrls: ['./audit.component.scss']
})
export class AuditComponent implements OnInit {


  displayedColumns: string[] = ['nome', 'tipo', 'data', 'descricao'];
  dataSource: any;
  isLoading: boolean = false;
 
  errorTableData: boolean = false;


  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private auditService: AuditService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getAll()
  }


  getAll() {
    const dataParaExibir = new Date().toLocaleString()
    this.isLoading = true
    this.auditService.getAll().subscribe((audits: Audit[]) => {

      audits.map(audit => {
        const dataParaExibir = new Date(audit.dateCreated).toLocaleString()
        audit.dataExibir = dataParaExibir
      })
      this.dataSource = new MatTableDataSource(audits)
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




  getLabelTipo(tipo: string) {
    switch (tipo) {
      case 'LOGIN':
        return 'Login'
      case 'LOGOUT':
        return 'Logout'
      case 'AGENDAMENTO':
        return 'Agendamento'
      case 'REMOCAO_EQUIPAMENTO':
        return 'Remoção Equipamento'
      case 'ADICAO_EQUIPAMENTO':
        return 'Adição de Equipamento'
      case 'APAGAR_AGENDAMENTO':
        return 'Apagar Agendamento'
      case 'CHECKIN':
        return 'Check-in'
      case 'CHECKOUT':
        return 'Check-out'
      case 'AUMENTO_PERIODO':
        return 'Aumento de Periodo'
        case 'APROVACAO_CONSULTA':
        return 'Aprovação da Consulta'
        case 'CONFIRMACAO_CONSULTA':
        return 'Confirmação da Consulta'
        case 'NAO_CONFIRMACAO_CONSULTA':
        return 'Consulta não Confirmada'
        case 'REAGENDAMENTO_CONSULTA':
        return 'Reagendamento da Consulta'
      case 'EDICAO_PERFIL':
        return 'Edição de Perfil'
      case 'REALOCACAO_AGENDAMENTO':
        return 'Realocação de Equipamento'
      case 'NOVA_CONSULTA':
        return 'Nova Consulta'
      case 'CANCELAR_CONSULTA':
        return 'Cancelar Consulta'
      default:
        return tipo
    }
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }



  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

}
