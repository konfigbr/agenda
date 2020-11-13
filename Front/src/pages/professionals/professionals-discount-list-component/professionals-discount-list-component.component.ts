
import { Component, OnInit, ViewChild, Inject, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import Discount from 'src/models/discount-model';
import { DiscountService } from 'src/services/discount.service';
import { DataService } from 'src/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { ProfessionalsDiscountModalComponent } from '../professionals-discount-modal/professionals-discount-modal.component';
import { UtilsService } from 'src/services/utils.service';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';


@Component({
  selector: 'app-professionals-discount-list-component',
  templateUrl: './professionals-discount-list-component.component.html',
  styleUrls: ['./professionals-discount-list-component.component.scss']
})
export class ProfessionalsDiscountListComponentComponent implements OnInit {
  @Input() selectedFunction:string;
  discountFormGroup: FormGroup
  displayedColumns: string[] = ['ativo', 'tipo', 'percentual', 'dataInicio', 'dataFim'];
  dataSource: any;
  idProfissional: string = ''
  functionSelected:string;
  step:string = 'SELECTION';
  isLoading: boolean = false;
  errorTableData: boolean = false;
  refreshUsers:boolean = false;
  @Output() refreshUsersEvent = new EventEmitter<boolean>();
  @Output() stepEvent = new EventEmitter<string>();
  discountToEditId: number;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  

  constructor(
    private discountService: DiscountService,
    public dialog: MatDialog,
    private utilsService: UtilsService,
    private snackBar: MatSnackBar,
    private dataService: DataService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private router: Router,
    public dialogRef: MatDialogRef<ProfessionalsDiscountModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {

  }

  ngOnInit(): void {
    
    if(this.selectedFunction === "EDIT"){
      this.displayedColumns.push('acoes');
      this.getCurrent();
    }else{
      this.getAllList();
    }
    
  }
  
  goToStep(step:string){
    this.functionSelected = step;
    this.step=step
  }

  sendStep(step:string) {
    this.sendRefreshUsers();
    this.stepEvent.emit(step);
  }


  receiveStep($event: string){
    this.step = $event;
  }

  receiveRefreshUsers($event: boolean){
    console.log('refresh  ', $event);
    this.refreshUsers = $event;
  }

  sendRefreshUsers(){
    this.refreshUsersEvent.emit(this.refreshUsers);
  }

  setEditDiscount(discountId: number){
    this.discountToEditId = discountId;
    this.goToStep('EDIT_DISCOUNT');
  }

  getAllList() {
    this.discountService.getAllDiscountsByProfessional(this.data.professional).subscribe((discounts: any[]) => {
      this.dataSource = new MatTableDataSource(discounts)
      this.errorTableData = false
      this.isLoading = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    }, error => {
      this.errorTableData = true
      this.isLoading = false
      console.log(error)
    })
  }

  getCurrent() {
    this.discountService.getCurrentDiscountsByProfessional(this.data.professional).subscribe((discounts: any[]) => {
      this.dataSource = new MatTableDataSource(discounts)
      this.errorTableData = false
      this.isLoading = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    }, error => {
      this.errorTableData = true
      this.isLoading = false
      console.log(error)
    })
  }

  formatDateWithPattern(date: string, pattern: string): string {
    return this.utilsService.formatDate(date, pattern)
  }

  getTranslate(ativo: string) {

    if (ativo) {
      return 'Ativo'
    } else {
      return 'Inativo'
    }
    

  }
  getLabelTipo(tipo: string) {
    switch (tipo) {
        case 'PERIODO':
        return 'Periodo'
      case 'HORA_AVULSA':
        return 'Hora Avulsa'
      default:
        return tipo
    }
  }
  editDiscount(professional: any): void {
    const dialogRef = this.dialog.open(ProfessionalsDiscountModalComponent, {
      width: '300px',
      data: { message: `Deseja Editar o Desconto Selecionando` ,
      professional: professional.id,
    }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      // result && this.deletEquipment(equipment.id)
    });
  }

  deleteDiscountAtivo(id:number) {
    console.log(id)
    this.discountService.deleteDiscount(id).subscribe((res: any) => {
      
      this. getCurrent();
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 3000);
      console.log(error)
    })
  }

  deleteElement(id:number): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '300px',
      data: { message: `Deseja mesmo inativar este desconto ?`, apagarText: 'Inativar'  }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deleteDiscountAtivo(id)
    })
  }
  
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }



}
