import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/services/auth.service';
import { AssistantService } from 'src/services/assistant.service';
import { ProfessionalService } from 'src/services/professional.service';
import { UserService } from 'src/services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DataService } from 'src/services/data.service';
import { PatientService } from 'src/services/patient.service';
import { ConsultaService } from 'src/services/consulta.service';
import { PabxService } from 'src/services/pabx.service';
import { Subscription } from 'rxjs';
import { Call } from 'src/models/call-.model';
import * as uuid from 'uuid';
import { MatDialog } from '@angular/material/dialog';
import { CallsComponent } from 'src/pages/calls/calls.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {

  userName: string;
  badgeCount: number = 0
  badgeCount1: number = 0
  callsSize: number = 0

  private sseStream: Subscription;

  constructor(
    private router: Router,
    private authService: AuthService,
    private assistantService: AssistantService,
    private snackBar: MatSnackBar,
    private professionalService: ProfessionalService,
    private consultaService: ConsultaService,
    private patientService: PatientService,
    public dataService: DataService,
    private userService: UserService,
    private pabxService: PabxService,
    private matDialog: MatDialog
  ) { }

  ngOnInit(): void {
    if (this.authService.getAccessType() === 'ROLE_ATENDENTE') {
      this.sseStream = this.pabxService.incomingCall()
        .subscribe((call: any) => {
          if (call.url) {
            this.pabxService.setCall = {
              uuid: uuid.v4(),
              professionalKey: call.url,
              professionalObj: JSON.stringify(call.medico),
              phone: call.phone,
              extensionPhone: call.extensionPhone,
              dateTime: call.dateTime
            } as Call;
          } else {
            localStorage.removeItem('professionalKey');
            localStorage.removeItem('professionalObj');
          }
        });
    }

    this.getProfile();
    if (this.authService.getAccessType() === 'ROLE_ADMINISTRADOR') {
      this.getProfessionals()
    }
    this.getCalls();
  }

  getCalls() {
    this.pabxService.getCalls.subscribe((calls: Map<String, Call>) => {
      this.callsSize = calls.size;
    });
  }

  calls() {
    this.matDialog.open(CallsComponent, {
      width: '70%',
      maxWidth: '100%',
      maxHeight: '85%',
      data: {
      }
    }).afterClosed().subscribe();
  }

  ngOnDestroy() {
    if (this.sseStream)
      this.sseStream.unsubscribe();
  }

  showMenu() {
    document.getElementById('opt-side-menu').classList.remove('opt-side-menu-close');
    document.getElementById('opt-backdrop-menu').classList.add('opt-backdrop-menu-active');
    document.getElementById('opt-side-menu').classList.add('opt-side-menu-active');
    document.getElementById('opt-toggle-menu').classList.add('opt-toggle-menu-active');
    document.getElementById('opt-fab-btn-add') !== null && document.getElementById('opt-fab-btn-add').classList.add('opt-fab-btn-add-disabled')
  }
  closeMenu() {
    document.getElementById('opt-side-menu').classList.add('opt-side-menu-close');
    document.getElementById('opt-backdrop-menu').classList.remove('opt-backdrop-menu-active');
    document.getElementById('opt-toggle-menu').classList.remove('opt-toggle-menu-active');
    document.getElementById('opt-fab-btn-add') !== null && document.getElementById('opt-fab-btn-add').classList.remove('opt-fab-btn-add-disabled')

    setTimeout(() => {
      document.getElementById('opt-side-menu').classList.remove('opt-side-menu-active');
    }, 300)
  }
  activeRoute(name: string) {
    return this.router.url === name;
  }
  logout() {
    this.authService.logout();
  }
  getProfessionals() {

    this.professionalService.getProfessionals().subscribe((professionals: any[]) => {

      this.dataService.badgeCount = professionals.filter(professional => professional.status === 'AGUARDANDO_APROVAÇÃO').length

      setTimeout(() => {
        this.getProfessionals()
      }, 90000)
    }, error => {
      console.log(error)
    })
  }
  getConsultasProfessional() {

    this.consultaService.getStatusRequestedProfessional().subscribe((consultas: any[]) => {
      this.dataService.badgeCount1 = consultas.length
    }, error => {
      console.log(error)
    })
  }
  getConsultas() {
    this.consultaService.getStatusRequested().subscribe((consultas: any[]) => {
      this.dataService.badgeCount1 = consultas.length

      setTimeout(() => {
        this.getConsultas()
      }, 90000)
    }, error => {
      console.log(error)
    })
  }

  getAccessType() {
    switch (this.authService.getAccessType()) {
      case 'ROLE_ADMINISTRADOR':
        return 'administrador'
      case 'ROLE_ASSISTENTE':
        return 'assistente'
      case 'ROLE_PROFISSIONAL':
        return 'profissional'
      case 'ROLE_ATENDENTE':
        return 'atendente'
      case 'ROLE_PACIENTE':
        return 'paciente'
      default:
        break;
    }
  }
  getProfile() {
    switch (this.authService.getAccessType()) {
      case 'ROLE_ADMINISTRADOR':
        this.getAdministrativeUserById(this.authService.getUserId())
        break;
      case 'ROLE_ASSISTENTE':
        this.getAssistantById()
        break;
      case 'ROLE_PROFISSIONAL':
        this.getProfessionalById()
        break;
      case 'ROLE_ATENDENTE':
        this.getAdministrativeUserById(this.authService.getUserId())
        break;
      case 'ROLE_PACIENTE':
        this.getPatienteById()
        break;
      default:
        break;
    }
  }
  getProfessionalById() {
    this.professionalService.getMe().subscribe(res => {
      this.userName = res.nome
      this.getConsultasProfessional()
    }, error => {
      console.log(error);
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }
  getAssistantById() {
    this.assistantService.getMe().subscribe(res => {
      this.userName = res.nome
    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }
  getAdministrativeUserById(administrativeUserId: string | number) {
    this.userService.getAdministrativeUserById(administrativeUserId).subscribe(res => {
      this.userName = res.nome
      this.getConsultas()
    }, error => {
      console.log(error)
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }
  getPatienteById() {
    this.patientService.getMe().subscribe(res => {
      this.userName = res.nome
    }, error => {
      console.log(error);
      this.openSnackBar(error.error.message, 'Ok', 5000);
    })
  }

  openSnackBar(message: string, action: string, timing: number) {
    this.snackBar.open(message, action, {
      duration: timing,
    });
  }

  navigate(route: String) {
    this.router.navigate([`${this.getProfessionalKey()}/${route}`], { replaceUrl: true })
  }

  getProfessionalKey() {
    return localStorage.getItem("professionalKey")
  }

  getProfessionalObj() {
    return JSON.parse(localStorage.getItem("professionalObj"))
  }

  logoutPatient() {
    const professionalKey = this.getProfessionalKey()
    this.authService.logoutPatient(professionalKey);
  }

}
