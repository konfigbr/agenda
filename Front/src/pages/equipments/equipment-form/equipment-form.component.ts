import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { CurrencyMaskConfig, CURRENCY_MASK_CONFIG } from 'ng2-currency-mask';
import { Observable } from 'rxjs';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import EquipmentType from 'src/models/equipment/equipment-type.model';
import Equipment from 'src/models/equipment/equipment.model';
import { EquipmentService } from 'src/services/equipment.service';
import { UtilsService } from 'src/services/utils.service';
import { selectEquipment } from 'src/store/equipment/selectors';

export const CustomCurrencyMaskConfig: CurrencyMaskConfig = {
  align: "right",
  allowNegative: true,
  decimal: ",",
  precision: 2,
  prefix: "R$ ",
  suffix: "",
  thousands: "."
};
@Component({
  selector: 'app-equipment-form',
  templateUrl: './equipment-form.component.html',
  styleUrls: ['./equipment-form.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
    { provide: CURRENCY_MASK_CONFIG, useValue: CustomCurrencyMaskConfig }
  ]
})
export class EquipmentFormComponent implements OnInit {

  equipment$: Observable<Equipment> = this.store.select(selectEquipment)
  equipment: Equipment = new Equipment();

  equipmentFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;
  equipmentTypes: EquipmentType[] = []
  billable = false
  teste = 0
  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private equipmentService: EquipmentService,
    private snackBar: MatSnackBar,
    private store: Store,
    private router: Router,
    private adapter: DateAdapter<any>,
    public utilsService: UtilsService
  ) {
    this.adapter.setLocale('pt-br');

    this.equipmentFormGroup = this.formBuilder.group({
      entryDate: new FormControl(new Date(), [Validators.required]),
      name: new FormControl('', [Validators.required]),
      type: new FormControl('', [Validators.required]),
      cost: new FormControl('', [Validators.required]),
      billable: new FormControl('', [Validators.nullValidator]),
      residualValue: new FormControl('', [Validators.required]),
      depreciation: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    this.getEquipmentTypes();
  }

  getEquipment(id: string | number) {
    this.equipmentService.getEquipmentById(id).subscribe((equipment: Equipment) => {
      this.equipment = equipment;
      this.equipment.tipoEquipamento = this.equipmentTypes.find(equipmentType => equipmentType.id === equipment.tipoEquipamento.id)
      this.isLoading = false;
    }, error => {
      console.log(error)
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }
  toFloat(element) {
    return parseFloat(element);
  }
  formEquipmentHasError(control: any, error: string) {
    return this.equipmentFormGroup.controls[control].hasError(error);
  }
  getEquipmentTypes() {
    this.isLoading = true
    this.equipmentService.getEquipmentTypes().subscribe((equipmentTypes: EquipmentType[]) => {
      this.equipmentTypes = equipmentTypes;
      this.route.params.subscribe(params => {
        if (params.id === 'new') {
          this.isLoading = false
          this.isEdit = false
        } else {
          params.id && (this.isEdit = true)
          params.id && this.getEquipment(params.id)
        }
      })
    }, error => {
      this.isLoading = false
      this.openSnackBar(error.error.message, 'Ok', 5000);
      console.log(error)
    })
  }
  onSubmit() {
    if (this.equipmentFormGroup.valid) {
      const equipment: Equipment = {
        cobravel: JSON.parse(this.equipment.cobravel as any),
        custo: parseFloat(this.equipment.custo as any),
        dataEntrada: this.equipment.dataEntrada,
        depreciacao: this.equipment.depreciacao,
        nome: this.equipment.nome,
        tipoEquipamento: this.equipment.tipoEquipamento,
        valorResidual: parseFloat(this.equipment.valorResidual as any),
      }
      this.isEdit ? this.updateEquipment({ ...equipment, id: this.equipment.id }) : this.createEquipment(equipment);

    }
    // if (this.equipmentFormGroup.valid) {
    //   this.isLoading = true;
    //   let equipmentType: EquipmentType;
    //   this.equipment$.subscribe(equipType => equipmentType = equipType)
    //   this.isEdit ? this.updateEquipmentType(equipmentType) : this.createEquipmentType(equipmentType);
    // }

  }
  createEquipment(equipment: Equipment) {
    this.equipmentService.createEquipment(equipment).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Equipamento criado com sucesso!', 'Ok', 3000);
      this.router.navigate(["equipamentos"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }
  updateEquipment(equipment: Equipment) {
    this.equipmentService.updateEquipment(equipment.id, equipment).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Equipamento atualizado com sucesso!', 'Ok', 3000);
      this.router.navigate(["equipamentos"], { replaceUrl: true })
    }, error => {
      this.isLoading = false;
      this.openSnackBar(error.error.message, 'Ok', 5000);
      console.log(error)
    })
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
      panelClass: 'teste'
    });
  }
}
