import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { PriceService } from 'src/services/price.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import PriceRange from 'src/models/price-range.model';
import Price from 'src/models/price.model';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';

@Component({
  selector: 'app-prices',
  templateUrl: './prices.component.html',
  styleUrls: ['./prices.component.scss']
})
export class PricesComponent implements OnInit {

  displayedColumns: string[] = ['nome', 'actions'];
  dataSource: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private priceService: PriceService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getPrices()
  }

  getPrices() {
    this.isLoading = true
    this.dataSource = new MatTableDataSource([])
    this.priceService.getPrices().subscribe((PriceRange: Price[]) => {
      this.dataSource = new MatTableDataSource(PriceRange)
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


  deleteElement(price: Price): void {
    const dialogRef = this.dialog.open(MessageModalComponent, {
      width: '400px',
      data: { message: `Deseja mesmo apagar a tabela de preços "${price.nome}"?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      result && this.deletePrice(price.id+'')
    });
  }

  deletePrice(id: string) {
    this.priceService.deletePrice(id).subscribe((res: any) => {
      this.openSnackBar('Tabela de preços apagado com sucesso!', 'Ok', 3000);
      this.getPrices();
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
