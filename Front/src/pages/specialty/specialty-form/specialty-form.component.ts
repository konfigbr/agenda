import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import MyErrorStateMatcher from 'src/config/error-state-matcher';
import { ActivatedRoute, Router } from '@angular/router';
import { SpecialtyService } from 'src/services/specialty.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import Specialty from 'src/models/specialty.model';
import Price from 'src/models/price.model';
@Component({
  selector: 'app-specialty-form',
  templateUrl: './specialty-form.component.html',
  styleUrls: ['./specialty-form.component.scss']
})
export class SpecialtyFormComponent implements OnInit {

  categoryPricesList:any=[];
  table: any = {}
  specialty: Specialty = new Specialty();
  specialtyFormGroup: FormGroup
  matcher = new MyErrorStateMatcher();
  isLoading: boolean = false;
  isEdit: boolean = false;
  showInProfessionalRegister = false;

  tipoAceite: any[] =
    [ { key: "MED", value: "MEDICINA" }
    , { key: "ODONTO", value: "ODONTOLOGIA" }
    ,{ key: "FONO", value: "FONOAUDIOLOGIA" }

    ];

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private specialtyService: SpecialtyService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.specialtyFormGroup = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      prices: new FormControl('', [Validators.nullValidator]),
      aceite: new FormControl( [Validators.required]),
      showInProfessionalRegister: new FormControl('', [Validators.nullValidator])
    })
  }

  ngOnInit(): void {
  
    this.getPriceTables()
  }

  getCategoryPricesList(){    
    this.route.params.subscribe(params => {
      if (params.id === 'new') {
        this.isEdit = false
      } else {
        params.id && (this.isEdit = true)
        params.id && this.getSpecialty(params.id)
      }
    })
   
  }

  getPriceTables() {
    this.isLoading = true;
    this.specialtyService.getPriceTables().subscribe((price: Price[]) => {
      this.categoryPricesList = price      
      this.getCategoryPricesList()      
      this.isLoading = false;
    },error => {
      this.isLoading = false;
      console.log(error)
      this.openSnackBar(error.error.message,"Ok",5000)
    })

  }

  getSpecialty(id: string | number) {
    this.specialtyService.getSpecialtyById(id).subscribe((response:any) => {
      this.specialty = response
      this.specialty.tabelaPreco = this.categoryPricesList.find(table => this.specialty.tabelaPreco.id === table.id)
      
    },error => {
      console.log(error)
      this.openSnackBar(error.error.message,"Ok",5000)
    })
  }

  formSpecialtyHasError(control: any, error: string) {
    return this.specialtyFormGroup.controls[control].hasError(error);
  }
  onSubmit() {
    if (this.specialtyFormGroup.valid) {
      this.isLoading = true;
      this.isEdit ? this.updateSpecialty() : this.createSpecialty();
    }

  }
  createSpecialty() {
    this.specialtyService.createSpecialty(this.specialty).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Especialidade criada com sucesso!', 'Ok', 3000);
      this.router.navigate(["especialidades"], { replaceUrl: true })
    }, error => {
      this.openSnackBar(error.error.message, 'Ok', 5000);
      this.isLoading = false;
      console.log(error)
    })
  }
  updateSpecialty() {
    this.specialtyService.updateSpecialty(this.specialty.id, this.specialty).subscribe(response => {
      this.isLoading = false;
      this.openSnackBar('Especialidade atualizada com sucesso!', 'Ok', 3000);
      this.router.navigate(["especialidades"], { replaceUrl: true })
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
