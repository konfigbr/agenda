import { registerLocaleData } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS, HttpClientJsonpModule } from '@angular/common/http';
import localePt from '@angular/common/locales/pt';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { CurrencyMaskModule } from 'ng2-currency-mask';
import { NgxMaskModule } from 'ngx-mask';
import { MessageProfessionalAproveComponent } from 'src/components/message-professional-aprove/message-professional-aprove.component';
import { AuthInterceptorService } from 'src/config/auth-interceptor.service';
import { HeaderComponent } from 'src/layout/header/header.component';
import { LayoutComponent } from 'src/layout/layout.component';
import { AdministrativeUsersComponent } from 'src/pages/administrative-users/administrative-users.component';
import { AuthComponent } from 'src/pages/auth/auth.component';
import { EquipmentTypeFormComponent } from 'src/pages/equipment-types/equipment-types-form/equipment-type-form.component';
import { EquipmentTypesComponent } from 'src/pages/equipment-types/equipment-types.component';
import { AssistantService } from 'src/services/assistant.service';
import { AuthService } from 'src/services/auth.service';
import { CalendarViewService } from 'src/services/calendar-view.service';
import { DateService } from 'src/services/date.service';
import { EquipmentService } from 'src/services/equipment.service';
import { InvoicesService } from 'src/services/invoices.service';
import { LocalService } from 'src/services/local.service';
import { MessageService } from 'src/services/message.service';
import { ProfessionalService } from 'src/services/professional.service';
import { RoomService } from 'src/services/room.service';
import { SchedulingService } from 'src/services/scheduling.service';
import { SpecialtyService } from 'src/services/specialty.service';
import { UserService } from 'src/services/user.service';
import { UtilsService } from 'src/services/utils.service';
import { ValidatorsService } from 'src/services/validators.service';
import appReducers from 'src/store';
import { MessageModalComponent } from '../components/message-modal/message-modal.component';
import { ShowImagesModalComponent } from '../components/show-images-modal/show-images-modal.component';
import { AdministrativeUserFormComponent } from '../pages/administrative-users/administrative-user-form/administrative-user-form.component';
import { AssistantSchedulePanelComponent } from '../pages/assistant-schedule-panel/assistant-schedule-panel.component';
import { AssistantScheduleModalComponent } from '../pages/assistant-schedule/assistant-schedule-modal/assistant-schedule-modal.component';
import { AssistantScheduleComponent } from '../pages/assistant-schedule/assistant-schedule.component';
import { AssistantFormComponent } from '../pages/assistants/assistant-form/assistant-form.component';
import { AssistantsComponent } from '../pages/assistants/assistants.component';
import { EquipmentFormComponent } from '../pages/equipments/equipment-form/equipment-form.component';
import { EquipmentsComponent } from '../pages/equipments/equipments.component';
import { InvoicesComponent } from '../pages/invoices/invoices.component';
import { MyPanelComponent } from '../pages/my-panel/my-panel.component';
import { EmpresaProfissionaisComponent } from '../pages/empresa-profissionais/professionals.component';
import { ProfessionalScheduleComponent } from '../pages/professional-schedule/professional-schedule.component';
import { ProfessionalFormComponent } from '../pages/professionals/professional-form/professional-form.component';
import { ProfessionalsComponent } from '../pages/professionals/professionals.component';
import { ReportProfessionalsComponent } from '../pages/reports/report-professionals/report-professionals.component';
import { ReportSchedulingsComponent } from '../pages/reports/report-schedulings/report-schedulings.component';
import { ReportsComponent } from '../pages/reports/reports.component';
import { ChangeAssistantSchedulingDialogComponent } from '../pages/rooms-scheduling/change-assistant-scheduling-dialog/change-assistant-scheduling-dialog.component';
import { RoomsSchedulingComponent } from '../pages/rooms-scheduling/rooms-scheduling.component';
import { RoomFormComponent } from '../pages/rooms/room-form/room-form.component';
import { RoomsComponent } from '../pages/rooms/rooms.component';
import { ScheduleRoomByTheTeamComponent } from '../pages/schedule-room-by-the-team/schedule-room-by-the-team.component';
import { SchedulingModalComponent } from '../pages/schedule-room-by-the-team/scheduling-modal/scheduling-modal.component';
import { AlertModalComponent } from './alert-modal/alert-modal.component';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './material.module';
import { MySchedulingsComponent } from './my-schedulings/my-schedulings.component';
import { FileServive } from 'src/services/file.service';
import { PurchaseDetailsComponent } from '../components/purchase-details/purchase-details.component';
import { CartService } from 'src/services/cart.service';
import { SchedulingPanelComponent } from '../pages/scheduling-panel/scheduling-panel.component';
import { SendMessageComponent } from 'src/pages/send-message/send-message.component';
import { EquipmentModalComponent } from '../pages/schedule-room-by-the-team/equipment-modal/equipment-modal.component';
import { FinishSaleComponent } from '../pages/schedule-room-by-the-team/finish-sale/finish-sale.component';
import { CheckingCheckoutProfessionalAuthComponent } from '../pages/my-panel/checking-checkout-professional-auth/checking-checkout-professional-auth.component';
import { InvoiceDetailsComponent } from 'src/pages/invoices/invoice-details/invoice-details.component';
import { CheckinService } from 'src/services/checkin.service';
import { PricesTableModalComponent } from '../pages/schedule-room-by-the-team/prices-table-modal/prices-table-modal.component';
import { ProfessionalProfileComponent } from '../pages/professional-profile/professional-profile.component';
import { UserTermsModalComponent } from '../components/user-terms-modal/user-terms-modal.component';
import { DataService } from 'src/services/data.service';
import { IncreaseSchedulingPeriodModalComponent } from '../pages/my-panel/increase-scheduling-period-modal/increase-scheduling-period-modal.component';
import { ReservationModalComponent } from '../pages/reservation-modal/reservation-modal.component';
import { SpecialtyComponent } from '../pages/specialty/specialty.component';
import { SpecialtyFormComponent } from '../pages/specialty/specialty-form/specialty-form.component';
import { PricesComponent } from 'src/pages/prices/prices.component';
import { PricesFormComponent } from 'src/pages/prices/prices-form/prices-form.component';
import { ProfessionalRegisterComponent } from '../pages/professional-register/professional-register.component';
import { PatientComponent } from '../pages/patient/patient.component';
import { SchedulesofthedayComponent } from '../pages/schedulesoftheday/schedulesoftheday.component';
import { SchedulesofthedayMyPanelComponent } from '../pages/schedulesoftheday/schedulesoftheday-my-panel/schedulesoftheday-my-panel.component';
import { PatientProfileComponent } from '../pages/patient-profile/patient-profile.component';
import { PatientListComponent } from '../pages/patient-list/patient-list.component';
import { AuditComponent } from '../pages/audit/audit.component';
import { CepService } from '../services/cep.service';
import { ScheduleAppointmentsComponent } from '../pages/schedule-appointments/schedule-appointments.component';
import { AuthPatientComponent } from '../pages/auth-patient/auth-patient.component';
import {ConsultaService} from '../services/consulta.service';
import { SchedulingAppointmentModalComponent } from '../pages/schedule-appointments/scheduling-appointment-modal/scheduling-appointment-modal.component';
import { AppointmentAdministrativeComponent } from '../pages/appointment-administrative/appointment-administrative.component';
import { AppointmentAdministrativeModalComponent } from '../pages/appointment-administrative/appointment-administrative-modal/appointment-administrative-modal.component';
import { ReschedulingComponent } from '../pages/rescheduling/rescheduling.component';
import { ReschedulingModalComponent } from '../pages/rescheduling/rescheduling-modal/rescheduling-modal.component';
import { ProfessionalsDiscountModalComponent } from '../pages/professionals/professionals-discount-modal/professionals-discount-modal.component';
import { ProfessionalsDiscountComponentComponent } from '../pages/professionals/professionals-discount-component/professionals-discount-component.component';
import { ProfessionalsDiscountListComponentComponent } from '../pages/professionals/professionals-discount-list-component/professionals-discount-list-component.component';
import { RepresentanteLegalComponent } from 'src/pages/representante-legal/representante-legal.component';
import { CallsComponent } from 'src/pages/calls/calls.component';
import { DocumentsComponent } from 'src/pages/documents/documents.component';
import { ResponsavelTecnicoComponent } from 'src/pages/responsavel-tecnico/responsavel-tecnico.component';
import {MatSortModule } from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table'



