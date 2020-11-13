import { Component, OnInit } from '@angular/core';
import { AssistantService } from 'src/services/assistant.service';
import { AuthService } from 'src/services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { CheckingCheckoutProfessionalAuthComponent } from './checking-checkout-professional-auth/checking-checkout-professional-auth.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CheckinService } from 'src/services/checkin.service';
import { ProfessionalService } from 'src/services/professional.service';
import { IncreaseSchedulingPeriodModalComponent } from './increase-scheduling-period-modal/increase-scheduling-period-modal.component';

@Component({
  selector: 'app-my-panel',
  templateUrl: './my-panel.component.html',
  styleUrls: ['./my-panel.component.scss']
})
export class MyPanelComponent implements OnInit {

  currentScheduling: any;
  isLoading: boolean = false;
  accessType: string;
  observation: string = ''

  constructor(
    private assistantService: AssistantService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private matDialog: MatDialog,
    private checkinService: CheckinService,
    private professionalService: ProfessionalService,
  ) { }

  ngOnInit(): void {
    this.accessType = this.authService.getAccessType()
    this.getCurrentScheduling()
  }

  getCurrentScheduling() {
    this.isLoading = true;
    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {
      this.professionalService.getCurrentScheduling()
        .subscribe(res => {
          this.currentScheduling = res
          this.isLoading = false;
        }, error => {
          this.isLoading = false;
          console.log(error)
          this.openSnackBar(error.error.message, 'Ok', 5000)
        })
    } else if (this.authService.getAccessType() === 'ROLE_ASSISTENTE') {
      this.assistantService.getCurrentScheduling()
        .subscribe(res => {
          this.currentScheduling = res
          this.isLoading = false;
        }, error => {
          this.isLoading = false;
          console.log(error)
          this.openSnackBar(error.error.message, 'Ok', 5000)
        })
    }

  }
  goToCheckingAuth() {
    const dialogRef = this.matDialog.open(CheckingCheckoutProfessionalAuthComponent, {
      width: '480px',
      maxWidth: '100%',
      data: { currentScheduling: this.currentScheduling, status: 'checkin' }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getCurrentScheduling()
    });
  }
  increaseSchedulingPeriod() {
    const dialogRef = this.matDialog.open(IncreaseSchedulingPeriodModalComponent, {
      width: '480px',
      maxWidth: '100%',
      data: { currentScheduling: this.currentScheduling }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getCurrentScheduling()
    });
  }
  goToCheckoutAuth() {
    const dialogRef = this.matDialog.open(CheckingCheckoutProfessionalAuthComponent, {
      width: '480px',
      maxWidth: '100%',
      data: { currentScheduling: this.currentScheduling, status: 'checkout' }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getCurrentScheduling()
    });
  }

  submitChecking() {
    this.isLoading = true;
    const checking: any = {
      agendamentoId: this.currentScheduling.id,
      userId: parseInt(this.authService.getUserId()),
      observacao: this.observation
    }
    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {
      this.professionalService.submitProfessionalCheckin(checking).subscribe(res => {
        this.getCurrentScheduling()
        this.openSnackBar('Check-in realizado com sucesso!', 'Ok', 3000)
      }, error => {
        console.log(error)
        this.openSnackBar(error.error.message, 'Ok', 5000)
        this.isLoading = false;
      })
    } else if (this.authService.getAccessType() === 'ROLE_ASSISTENTE') {
      this.checkinService.submitAssistantCheckin(checking).subscribe(res => {
        this.getCurrentScheduling()
        this.openSnackBar('Check-in realizado com sucesso!', 'Ok', 3000)
      }, error => {
        console.log(error)
        this.openSnackBar(error.error.message, 'Ok', 5000)
        this.isLoading = false;
      })
    } else {
      this.isLoading = false;
    }
  }
  submitCheckout() {
    const checkout: any = {
      agendamentoId: this.currentScheduling.id,
      userId: parseInt(this.authService.getUserId())
    }

    if (this.authService.getAccessType() === 'ROLE_PROFISSIONAL') {
      this.professionalService.submitProfessionalCheckout(checkout).subscribe(res => {
        this.openSnackBar('Check-out realizado com sucesso!', 'Ok', 3000)
        this.getCurrentScheduling()
      }, error => {
        console.log(error)
        this.openSnackBar(error.error.message, 'Ok', 5000)
      })
    } else if (this.authService.getAccessType() === 'ROLE_ASSISTENTE') {
      this.checkinService.submitAssistantCheckout(checkout).subscribe(res => {
        this.openSnackBar('Check-out realizado com sucesso!', 'Ok', 3000)
        this.getCurrentScheduling()
      }, error => {
        console.log(error)
        this.openSnackBar(error.error.message, 'Ok', 5000)
      })
    }
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
