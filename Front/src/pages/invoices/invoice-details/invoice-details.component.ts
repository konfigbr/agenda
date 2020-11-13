import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { InvoicesService } from 'src/services/invoices.service';

@Component({
  selector: 'app-invoice-details',
  templateUrl: './invoice-details.component.html',
  styleUrls: ['./invoice-details.component.scss']
})
export class InvoiceDetailsComponent implements OnInit {
  displayedColumns: string[] = ['room', 'date', 'start', 'end', 'value','nomeEquipamentos', 'valueEquipments', 'total'];
  displayedColumnsCredits: string[] = ['index', 'valor'];

  dataSource: any;
  dataSourceCredits: any;
  isLoading: boolean = false;
  errorTableData: boolean = false;
  invoice: any = {}

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  // @ViewChild(MatPaginator, { static: true }) paginatorCredit: MatPaginator;


  constructor(
    private invoicesService: InvoicesService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.getInvoiceDetail(params.id)
      } else {
        this.router.navigateByUrl('fatura')
      }
    })
  }
  test() {
    return new Date()
  }
  getInvoiceDetail(invoiceId: string | number) {
    this.isLoading = true
    this.dataSource = new MatTableDataSource([])
    this.invoicesService.getInvoiceDetail(invoiceId).subscribe(response => {
      this.invoice = response;
      this.dataSource = new MatTableDataSource(response.resumoCompraDetalhadoDTOS)
      this.dataSourceCredits = new MatTableDataSource(response.creditosUtilizados)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
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
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

}
