import { Component, OnInit, ViewChild, Input, OnDestroy } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { Subject } from 'rxjs';
import { CartService } from 'src/services/cart.service';

@Component({
  selector: 'app-purchase-details',
  templateUrl: './purchase-details.component.html',
  styleUrls: ['./purchase-details.component.scss']
})
export class PurchaseDetailsComponent implements OnInit, OnDestroy {
  @ViewChild('saleDetail', { static: true }) saleDetail: MatSort;
  displayedColumnsSaleDetail: string[] = ['index', 'sala', 'data', 'horaInicio', 'horaFim', 'custo', 'equipamentos', 'total']
  dataSourceSaleDetail = new MatTableDataSource([])
  @Input() refresh: Subject<any>
  @Input() id: any
  @Input() data: any
  @Input() index: any
  total: number = 0;
  indexDetail: any;
  dataFull: any;

  constructor(
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    this.index.subscribe(index => {
      this.indexDetail = index
      if (this.dataFull && (index || index === 0)) {
        this.dataSourceSaleDetail = new MatTableDataSource(this.dataFull[this.indexDetail || 0].detalhes);
        this.total = this.dataFull[this.indexDetail || 0].custo
        this.dataSourceSaleDetail.sort = this.saleDetail;
      } else {
        this.dataSourceSaleDetail = new MatTableDataSource([])
      }

    })
    this.data.subscribe(data => {
      this.dataFull = data;
      if (this.dataFull && this.dataFull[this.indexDetail || 0] && this.dataFull[this.indexDetail || 0].detalhes) {

        this.dataSourceSaleDetail = new MatTableDataSource(this.dataFull[this.indexDetail || 0].detalhes);
        this.total = this.dataFull[this.indexDetail || 0].custo
        this.dataSourceSaleDetail.sort = this.saleDetail;
      } else {
        this.dataSourceSaleDetail = new MatTableDataSource([])
      }
    }, error => {
      console.log(error)
    })

  }
  ngOnDestroy(): void {
    this.refresh.unsubscribe();
    this.data.unsubscribe();
    this.index.unsubscribe();
  }
}
