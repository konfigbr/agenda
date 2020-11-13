import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import Equipment from 'src/models/equipment/equipment.model';
import { ReservationModalComponent } from '../reservation-modal/reservation-modal.component';
import { SchedulesofthedayMyPanelComponent } from './schedulesoftheday-my-panel/schedulesoftheday-my-panel.component';
import { SchedulingService } from 'src/services/scheduling.service';

@Component({
  selector: 'app-schedulesoftheday',
  templateUrl: './schedulesoftheday.component.html',
  styleUrls: ['./schedulesoftheday.component.scss']
})
export class SchedulesofthedayComponent implements OnInit {
  displayedColumns: string[] = ['nomeSala', 'periodo', 'assistente','profissional', 'especialidade', 'actions'];
  dataSource: any = [{}]
  isLoading: boolean = false;
  errorTableData: boolean = false;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    private schedulingService: SchedulingService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.getScheduling()
  }

  getScheduling() {
    this.isLoading = true;
    this.schedulingService.getSchegulingDayList().subscribe((agendamentos: any[]) => {
      this.dataSource = new MatTableDataSource(agendamentos)     
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.isLoading = false;
      this.errorTableData = false
    }, error => {
      this.isLoading = false;
      this.errorTableData = true

    })
  }
  public formatDate(date: string | Date) {
    return new Date(date).toLocaleDateString();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  openAssitantsScheduleModal() {

    const dialogRef = this.matDialog.open(SchedulesofthedayMyPanelComponent, {
      width: '480px',
      maxWidth: '100%',
      data: {}
    });



  }
  downloadSchedulingOfDay() {
    this.isLoading = true
    
    let fileName: string;
    this.schedulingService.downloadSchedulingOfDay().then(response => {
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