registerLocaleData(localePt, 'pt');

@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    RoomsSchedulingComponent,
    RoomsComponent,
    EquipmentTypesComponent,
    EquipmentsComponent,
    ProfessionalsComponent,
    EmpresaProfissionaisComponent,
    AssistantsComponent,
    AssistantScheduleComponent,
    ReportsComponent,
    LayoutComponent,
    HeaderComponent,
    ChangeAssistantSchedulingDialogComponent,
    EquipmentFormComponent,
    EquipmentTypeFormComponent,
    RoomFormComponent,
    AssistantFormComponent,
    ProfessionalFormComponent,
    MessageModalComponent,
    InvoicesComponent,
    ScheduleRoomByTheTeamComponent,
    MyPanelComponent,
    AssistantSchedulePanelComponent,
    MySchedulingsComponent,
    ProfessionalScheduleComponent,
    SchedulingModalComponent,
    AlertModalComponent,
    AdministrativeUsersComponent,
    AssistantScheduleModalComponent,
    AdministrativeUserFormComponent,
    MessageProfessionalAproveComponent,
    ShowImagesModalComponent,
    ReportSchedulingsComponent,
    ReportProfessionalsComponent,
    PurchaseDetailsComponent,
    SchedulingPanelComponent,
    SendMessageComponent,
    EquipmentModalComponent,
    FinishSaleComponent,
    CheckingCheckoutProfessionalAuthComponent,
    InvoiceDetailsComponent,
    PricesTableModalComponent,
    ProfessionalProfileComponent,
    UserTermsModalComponent,
    IncreaseSchedulingPeriodModalComponent,
    ReservationModalComponent,
    SpecialtyComponent,
    SpecialtyFormComponent,
    PricesComponent,
    PricesFormComponent,
    ProfessionalRegisterComponent,
    PatientComponent,
    SchedulesofthedayComponent,
    SchedulesofthedayMyPanelComponent,
    PatientProfileComponent,
    PatientListComponent,
    AuditComponent,
    ScheduleAppointmentsComponent,
    SchedulingAppointmentModalComponent,
    AuthPatientComponent,
    AppointmentAdministrativeComponent,
    AppointmentAdministrativeModalComponent,
    ReschedulingComponent,
    ReschedulingModalComponent,
    ProfessionalsDiscountModalComponent,
    ProfessionalsDiscountComponentComponent,
    ProfessionalsDiscountListComponentComponent,     
    RepresentanteLegalComponent,
    ProfessionalsDiscountListComponentComponent,
    CallsComponent,
    DocumentsComponent,
    ResponsavelTecnicoComponent
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CurrencyMaskModule,
    ReactiveFormsModule,
    HttpClientModule,
    StoreModule.forRoot(appReducers),
    StoreDevtoolsModule.instrument({
      maxAge: 25
    }),
    MaterialModule,
    CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }),
    NgxMaskModule.forRoot(),
    HttpClientJsonpModule,
    MatTableModule,
    MatSortModule
  ],
  entryComponents: [
    MessageModalComponent,
    SchedulingModalComponent,
    AlertModalComponent,
    CartService,
    AssistantScheduleModalComponent,
    MessageProfessionalAproveComponent,
    ShowImagesModalComponent,
    EquipmentModalComponent,
    FinishSaleComponent,
    CheckingCheckoutProfessionalAuthComponent,
    UserTermsModalComponent,
    PricesTableModalComponent,
    IncreaseSchedulingPeriodModalComponent,
  ],
  providers: [
    ValidatorsService,
    AuthService,
    RoomService,
    SchedulingService,
    DateService,
    CalendarViewService,
    EquipmentService,
    UserService,
    LocalService,
    SpecialtyService,
    UtilsService,
    AssistantService,
    ProfessionalService,
    MessageService,
    DataService,
    InvoicesService,
    CheckinService,
    FileServive,
    CepService,
    ConsultaService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
