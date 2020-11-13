import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from 'src/config/auth.guard';
import { LayoutComponent } from 'src/layout/layout.component';
import { AssistantScheduleComponent } from 'src/pages/assistant-schedule/assistant-schedule.component';
import { AssistantFormComponent } from 'src/pages/assistants/assistant-form/assistant-form.component';
import { AssistantsComponent } from 'src/pages/assistants/assistants.component';
import { AuthComponent } from 'src/pages/auth/auth.component';
import { AuthPatientComponent } from 'src/pages/auth-patient/auth-patient.component';
import { EquipmentTypeFormComponent } from 'src/pages/equipment-types/equipment-types-form/equipment-type-form.component';
import { EquipmentTypesComponent } from 'src/pages/equipment-types/equipment-types.component';
import { EquipmentFormComponent } from 'src/pages/equipments/equipment-form/equipment-form.component';
import { EquipmentsComponent } from 'src/pages/equipments/equipments.component';
import { ProfessionalFormComponent } from 'src/pages/professionals/professional-form/professional-form.component';
import { ProfessionalsComponent } from 'src/pages/professionals/professionals.component';
import { EmpresaProfissionaisComponent } from 'src/pages/empresa-profissionais/professionals.component';
import { ReportsComponent } from 'src/pages/reports/reports.component';
import { RoomsSchedulingComponent } from 'src/pages/rooms-scheduling/rooms-scheduling.component';
import { RoomFormComponent } from 'src/pages/rooms/room-form/room-form.component';
import { RoomsComponent } from 'src/pages/rooms/rooms.component';
import { ScheduleRoomByTheTeamComponent } from 'src/pages/schedule-room-by-the-team/schedule-room-by-the-team.component';
import { InvoicesComponent } from 'src/pages/invoices/invoices.component';
import { MyPanelComponent } from 'src/pages/my-panel/my-panel.component';
import { AssistantSchedulePanelComponent } from 'src/pages/assistant-schedule-panel/assistant-schedule-panel.component';
import { ProfessionalScheduleComponent } from 'src/pages/professional-schedule/professional-schedule.component';
import { AdministrativeUsersComponent } from 'src/pages/administrative-users/administrative-users.component';
import {
  AdministrativeUserFormComponent
} from 'src/pages/administrative-users/administrative-user-form/administrative-user-form.component';
import { SchedulingPanelComponent } from 'src/pages/scheduling-panel/scheduling-panel.component';
import { SendMessageComponent } from 'src/pages/send-message/send-message.component';
import { InvoiceDetailsComponent } from 'src/pages/invoices/invoice-details/invoice-details.component';
import { ProfessionalProfileComponent } from 'src/pages/professional-profile/professional-profile.component';
import { SpecialtyComponent } from 'src/pages/specialty/specialty.component';
import { SpecialtyFormComponent } from 'src/pages/specialty/specialty-form/specialty-form.component';
import { PricesComponent } from 'src/pages/prices/prices.component';
import { PricesFormComponent } from 'src/pages/prices/prices-form/prices-form.component';
import { ProfessionalRegisterComponent } from 'src/pages/professional-register/professional-register.component';
import { PatientComponent } from 'src/pages/patient/patient.component';
import { SchedulesofthedayComponent } from 'src/pages/schedulesoftheday/schedulesoftheday.component';
import { SchedulesofthedayMyPanelComponent } from 'src/pages/schedulesoftheday/schedulesoftheday-my-panel/schedulesoftheday-my-panel.component';
import { PatientProfileComponent } from 'src/pages/patient-profile/patient-profile.component';
import { PatientListComponent } from 'src/pages/patient-list/patient-list.component';
import { AuditComponent } from 'src/pages/audit/audit.component';
import { ScheduleAppointmentsComponent } from 'src/pages/schedule-appointments/schedule-appointments.component';
import { AppointmentAdministrativeComponent } from 'src/pages/appointment-administrative/appointment-administrative.component';
import { ReschedulingComponent } from 'src/pages/rescheduling/rescheduling.component';
import { CallsComponent } from 'src/pages/calls/calls.component';





