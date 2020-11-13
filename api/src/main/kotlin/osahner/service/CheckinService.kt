package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.CheckIn
import osahner.dto.CheckinDto
import osahner.dto.TipoEquipamentoDto
import osahner.repository.AgendaSalaRepository
import osahner.repository.AgendamentoRepository
import osahner.repository.CheckinRepository
import java.util.*

@Component
class CheckinService(
  private val checkinRepository: CheckinRepository,
  private val agendaSalaRepository: AgendaSalaRepository
) {
  fun list(): Collection<CheckIn> = checkinRepository.findAll()

  fun findById(id: Long?): Optional<CheckIn> = checkinRepository.findById(id)


  //fun save(dto: CheckinDto): CheckIn = checkinRepository.saveAndFlush(CheckIn.fromDTO(dto))

  fun delete(id: Long) = checkinRepository.deleteById(id)

  /*fun alter(id: Long, checkin: CheckinDto) : CheckIn {
    var safeTipoEquipamento = checkin.copy(id=id)
    return save(safeTipoEquipamento)
  }*/

  fun existsById(id: Long) = checkinRepository.existsById(id)

  fun confirmaUsuario(idAgendamentoSala: Long?, idUser: Long?): Boolean {

   var agendamento = agendaSalaRepository.findById(idAgendamentoSala).get()

    if(idUser != agendamento.agendamento?.medico?.user?.id && idUser != agendamento.assistente?.user?.id){

      return false
    }
    return true
  }


  fun confirmaUsuarioCheckinDuplo(idAgendamentoSala: Long?, idUser: Long?): Boolean {

    var agendamento = agendaSalaRepository.findById(idAgendamentoSala).get()

    if(idUser != agendamento.agendamento?.medico?.user?.id ){

      return false
    }
    return true
  }


}
