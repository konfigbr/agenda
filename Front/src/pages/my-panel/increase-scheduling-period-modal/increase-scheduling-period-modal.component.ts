import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { SchedulingService } from 'src/services/scheduling.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import MyErrorStateMatcher from 'src/config/error-state-matcher';

@Component({
  selector: 'app-increase-scheduling-period-modal',
  templateUrl: './increase-scheduling-period-modal.component.html',
  styleUrls: ['./increase-scheduling-period-modal.component.scss']
})
export class IncreaseSchedulingPeriodModalComponent implements OnInit {

  isLoading: boolean = false
  increaseSchedulingPeriodFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();

  increaseSchedulingPeriod = {
    agendamento: null,
    assistente: null,
    horas: 1
  }

  constructor(
    public dialogRef: MatDialogRef<IncreaseSchedulingPeriodModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private schedulingService: SchedulingService
  ) {
    this.increaseSchedulingPeriodFormGroup = this.formBuilder.group({
      hours: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    this.increaseSchedulingPeriod = {
      agendamento: this.data.currentScheduling.id,
      assistente: this.data.currentScheduling.assistente.id,
      horas: 1
    }
  }

  formIncreaseSchedulingPeriodHasError(control: any, error: string) {
    return this.increaseSchedulingPeriodFormGroup.controls[control].hasError(error);
  }
  onSubmit() {
    if (this.increaseSchedulingPeriodFormGroup.valid) {
      this.isLoading = true;
      this.schedulingService.increaseSchedulingPeriod(this.increaseSchedulingPeriod).subscribe(res => {
        this.openSnackBar('Período de agendamento aumentado com sucesso', 'Ok', 3000)
        this.dialogRef.close()
        this.isLoading = false;
      }, error => {
        console.log(error)
        this.openSnackBar(error.error.message, 'Ok', 5000)
        this.dialogRef.close()
        this.isLoading = false;
      })
    }

  }
  cancel() {
    this.dialogRef.close()
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
}