const routes: Routes = [
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
  {
    path: 'auth/:page',
    component: AuthComponent
  },
  {
    path: '',
    redirectTo: 'auth/:professionalKey/login',
    pathMatch: 'full'
  },
  {
    path: 'auth/:professionalKey/:page',
    component: AuthPatientComponent
  },
  {
    path: ':professionalKey/paciente/cadastro',
    component: ProfessionalRegisterComponent,
  },
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'calls',
        component: CallsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ATENDENTE'] }
      },
      {
        path: ':professionalKey/minhas-consultas',
        component: PatientComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PACIENTE', 'ROLE_ATENDENTE'] }
      },
      {

        path: ':professionalKey/agendar-consultas',
        component: ScheduleAppointmentsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PACIENTE', 'ROLE_ATENDENTE'] }
      },
      {

        path: ':professionalKey/meu-perfil-paciente',
        component: PatientProfileComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PACIENTE'] }
      },
      {
        path: 'agendamento-de-salas',
        component: RoomsSchedulingComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'auditoria',
        component: AuditComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'salas',
        component: RoomsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'especialidades',
        component: SpecialtyComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'especialidade/:id',
        component: SpecialtyFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'tabelas-de-precos',
        component: PricesComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'tabela-de-precos/:id',
        component: PricesFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'sala/:id',
        component: RoomFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'equipamentos',
        component: EquipmentsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'equipamento/:id',
        component: EquipmentFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'tipos-de-equipamentos',
        component: EquipmentTypesComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'tipo-de-equipamento/:id',
        component: EquipmentTypeFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
              path: 'empresa/:id/profissionais',
              component: EmpresaProfissionaisComponent,
              canActivate: [AuthGuard],
              data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'profissionais',
        component: ProfessionalsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE'] }
      },
      {
        path: 'profissional/:id',
        component: ProfessionalFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE'] }
      },
      {
        path: 'assistentes',
        component: AssistantsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'assistente/:id',
        component: AssistantFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'agenda-de-assistentes',
        component: AssistantScheduleComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'relatorios',
        component: ReportsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'usuarios-administrativos',
        component: AdministrativeUsersComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'pacientes',
        component: PatientListComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'usuario-administrativo/:id',
        component: AdministrativeUserFormComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'agendar-salas',
        component: ScheduleRoomByTheTeamComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ATENDENTE', 'ROLE_PROFISSIONAL'] }
      },
      {
        path: 'faturas',
        component: InvoicesComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PROFISSIONAL'] }
      },
      {
        path: 'meu-painel',
        component: MyPanelComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ASSISTENTE', 'ROLE_PROFISSIONAL'] }
      },
      {
        path: 'minha-agenda',
        component: AssistantSchedulePanelComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ASSISTENTE'] }
      },
      {
        path: 'meus-agendamentos',
        component: ProfessionalScheduleComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PROFISSIONAL'] }
      },
      {
        path: 'painel-de-agendamentos',
        component: SchedulingPanelComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ATENDENTE'] }
      },
      {
        path: 'enviar-mensagem',
        component: SendMessageComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ATENDENTE'] }
      },
      {
        path: 'agendamentos-do-dia',
        component: SchedulesofthedayComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ATENDENTE'] }
      },
      {
        path: 'painel-agendamentos-do-dia/:id',
        component: SchedulesofthedayMyPanelComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ATENDENTE'] }
      },
      {
        path: 'fatura/:id',
        component: InvoiceDetailsComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PROFISSIONAL'] }
      },
      {
        path: 'meu-perfil',
        component: ProfessionalProfileComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PROFISSIONAL'] }
      },
      {
        path: 'consultas',
        component: AppointmentAdministrativeComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PROFISSIONAL', 'ROLE_ATENDENTE', 'ROLE_ADMINISTRADOR'] }
      },
      {
        path: 'agendamento-consulta/:id',
        component: ReschedulingComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_PROFISSIONAL', 'ROLE_ATENDENTE', 'ROLE_ADMINISTRADOR'] }
      },
      {
        path: ':idProfessionalUrl/agendamento-consulta',
        component: ReschedulingComponent,
        canActivate: [AuthGuard],
        data: { canAccess: ['ROLE_ATENDENTE', 'ROLE_ADMINISTRADOR'] }
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { anchorScrolling: 'enabled', useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
