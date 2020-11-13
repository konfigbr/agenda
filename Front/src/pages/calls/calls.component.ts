import { Component, OnInit, ViewChild, AfterContentInit, AfterViewInit } from '@angular/core';

import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { PabxService } from 'src/services/pabx.service';
import { Call } from 'src/models/call-.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-calls',
  templateUrl: './calls.component.html',
  styleUrls: ['./calls.component.scss']
})
export class CallsComponent implements OnInit, AfterContentInit {

  displayedColumns: string[] = ['medico', 'dataHora', 'url', 'telefone', 'ramal', 'acoes'];
  dataSource: any = [{}]
  isLoading: boolean = false;
  errorTableData: boolean = false;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private pabxService: PabxService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource([]);
    this.getCalls();
  }

  ngAfterContentInit() {
    this.pabxService.load();
  }

  minhasConsultas(call: Call) {
    localStorage.setItem('professionalObj', JSON.stringify(call.professionalObj));
    localStorage.setItem("professionalKey", call.professionalKey);
    this.navigate('minhas-consultas');
  }

  agendarConsultas(call: Call) {
    localStorage.setItem('professionalObj', JSON.stringify(call.professionalObj));
    localStorage.setItem("professionalKey", call.professionalKey);
    this.navigate('agendar-consultas');
  }

  finalizarLigacao(call: Call) {
    this.pabxService.remove(call);
  }
  
  private navigate(route: String) {
    this.router.navigate([`${this.getProfessionalKey()}/${route}`], { replaceUrl: true })
  }

  getCalls() {
    this.pabxService.getCalls.subscribe((calls: Map<String, Call>) => {
      this.isLoading = true;
      let callsDataSource: any[] = [];
      for (let call of calls.values()) {
        callsDataSource.push({
          uuid: call.uuid,
          medico: JSON.parse(call.professionalObj).nome,
          url: call.professionalKey,
          ramal: call.extensionPhone,
          phone: call.phone,
          extensionPhone: call.extensionPhone,
          dateTime: call.extensionPhone,
          professionalKey: call.professionalKey,
          professionalObj: JSON.parse(call.professionalObj)
        });
      }
      this.dataSource = new MatTableDataSource(callsDataSource);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false;
      this.errorTableData = false
    }, error => {
      this.isLoading = false;
      this.errorTableData = true

    });
  }

  private getProfessionalKey() {
    return localStorage.getItem("professionalKey")
  }
}
