import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent implements OnInit {

  fileRG: any;
  fileConstituicao: any;
  fileCNPJ: any;

  constructor() { }

  ngOnInit(): void {
  }

  setFile(type: string, event: any) {
    const file = event.target.files[0]

    if (type === 'RG') 
      this.fileRG = file
    else if (type === 'CNPJ')
      this.fileCNPJ = file
    else if (type === 'CONSTITUICAO')
      this.fileConstituicao = file
  }
}
