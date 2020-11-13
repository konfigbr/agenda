export interface CreateAppointmentModel {
  dataInicio: string | Date,
  dataFim: string | Date,
  motivo: string,
  agendamentoId: number,
  pacienteId: number | null
}
