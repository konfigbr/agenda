import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { AssistantService } from 'src/services/assistant.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import Assistant from 'src/models/assistant.model';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { UtilsService } from 'src/services/utils.service';

@Component({
  selector: 'app-assistants',
  templateUrl: './assistants.component.html',
  styleUrls: ['./assistants.component.scss']
})
export class AssistantsComponent implements OnInit {
  displayedColumns: string[] = ['nome', 'username', 'cpf', 'rg', 'telefone', 'nomeDoCurso', 'actions'];

  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private assistantService: AssistantService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    public utilsService: UtilsService
  ) { }

  ngOnInit(): void {
    this.getAssistants()
  }

  getAssistants() {
    this.isLoading = true
    this.dataSource = new MatTableDataSource([])
    this.assistantService.getAssistants().subscribe((assistants: Assistant[]) => {
      this.dataSource = new MatTableDataSource(assistants)
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
  deleteElement(assistant: Assistant): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar o(a) assistente "${assistant.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deleteAssistant(assistant.id)
    });
  }

  deleteAssistant(id: string | number) {
    this.assistantService.deleteAssistant(id).subscribe((res: any) => {
      this.openSnackBar('Assistente apagado(a) com sucesso!', 'Ok', 3000);
      this.getAssistants();
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
