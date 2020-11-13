import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-message-professional-aprove',
  templateUrl: './message-professional-aprove.component.html',
  styleUrls: ['./message-professional-aprove.component.scss']
})
export class MessageProfessionalAproveComponent implements OnInit {
  constructor(
    public dialogRef: MatDialogRef<MessageProfessionalAproveComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit(): void { }

  close(confirm: boolean) {
    this.dialogRef.close(confirm);
  }

}
