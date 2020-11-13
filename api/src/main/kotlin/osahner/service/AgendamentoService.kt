package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.Agendamento
import osahner.domain.SolicitacaoReserva
import osahner.dto.AgendamentoDto
import osahner.enums.SolicitacaoStatus
import osahner.repository.AgendamentoRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.*


@Component
class AgendamentoService(
        private val agendamentoRepository: AgendamentoRepository
) {
  fun list(): Collection<Agendamento> = agendamentoRepository.findAll()

  fun findById(id: Long?): Optional<Agendamento> = agendamentoRepository.findById(id)

  fun save(dto: AgendamentoDto): Agendamento = agendamentoRepository.saveAndFlush(Agendamento.fromDTO(dto))

  fun delete(id: Long) = agendamentoRepository.deleteById(id)

  fun alter(id: Long, agendamento: AgendamentoDto) : Agendamento {
    var safeAgendamento = agendamento.copy(id=id)
    return  return save(safeAgendamento)
  }

  fun existsById(id: Long) = agendamentoRepository.existsById(id)

  fun findAllByStatusNotLikeAndSolicitacaoReservaIn(status: SolicitacaoStatus, listSolicitao: List<SolicitacaoReserva>) =
          agendamentoRepository.findAllByStatusNotLikeAndSolicitacaoReservaIn(status, listSolicitao)

  fun findAllByStatusLikeAndSolicitacaoReservaIn(status: SolicitacaoStatus, listSolicitao: List<SolicitacaoReserva>) =
          agendamentoRepository.findAllByStatusLikeAndSolicitacaoReservaIn(status, listSolicitao)

  fun findAllByMedicoAndInicioMesAndFimMes(idMedico: Long, inicioMes: LocalDateTime, fimMes: LocalDateTime) =
          agendamentoRepository.findAllByMedicoAndInicioMesAndFimMes(idMedico, inicioMes, fimMes)

  /*fun calcularHorasAgendadas(idMedico: Long, inicioMes: LocalDateTime, fimMes: LocalDateTime): Int =
    agendamentoRepository.findAllByMedicoAndInicioMesAndFimMes(idMedico, inicioMes, fimMes).count()
*/
  fun calcularHorasAgendadas(idMedico: Long, inicioMes: LocalDateTime, fimMes: LocalDateTime) : MutableMap<Agendamento,Int> {

    var mapResult:  MutableMap<Agendamento,Int>  = mutableMapOf()
    val agendamentos =  agendamentoRepository.findAllByMedicoAndInicioMesAndFimMes(idMedico, inicioMes, fimMes)

    agendamentos.forEach {agendamento ->
      var result = 0
      mapResult.put(agendamento,agendamento.agendaSalas!!.size)
    }
    return mapResult
  }


}