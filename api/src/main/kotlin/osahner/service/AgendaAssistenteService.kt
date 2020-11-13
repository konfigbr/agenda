package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.AgendaAssistente
import osahner.dto.AgendaAssistenteDto
import osahner.repository.AgendaAssistenteRepository
import java.util.*

@Component
class AgendaAssistenteService(
  private val agendaAssistenteRepository: AgendaAssistenteRepository
) {
  fun list(): Collection<AgendaAssistente> = agendaAssistenteRepository.findAll()

  fun findById(id: Long?): Optional<AgendaAssistente> = agendaAssistenteRepository.findById(id)

  fun save(dto: AgendaAssistenteDto): AgendaAssistente = agendaAssistenteRepository.saveAndFlush(AgendaAssistente.fromDTO(dto))

  fun delete(id: Long) = agendaAssistenteRepository.deleteById(id)

  fun alter(id: Long, agendamento: AgendaAssistenteDto) : AgendaAssistente {
    var safeAgendamento = agendamento.copy(id=id)
    return  return save(safeAgendamento)
  }

  fun existsById(id: Long) = agendaAssistenteRepository.existsById(id)

}
