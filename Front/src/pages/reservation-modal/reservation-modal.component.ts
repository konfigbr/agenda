import { Component, OnInit, Inject } from '@angular/core';
import { SchedulingService } from 'src/services/scheduling.service';
import { addHours, addMinutes, endOfWeek, isBefore, isSameDay, setHours, startOfWeek } from 'date-fns';
import { CalendarEvent } from 'angular-calendar';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-reservation-modal',
  templateUrl: './reservation-modal.component.html',
  styleUrls: ['./reservation-modal.component.scss']
})
export class ReservationModalComponent implements OnInit {

  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  isLoading: boolean =  false;

  reservas: any

  constructor(
    private snackBar: MatSnackBar,
    private schedulingService: SchedulingService,
    public dialogRef: MatDialogRef<ReservationModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {



  }

  ngOnInit(): void {
    this.getAssistantsSchedules()
    this.events
  
  }




  getAssistantsSchedules() {
    this.isLoading = true;
    const localeEndDate = endOfWeek(this.viewDate).toLocaleDateString().split('/');

    const params = {
      sala: this.data.selectedRoom.sala.id,
      inicio: startOfWeek(this.viewDate).toISOString().split('T')[0],
      fim: `${localeEndDate[2]}-${localeEndDate[1].length === 2 ? localeEndDate[1] : '0' + localeEndDate[1]}-${localeEndDate[0].length === 2 ? localeEndDate[0] : '0' + localeEndDate[0]}`
    }
    
    this.schedulingService.getAssistantsSchedules(params).subscribe((res: any) => {
      
      this.events = res
      
      this.isLoading = false;

    }, error => {
      console.log(error)
      this.isLoading = false;
      this.openSnackBar(error.error.message,"Ok",5000)
    })
  }
  getLabelDay(day: string){
    switch(day) {
        case 'SUNDAY':
        return 'Domingo'
        case 'MONDAY':
        return 'Segunda-feira'
        case 'TUESDAY':
        return 'Terça-feira'
        case 'WEDNESDAY':
        return 'Quarta-feira'
        case 'THURSDAY':
        return 'Quinta-feira'
        case 'FRIDAY':
        return 'Sexta-feira'
        case 'SATURDAY':
        return 'Sábado'
        default:
        return day
    }                                        
}
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
  close() {
    this.dialogRef.close();
  }


}
