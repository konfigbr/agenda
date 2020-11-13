import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-show-images-modal',
  templateUrl: './show-images-modal.component.html',
  styleUrls: ['./show-images-modal.component.scss']
})
export class ShowImagesModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ShowImagesModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit(): void { }

  close() {
    this.dialogRef.close();
  }

}
