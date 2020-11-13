import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FinishSaleComponent } from '../finish-sale/finish-sale.component';
import { UtilsService } from 'src/services/utils.service';

@Component({
  selector: 'app-prices-table-modal',
  templateUrl: './prices-table-modal.component.html',
  styleUrls: ['./prices-table-modal.component.scss']
})
export class PricesTableModalComponent implements OnInit {

  displayedColumnsPrices: string[] = ['totalHoras', 'valor'];
  @ViewChild('pricesSort', { static: true }) pricesSort: MatSort;
  
  
  dataSourcePrices = null
  discountAvulso = null
  discountPeriodo= null

  constructor(
    public dialogRef: MatDialogRef<FinishSaleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private utilsService : UtilsService,
  ) {
    this.dataSourcePrices= new MatTableDataSource(this.data.faixaPrecos)
   this.discountAvulso =this.data.discountAvulso
   this.discountPeriodo=this.data.discountPeriodo

   }

  ngOnInit(): void {
    console.log(this.discountPeriodo)
    this.dataSourcePrices.sort = this.pricesSort;
    
    
    
  }

  close() {
    this.dialogRef.close();
  }
  formatDateWithPattern(date: string, pattern: string): string {
    return this.utilsService.formatDate(date, pattern)
  }

 }
 


