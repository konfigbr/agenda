package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.AgendaAssistente
import osahner.domain.Mensagem
import osahner.domain.Sala
import osahner.domain.User
import osahner.dto.AgendaAssistenteDto
import osahner.dto.MensagemDto
import osahner.repository.AgendaAssistenteRepository
import osahner.repository.MensagemRepository
import java.util.*

@Component
class MensagemService(
  private val mensagemRepository: MensagemRepository
) {
  fun list(): Collection<Mensagem> = mensagemRepository.findAll()

  fun findById(id: Long?): Optional<Mensagem> = mensagemRepository.findById(id)

  fun save(dto: MensagemDto): Mensagem = mensagemRepository.saveAndFlush(Mensagem.fromDTO(dto))

  fun delete(id: Long) = mensagemRepository.deleteById(id)

  fun alter(id: Long, mensagem: MensagemDto) : Mensagem {
    var safeMensagem = mensagem.copy(id=id)
    return  return save(safeMensagem)
  }

  fun existsById(id: Long) = mensagemRepository.existsById(id)

  fun findByRemetente(remetente: User): MutableList<Mensagem> = mensagemRepository.findByRemetente(remetente)

  fun findByDestinatario(destinatario: User): MutableList<Mensagem> = mensagemRepository.findByDestinatario(destinatario)

  fun findBySala(sala: Sala): MutableList<Mensagem> = mensagemRepository.findBySala(sala)
}
