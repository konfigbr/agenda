import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-user-terms-modal',
  templateUrl: './user-terms-modal.component.html',
  styleUrls: ['./user-terms-modal.component.scss']
})
export class UserTermsModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<UserTermsModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit(): void {
    console.log(this.data)
  }

}
