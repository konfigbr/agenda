import { Component, OnInit, Inject } from '@angular/core';
import { AssistantService } from 'src/services/assistant.service';
import Assistant from 'src/models/assistant.model';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { startOfWeek, endOfISOWeek, endOfWeek } from 'date-fns';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageModalComponent } from 'src/components/message-modal/message-modal.component';
import { MessageProfessionalAproveComponent } from 'src/components/message-professional-aprove/message-professional-aprove.component';
import { SchedulingService } from 'src/services/scheduling.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-change-assistant-scheduling-dialog',
  templateUrl: './change-assistant-scheduling-dialog.component.html',
  styleUrls: ['./change-assistant-scheduling-dialog.component.scss']
})
export class ChangeAssistantSchedulingDialogComponent implements OnInit {

  assistants: Assistant[] = [];
  isLoading: boolean = false;
  isHidden: boolean = false;
  assistantSelected: Assistant;
  isSchedule: boolean = false
  increaseSchedulingPeriod = {
    sala: null,
    inicio: null,
    fim: null,
    periodoAgendamento :null,
  }
  constructor(
    public dialog: MatDialog,
    private router: Router, 
    private assistantService: AssistantService,
    private scheduleService: SchedulingService,
    public dialogRef: MatDialogRef<ChangeAssistantSchedulingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.getAssistants();
    this.increaseSchedulingPeriod = {
      sala: this.data.room.id,
      inicio: this.data.start,
      fim: this.data.end,
      periodoAgendamento: new Date(this.data.end).getHours() - new Date(this.data.start).getHours()
    }  
   
  }

  scheduleAssistant(): void {
    this.isSchedule = true
    this.isHidden = true
  }

  goBack(): void{
    this.isSchedule = false
    this.isHidden = false
  }

  scheduleMaintenance(): void {
    const dialogRef = this.dialog.open(MessageProfessionalAproveComponent, {
      width: '400px',
      data: { message: `Deseja mesmo criar um agendamento de manutenção neste horario?`,title:`Atenção`, confirmLabel:`Agendar` }
    });
    
    dialogRef.afterClosed().subscribe(result => {
       result && this.submitMaintenance()
    });
  }

  submitMaintenance():void{
    this.isLoading = true
    this.scheduleService.schedulingMaintenance(
      this.increaseSchedulingPeriod
    ).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Manutenção criada com sucesso!', 'Ok', 3000);     
      
    //  this.router.navigate(["/agendamento-de-salas"], { replaceUrl: true })
      this.close()
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }

  getAssistants() {
    this.isLoading = true;
    this.assistantService.getAssistants().subscribe((assistants: Assistant[]) => {
      this.assistants = assistants;
      this.isLoading = false;
      this.assistantSelected = assistants.find(
        assistant => assistant.id === (this.data.assistant && this.data.assistant.id)
      ) || assistants[0];

    }, error => {
      console.log(error)
      this.isLoading = false;
    })
  }

  close() {
    this.dialogRef.close(true);
  }

  submitAssistantSelected() {
    (this.data.scheduling && this.data.scheduling.id) ? this.changeAssistant() : this.changeRoomAssistant()
  }
  changeAssistant() {
    const changeAssistant = {
      assistente: { id: this.assistantSelected.id },
      agendamento: { id: this.data.scheduling.id }
    }
    this.assistantService.changeAssistant(changeAssistant).subscribe(res => {
      this.openSnackBar('Assistente alterado(a) com sucesso!', 'Ok', 3000)
      this.dialogRef.close(true);
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000)
      console.log(error)
      this.dialogRef.close(false);
    })
  }
  changeRoomAssistant() {
    const changeRoomAssistant = {
      assistente: { id: this.assistantSelected.id },
      sala: { id: this.data.room.id },
      inicio: this.data.start,
      fim: this.data.end,
    }
    this.assistantService.changeRoomAssistant(changeRoomAssistant).subscribe(res => {
      this.openSnackBar('Assistente agendado(a) com sucesso!', 'Ok', 3000)
      this.dialogRef.close(true);
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000)
      console.log(error)
      this.dialogRef.close(false);
    })
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

}
