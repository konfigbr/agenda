import { Component, OnInit, ViewChild } from '@angular/core';
import { SpecialtyService } from 'src/services/specialty.service';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { SchedulingService } from 'src/services/scheduling.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-report-professionals',
  templateUrl: './report-professionals.component.html',
  styleUrls: ['./report-professionals.component.scss']
})
export class ReportProfessionalsComponent implements OnInit {

  report: any = {}
  reportProfessionalsFormGroup: FormGroup
  isLoading: boolean = false;
  specialities: any[] = []

  displayedColumns: string[] = ['nome', 'rg', 'cpf', 'especialidade', 'documentoProfissional', 'telefone', 'email']
  dataSource: any = new MatTableDataSource([])
  errorTableData: boolean = false;


  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private specialityService: SpecialtyService,
    private schedulingService: SchedulingService
  ) {
    this.reportProfessionalsFormGroup = this.formBuilder.group({
      speciality: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    this.getSpecialities();
  }
  // openSnackBar(message: string, action: string, timing: number) {
  //   this.snackBar.open(message, action, {
  //     duration: timing,
  //   });
  // }
  getSpecialities() {
    this.specialityService.getProfessionalSpecialties().subscribe(res => {
      this.specialities = res
    }, error => {
      console.log(error)
    })
  }
  onSubmit() {
    this.isLoading = true
    this.schedulingService.getReportProfessional(this.report.speciality.id).subscribe(res => {
      this.dataSource = new MatTableDataSource(res)
      this.errorTableData = false
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false
    }, error => {
      console.log(error)
    })
  }
  dowloadReport() {
    this.isLoading = true;
    let fileName: string;
    this.schedulingService.downloadReportProfessional(this.report.speciality.id).then(response => {
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
}
