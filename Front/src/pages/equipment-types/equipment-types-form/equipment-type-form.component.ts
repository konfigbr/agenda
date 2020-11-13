import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import EquipmentType from 'src/models/equipment/equipment-type.model';
import { EquipmentService } from 'src/services/equipment.service';
import { SetEquipmentType } from 'src/store/equipment/actions';
import { selectEquipmentType } from 'src/store/equipment/selectors';
import { CURRENCY_MASK_CONFIG } from 'ng2-currency-mask';
import { CustomCurrencyMaskConfig } from 'src/pages/equipments/equipment-form/equipment-form.component';

@Component({
  selector: 'app-equipment-type-form',
  templateUrl: './equipment-type-form.component.html',
  styleUrls: ['./equipment-type-form.component.scss'],
  providers: [
    { provide: CURRENCY_MASK_CONFIG, useValue: CustomCurrencyMaskConfig }
  ]
})
export class EquipmentTypeFormComponent implements OnInit {
  // equipmentType$: Observable<EquipmentType> = this.store.select(selectEquipmentType)
  equipmentType: EquipmentType = new EquipmentType();
  equipmentTypeFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private equipmentService: EquipmentService,
    private snackBar: MatSnackBar,
    private store: Store,
    private router: Router
  ) {
    this.equipmentTypeFormGroup = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      chargeableAmount: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {

    this.route.params.subscribe(params => {
      if (params.id === 'new') {
        this.isEdit = false
      } else {
        params.id && (this.isEdit = true)
        params.id && this.getEquipmentType(params.id)
      }
    })
  }

  getEquipmentType(id: string | number) {
    this.equipmentService.getEquipmentTypeById(id).subscribe((equipmentType: EquipmentType) => {
      this.equipmentType = equipmentType
    })
  }

  formEquipmentTypeHasError(control: any, error: string) {
    return this.equipmentTypeFormGroup.controls[control].hasError(error);
  }
  onSubmit() {
    if (this.equipmentTypeFormGroup.valid) {
      this.isLoading = true;
      this.isEdit ? this.updateEquipmentType() : this.createEquipmentType();
    }

  }
  createEquipmentType() {
    this.equipmentService.createEquipmentType(this.equipmentType).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Tipo de equipamento criado com sucesso!', 'Ok', 3000);
      this.router.navigate(["tipos-de-equipamentos"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }
  updateEquipmentType() {
    this.equipmentService.updateEquipmentType(this.equipmentType.id, this.equipmentType).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Tipo de equipamento atualizado com sucesso!', 'Ok', 3000);
      this.router.navigate(["tipos-de-equipamentos"], { replaceUrl: true })
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
