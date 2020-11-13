import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CartService } from 'src/services/cart.service';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MAT_MOMENT_DATE_FORMATS } from '@angular/material-moment-adapter';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { SchedulingService } from 'src/services/scheduling.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { addHours } from 'date-fns';
import * as moment from 'moment';
@Component({
  selector: 'app-equipment-modal',
  templateUrl: './equipment-modal.component.html',
  styleUrls: ['./equipment-modal.component.scss'],
  providers: [
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
  ]
})
export class EquipmentModalComponent implements OnInit {
  cardMaxLength: any;
  equipmentAddFormGroup: FormGroup;
  equipmentTypesAvailable: any[] = []
  equipmentAdd: any = {};
  isLoading: boolean = false;
  matcher = new MyErrorStateMatcher();
  equipmentsAdded: any[] = [];
  equipmentsAddedToCancel: any[] = [];
  isRemove: boolean = false
  justCancel: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<EquipmentModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private cartService: CartService,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private schedulingService: SchedulingService
  ) {
    this.equipmentAddFormGroup = this.formBuilder.group({
      equipmentType: new FormControl('', [Validators.nullValidator]),
      start: new FormControl('', [Validators.nullValidator]),
      end: new FormControl('', [Validators.nullValidator]),
    })
  }

  ngOnInit(): void {
    this.justCancel = this.data.justCancel
    this.horarios = new Map<number, any[]>()
    this.inicio = new Date(this.data.scheduling.inicio)
    if (this.data.scheduling.status === 'CONFIRMADO') {
      this.isRemove = true
      this.equipmentsAddedToCancel = this.data.scheduling.tiposEquipamentos
    }
    this.getEquipmentTypesAvailable()
  }
  cancelEquipment(id: any) {
    const cancelEquipment: any = {
      agendamento: this.data.scheduling.id,
      tiposEquipamentos: [id]
    }
    this.schedulingService.cancelEquipamento(cancelEquipment).subscribe(res => {
      this.openSnackBar('Equipamento cancelado com sucesso!', 'Ok', 3000)
      this.dialogRef.close('equipCancel')
    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000)
      this.dialogRef.close(false)
    })
  }
  keyItem() {
    if (!this.equipmentAdd.equipmentType) {
      return
    }
    return this.equipmentAdd.equipmentType.id;
  }

  formRoomHasError(control: any, error: string) {
    return this.equipmentAddFormGroup.controls[control].hasError(error);
  }
  removeEquipment(id) {
    this.equipmentsAdded.splice(this.equipmentsAdded.indexOf(this.equipmentsAdded.find(equipment => equipment.equipmentType.id === id)), 1)

  }
  // tslint:disable-next-line:member-ordering
  horarios: Map<number, any[]>
  // tslint:disable-next-line:member-ordering
  inicio: any;

  getEquipmentTypesAvailable() {
    this.isLoading = true;

    const params: any = {
      inicio: this.data.scheduling.inicio,
      fim: this.data.scheduling.fim,
    }
    this.cartService.getEquipmentTypesAvailable(params).subscribe(res => {
      this.equipmentTypesAvailable = res

      this.equipmentTypesAvailable.forEach(te => {
        this.horarios.set(te.id, [])
        let ini = moment(te.horarios[0], 'HH:mm').toDate()
        ini.setFullYear(this.inicio.getFullYear(), this.inicio.getMonth(), this.inicio.getDate())
        const fim = moment(te.horarios[1], 'HH:mm').toDate()
        fim.setFullYear(this.inicio.getFullYear(), this.inicio.getMonth(), this.inicio.getDate())

        while (ini < fim) {
          this.horarios.get(te.id).push(ini)
          ini = addHours(ini, 1)
        }
        this.horarios.get(te.id).push(fim)

      })

      this.isLoading = false;
    }, error => {
      console.log(error)
      this.isLoading = false;
    })
  }
  getHorarios(index: number): any[] {
    if (!index) {
      return
    }
    return this.horarios.get(index).slice(0, -1)
  }

  getHorariosFinais(index: number): any[] {
    if (!index) {
      return
    }
    const horaInicialIndex = this.horarios.get(index).findIndex(h => h === this.equipmentAdd.start)
    return this.horarios.get(index).slice(horaInicialIndex + 1)
  }

  onSubmit() {
    this.isLoading = true;
    setTimeout(() => {
      const equipmentAdd = {
        ...this.equipmentAdd,
        start: (this.equipmentAdd.start.getHours() < 10 ? '0' + this.equipmentAdd.start.getHours() : this.equipmentAdd.start.getHours()) + ':00',
        end: (this.equipmentAdd.end.getHours() < 10 ? '0' + this.equipmentAdd.end.getHours() : this.equipmentAdd.end.getHours()) + ':00'
      }
      this.equipmentsAdded.push(equipmentAdd);
      this.equipmentAdd = {}
      setTimeout(() => {
        this.isLoading = false;
      }, 300)
    }, 300)
  }

  cancel() {
    this.dialogRef.close(false)
  }

  save() {
    const data: any = {
      sala: this.data.scheduling.sala.id,
      medico: this.data.scheduling.user.id,
      tiposEquipamentos: this.equipmentsAdded.map(equipment => (
        {
          tipoEquipamento: equipment.equipmentType.id,
          inicio: new Date(this.data.scheduling.inicio.split('T')[0] + 'T' + equipment.start + ':00'),
          fim: new Date(this.data.scheduling.fim.split('T')[0] + 'T' + equipment.end + ':00')
        }
      ))
    }
    const tipoEquipamentos: any[] = data.tiposEquipamentos
    data.tiposEquipamentos = []

    tipoEquipamentos.forEach(h => {
      let horaInicio = new Date(h.inicio.getTime())
      const horaFim = new Date(h.fim.getTime())

      while (horaInicio < horaFim) {
        data.tiposEquipamentos.push({
          tipoEquipamento: h.tipoEquipamento,
          inicio: this.toUtcDate(horaInicio).toISOString().split('.')[0],
          fim: this.toUtcDate(addHours(horaInicio, 1)).toISOString().split('.')[0],
        })
        horaInicio = addHours(horaInicio, 1)
      }
    })
    this.schedulingService.addEquipments(data).subscribe(res => {
      this.dialogRef.close(res);
    }, error => {
      this.openSnackBar(error.error.message || 'Erro ao adicionar equipamento!', 'Ok', 5000);
      this.dialogRef.close(error);
      console.log(error)
    })
  }
  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }
  private toUtcDate(date: Date): Date {
    const dateUTC = new Date(date.getTime())
    const timezoneDiff = new Date(date.getTime()).getTimezoneOffset()
    dateUTC.setUTCMinutes(dateUTC.getUTCMinutes() - timezoneDiff)
    return dateUTC
  }
}
