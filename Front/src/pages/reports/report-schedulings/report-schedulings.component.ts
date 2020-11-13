import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import Professional from 'src/models/professional.model';
import { ProfessionalService } from 'src/services/professional.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { saveAs } from 'file-saver';
import { DateService } from 'src/services/date.service';
import { endOfDay, startOfDay } from 'date-fns';

@Component({
  selector: 'app-report-schedulings',
  templateUrl: './report-schedulings.component.html',
  styleUrls: ['./report-schedulings.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
  ]
})
export class ReportSchedulingsComponent implements OnInit {
  report: any = {}
  professionals: Professional[] = []
  reportSchedulingsFormGroup: FormGroup
  isLoading: boolean = false;

  displayedColumns: string[] = ['date', 'inicio', 'fim', 'status', 'custoHora', 'assistente', 'profissional', 'checkinAssistente', 'checkinProfessional', 'checkoutAssitente', 'checkoutProfessional', 'sala'];
  dataSource: any = new MatTableDataSource([])
  errorTableData: boolean = false;


  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private adapter: DateAdapter<any>,
    private professionalService: ProfessionalService,
    private schedulingService: SchedulingService,
    private dateService: DateService
  ) {
    this.adapter.setLocale('pt-br');
    this.reportSchedulingsFormGroup = this.formBuilder.group({
      professional: new FormControl('', [Validators.nullValidator]),
      cpf: new FormControl('', [Validators.nullValidator]),
      startDate: new FormControl('', [Validators.required]),
      endDate: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    this.getProfessionals()
  }
  onSubmit() {
    const params: any = {
      startDate: this.formatDate(startOfDay(this.report.startDate._d)),
      endDate: this.formatDate(endOfDay(this.report.endDate._d)),
      professionalId: this.report.professional ? this.report.professional.id : '',
      cpf: this.report.cpf ? this.report.cpf : ''
    }
    this.schedulingService.getReportProfessionalScheduling(params).subscribe(res => {
      this.dataSource = new MatTableDataSource(res)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false
    }, error => {
      console.log(error)
    })

  }
  formatDate(date: any) {
    const dt = new Date(date).toLocaleDateString().split('/');
    const dateFormated = `${dt[2]}-${dt[1].length === 2 ? dt[1] : '0' + dt[1]}-${dt[0].length === 2 ? dt[0] : '0' + dt[0]}`;
    const hourFormated = new Date(date).toLocaleTimeString().length === 8
      ? new Date(date).toLocaleTimeString()
      : '0' + new Date(date).toLocaleTimeString();

    return `${dateFormated}T${hourFormated}`
  }
  dowloadReport() {
    this.isLoading = true
    const start: Date = this.report.startDate._d;
    const end: Date = this.report.endDate._d;
    const params: any = {
      startDate: start.toISOString().split('.')[0],
      endDate: end.toISOString().split('.')[0],
      professionalId: this.report.professional ? this.report.professional.id : '',
      cpf: this.report.cpf ? this.report.cpf : ''
    }
    let fileName: string;
    this.schedulingService.downloadReportProfessionalScheduling(params).then(response => {
      fileName = response.headers.get('Content-Disposition').split('"')[1].toLocaleLowerCase()
      return response.arrayBuffer()
    })
      .then(result => {
        const blob = new Blob([new Int8Array(result)], { type: "application/octet-stream" })
        saveAs(blob, fileName)
        this.isLoading = false;
      })
      .catch(error => {
        console.log(error)
        this.isLoading = false;
      });
  }
  getProfessionals() {
    this.professionalService.getProfessionals().subscribe(professionals => {
      this.professionals = professionals
    }, error => {
      console.log(error)
    })
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
