import { CalendarEventAction } from 'angular-calendar';
import { endOfDay, startOfDay } from 'date-fns';

export class ReservaEventModel {
    sala: any
    assistente: any
    periodo: any
    repeticaoPeriodo: number
    horaInicio: Date;
    horaFim?: Date;
    diasSemana: number[]
    dataFim: string | Date
    maximoAgendamentos: number
    actions?: CalendarEventAction[];
    cssClass?: string;
    resizable?: {
        beforeStart?: boolean;
        afterEnd?: boolean;
    };
    draggable?: boolean;


    constructor(data?) {
        data = data || {};
        this.sala = data.sala ? data.sala : null
        this.assistente = data.assistente ? data.assistente : null
        this.periodo = data.periodo ? data.periodo : 'SEMANAL'
        this.repeticaoPeriodo = data.repeticaoPeriodo ? data.repeticaoPeriodo : 0
        this.horaInicio = new Date(data.horaInicio) || startOfDay(new Date());
        this.horaFim = new Date(data.horaFim) || endOfDay(new Date());
        this.diasSemana = []
        this.dataFim = new Date(data.dataFim) || endOfDay(new Date());
        this.maximoAgendamentos = 0
        this.draggable = data.draggable;
        this.resizable = {
            beforeStart: data.resizable && data.resizable.beforeStart || true,
            afterEnd: data.resizable && data.resizable.afterEnd || true
        };
        this.actions = data.actions || [];
        this.cssClass = data.cssClass || '';
    }
}
