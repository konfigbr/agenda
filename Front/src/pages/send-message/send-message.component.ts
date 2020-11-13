import { Component, OnInit } from '@angular/core';
import { ProfessionalService } from 'src/services/professional.service';
import Professional from 'src/models/professional.model';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import MyErrorStateMatcher from 'src/config/error-state-matcher';

@Component({
  selector: 'app-send-message',
  templateUrl: './send-message.component.html',
  styleUrls: ['./send-message.component.scss']
})
export class SendMessageComponent implements OnInit {
  isLoading: boolean = false
  professionals: Professional[] = [];
  selectedProfessional: Professional;
  sendMessageFormGroup: FormGroup
  sendMessage: any = {};
  matcher = new MyErrorStateMatcher();

  constructor(
    private professionalService: ProfessionalService,
    private formBuilder: FormBuilder
  ) {
    this.sendMessageFormGroup = this.formBuilder.group({
      professional: new FormControl('', [Validators.required]),
      message: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit(): void {
    this.getProfessionals()
  }
  getProfessionals() {
    this.isLoading = true;
    this.professionalService.getProfessionals().subscribe(professionals => {
      this.professionals = professionals;
      this.selectedProfessional = professionals[0] && professionals[0]
      this.isLoading = false;
    }, error => {
      console.log(error)
      this.isLoading = false;
    })
  }

  sendMessageHasError(control: any, error: string) {
    return this.sendMessageFormGroup.controls[control].hasError(error);
  }
  onSubmit() {

  }
}
