import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { PriceService } from 'src/services/price.service';
import { CURRENCY_MASK_CONFIG } from 'ng2-currency-mask';
import { CustomCurrencyMaskConfig } from 'src/pages/equipments/equipment-form/equipment-form.component';
import Price from 'src/models/price.model';
import PriceRange from 'src/models/price-range.model';

@Component({
  selector: 'app-prices-form',
  templateUrl: './prices-form.component.html',
  styleUrls: ['./prices-form.component.scss'],
  providers: [
    { provide: CURRENCY_MASK_CONFIG, useValue: CustomCurrencyMaskConfig }
  ]
})
export class PricesFormComponent implements OnInit {

  price: Price = new Price();
  priceFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;

  hours: {rowNumber: number, hours: number[]}[]

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private priceService: PriceService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.hours = this.getHoursArray()
    this.initForm()
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params.id === 'new') {
        this.isEdit = false
      } else {
        params.id && (this.isEdit = true)
        params.id && this.getPriceById(params.id)
      }
    })
  }

  getPriceById(id: string | number) {
    this.priceService.getPriceById(id).subscribe((price: Price) => {
      this.price = price
      this.loadForm(price)
    })
  }

  formPricesError(control: any, error: string) {
    return this.priceFormGroup.controls[control].hasError(error);
  }

  onSubmit() {
    if (this.priceFormGroup.valid) {
      this.isLoading = true;
      this.isEdit ? this.updatePrice() : this.createPrice();
    }
  }

  createPrice() {
    this.priceService.createPrice(this.getPriceModel()).subscribe(() => {
      this.isLoading = false;
      this.openSnackBar('Tabela de preços criada com sucesso!', 'Ok', 3000);
      this.router.navigate(["tabelas-de-precos"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }

  updatePrice() {
    this.priceService.updatePrice(this.price.id, this.getPriceModel()).subscribe(() => {
      this.isLoading = false;
      this.openSnackBar('Tabela de preços atualizada com sucesso!', 'Ok', 3000);
      this.router.navigate(["tabelas-de-precos"], { replaceUrl: true })
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

  private getPriceModel(): Price {
    const faixaPreco: PriceRange[] = []

    this.hours.forEach(hours => {
      hours.hours.forEach(hour => {
        faixaPreco.push({
          id: this.price.faixaPrecos.find(faixa => faixa.totalHoras === hour)?.id || null,
          version: this.price.faixaPrecos.find(faixa => faixa.totalHoras === hour)?.version || null,
          totalHoras: hour,
          valor: Number(this.priceFormGroup.get(`faixaPrecoHora_${hour}`).value),
        })
      })
    })

    return {
      id: this.price.id,
      nome: this.priceFormGroup.get('name').value,
      faixaPrecos: faixaPreco,
    }
  }

  private initForm(): void {
    this.priceFormGroup = this.formBuilder.group({
      name: new FormControl('', [Validators.required])
    })

    this.hours.forEach(value => {
      value.hours.forEach(hour => {
        this.priceFormGroup.addControl(`faixaPrecoHora_${hour}`, new FormControl(0, [Validators.required, Validators.min(0.01)]))
      })
    })
  }

  private loadForm(price: Price): void {
    this.priceFormGroup.get('name').setValue(price.nome)

    price.faixaPrecos.forEach(faixaPreco => {
      this.priceFormGroup.get(`faixaPrecoHora_${faixaPreco.totalHoras}`).setValue(faixaPreco.valor)
    })
  }

  private getHoursArray(): {rowNumber: number, hours: number[]}[] {
    const hoursArray: {rowNumber: number, hours: number[]}[] = []
    const rowsQty = 25
    let lastHour = 0;

    for (let i = 1; i <= rowsQty; i++) {
      if (i === 1) {
        hoursArray.push({
          rowNumber: 1,
          hours: [1, 2],
        })

        continue
      }

      hoursArray.push({
        rowNumber: i,
        hours: [lastHour + 4, lastHour + 8],
      })

      lastHour += 8
    }

    return hoursArray
  }
}
