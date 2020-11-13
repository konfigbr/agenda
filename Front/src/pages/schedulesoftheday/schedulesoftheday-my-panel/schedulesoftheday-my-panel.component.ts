import { Component, OnInit } from '@angular/core';
import { AssistantService } from 'src/services/assistant.service';
import { AuthService } from 'src/services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CheckinService } from 'src/services/checkin.service';
import { ProfessionalService } from 'src/services/professional.service';
import { ActivatedRoute } from '@angular/router';
import { SchedulingService } from 'src/services/scheduling.service';



@Component({
  selector: 'app-schedulesoftheday-my-panel',
  templateUrl: './schedulesoftheday-my-panel.component.html',
  styleUrls: ['./schedulesoftheday-my-panel.component.scss']
})
export class SchedulesofthedayMyPanelComponent implements OnInit {
  schedule: any = null;
  currentScheduling: any;
  isLoading: boolean = false;
  accessType: string;
  observation: string = ''

  constructor(
    private schedulingService: SchedulingService,
    private route: ActivatedRoute,
    private assistantService: AssistantService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private checkinService: CheckinService,
    private professionalService: ProfessionalService,
    
  ) { }

  ngOnInit(): void {
    this.accessType = this.authService.getAccessType()
     this.route.params.subscribe(params => {
     
        params.id && this.getScheduling(params.id)
      }) 
  }

  getScheduling(id: string | number) {
    this.isLoading = true;
    this.schedulingService.getSchegulingDay(id).subscribe((currentScheduling: any) => {
      this.currentScheduling = currentScheduling
      this.isLoading = false; 
     
    }, error => {
      this.isLoading = false;    

    })
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }  
  
}
