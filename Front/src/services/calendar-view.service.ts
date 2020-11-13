import { Injectable } from '@angular/core';
import { CalendarWeekViewBeforeRenderEvent } from 'angular-calendar';
import { EventColor } from 'calendar-utils';
import { UserService } from './user.service';

import * as moment from 'moment';
import Scheduling from 'src/models/scheduling.model';
import { ReservationType } from 'src/models/reservation-type.model';
import { ProfileRoles } from 'src/models/profile-roles.model';
import { AuthService } from './auth.service';
import { DisponibilidadeAgenda } from 'src/pages/schedule-appointments/disponibilidade-agenda.model';

@Injectable({
  providedIn: 'root'
})
export class CalendarViewService {

  colors: any = {
    red: {
      primary: 'rgb(180, 64, 64)',
      secondary: 'rgb(180, 64, 64, 0.2)'
    },
    blue: {
      primary: 'rgb(14, 52, 106)',
      secondary: 'rgb(14, 52, 106, 0.1)'
    },
    yellow: {
      primary: 'rgba(180, 140, 64)',
      secondary: 'rgba(180, 140, 64, 0.3)'
    }
  };

  constructor(private userService: UserService, private authService: AuthService) { }

  getCollorByScheduling(scheduling: Scheduling): EventColor {
    if (scheduling.tipoReserva === ReservationType.MANUTENCAO) {
      return this.colors.red
      // tslint:disable-next-line:max-line-length
    } else if (this.authService.getAccessType() === ProfileRoles.ROLE_ATENDENTE ||
      // tslint:disable-next-line:max-line-length
      ((this.authService.getUserId() !== undefined) && (scheduling.user.id === parseInt(this.authService.getUserId())))
    ) {
      return this.colors.blue
    } else {
      return this.colors.yellow
    }
  }

  getCorPorTipoReserva(tipoReserva: ReservationType): EventColor {
    if (tipoReserva === ReservationType.MANUTENCAO) {
      return this.colors.red
    } else {
      return this.colors.blue
    }
  }

  getDescricaoPorAgendamento(agendamento: Scheduling): string {
    if (agendamento.tipoReserva === ReservationType.MANUTENCAO) {
      return 'MANUTENÇÃO'
    } else if (this.userService.perfilUsuarioValue === ProfileRoles.ROLE_ADMINISTRADOR
      || this.userService.perfilUsuarioValue === ProfileRoles.ROLE_ATENDENTE
      || agendamento.medico.id === (this.userService.usuarioValue as any).id) {

      let titulo = agendamento.assistente.nome

      agendamento.equipamentos.forEach(equipamento => {
        titulo += '<br>' + equipamento.nome + ' (' + moment(equipamento.inicio).format("HH") + "-" + moment(equipamento.fim).format('HH') + ")"
      })
      return titulo
    } else {
      return agendamento.medico.nome
    }
  }

  getDescriptionBySchedulingWithStatus(scheduling: Scheduling): string {
    if (scheduling.tipoReserva === 'MANUTENCAO') {
      return 'MANUTENÇÃO'
    } else if (this.authService.getAccessType() === 'ROLE_ATENDENTE'
      || this.authService.getAccessType() === 'ROLE_ADMINISTRADOR'
      || scheduling.user.id === parseInt(this.authService.getUserId())) {

      let titulo = ""
      if (scheduling.sala && scheduling.sala.nome) {
        titulo += `<span class="opt-room-indicator">${scheduling.sala.nome}</span><br>`
      }
      if (scheduling.user && scheduling.user.nome) {
        titulo += `<span class="opt-professional-indicator">${scheduling.user.nome}</span><br>`
      }
      if (scheduling.assistente) {
        titulo += `<span class="opt-assistant-indicator">${scheduling.assistente.nome}</span>`
      }
      titulo += `<br>${
        scheduling.agendamentoStatusAtual === 'NAO_INICIADO'
          ? '<span class="opt-status-warning">Não iniciado<span>'
          : scheduling.agendamentoStatusAtual === 'OK'
            ? '<span class="opt-status-confirm">Iniciado<span>'
            : scheduling.agendamentoStatusAtual === 'LIBERADO'
              ? '<span class="opt-status-danger">Sala liberada<span>'
              : ''
        }`

      scheduling.equipamentos && scheduling.equipamentos.forEach(equipamento => {
        titulo += '<br>' + equipamento.nome + ' (' + moment(equipamento.inicio).format("HH") + "-" + moment(equipamento.fim).format('HH') + ")"
      })

      return titulo
    } else {
      return ''
    }
  }

  getDescricaoPorAgendamentoComSala(agendamento: Scheduling): string {
    if (agendamento.tipoReserva === ReservationType.MANUTENCAO) {
      return 'MANUTENÇÃO'
    } else if (agendamento.medico.id === (this.userService.usuarioValue as any).id) {
      let titulo = agendamento.sala + '<br>' + agendamento.assistente.nome
      agendamento.equipamentos.forEach(equipamento => {
        titulo += '<br>' + equipamento.nome + ' (' + moment(equipamento.inicio).format("HH") + "-" + moment(equipamento.fim).format('HH') + ")"
      })
      return titulo
    } else {
      return agendamento.medico.nome
    }
  }

  getDescricaoAgendamentoAssistente(agendamento: Scheduling): string {
    let titulo = agendamento.sala + '<br>' + agendamento.medico.nome
    agendamento.equipamentos.forEach(equipamento => {
      titulo += '<br>' + equipamento.nome + ' (' + moment(equipamento.inicio).format("HH") + "-" + moment(equipamento.fim).format('HH') + ")"
    })
    return titulo
  }

  desabilitar(renderEvent: CalendarWeekViewBeforeRenderEvent): void {
    renderEvent.hourColumns.forEach(hourColumn => {
      hourColumn.hours.forEach(hour => {
        hour.segments.forEach(segment => {
          // tslint:disable-next-line:max-line-length
          if (segment.date.getHours() === 12 || segment.date.getHours() === 17 || (segment.date.getHours() >= 12 && segment.date.getDay() === 6)) {
            segment.cssClass = 'opt-calendar-disable-color';
          }
        });
      });
    });
  }

  private getNomesTiposEquipamentos(agendamento: Scheduling): any[] {
    if (!agendamento.equipamentos) {
      return []
    }

    const tipos = agendamento.equipamentos.map(e => {
      return { id: e.id.toString(), nome: e.nome, enableRemove: null, custo: null, };
    })

    return tipos.filter((obj, pos, arr) => {
      return arr.map(mapObj => mapObj['id']).indexOf(obj['id']) === pos;
    });
  }

  getColorByDisponibilidadeAgenda(disponibilidadeAgenda: DisponibilidadeAgenda): EventColor {
    if (disponibilidadeAgenda === DisponibilidadeAgenda.DISPONIVEL) {
      return this.colors.blue
    } else if (disponibilidadeAgenda === DisponibilidadeAgenda.PARCIALMENTE_DISPONIVEL) {
      return this.colors.yellow
    } else {
      return this.colors.red
    }
  }
}
