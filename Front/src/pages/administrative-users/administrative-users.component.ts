import { Component, OnInit, ViewChild } from '@angular/core';
import { UserService } from 'src/services/user.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';

@Component({
  selector: 'app-administrative-users',
  templateUrl: './administrative-users.component.html',
  styleUrls: ['./administrative-users.component.scss']
})
export class AdministrativeUsersComponent implements OnInit {

  displayedColumns: string[] = ['nome', 'accessType', 'username', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private userService: UserService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getAdministrativeUsers()
  }
  getAdministrativeUsers() {
    this.isLoading = true

    this.userService.getAdministrativeUsers().subscribe(administrativeUsers => {
      this.dataSource = new MatTableDataSource(administrativeUsers)
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
  deleteElement(administrativeUser: any): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar o usuário "${administrativeUser.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deleteAdministrativeUser(administrativeUser.id)
    });
  }

  deleteAdministrativeUser(id: string) {
    this.userService.deleteAdministrativeUser(id).subscribe(res => {
      this.openSnackBar('Usuário administrativo apagado com sucesso!', 'Ok', 3000);
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
  getAccessType(accessType: string) {
    switch (accessType) {
      case 'ROLE_PROFISSIONAL':
        return 'Profissional'
      case 'ROLE_ASSISTENTE':
        return 'Assistente'
      case 'ROLE_ATENDENTE':
        return 'Atendente'
      case 'ROLE_ADMINISTRADOR':
        return 'Administrador'
      default:
        return "";
    }
  }

}
