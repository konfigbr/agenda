package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.AgendaAssistente
import osahner.domain.Mensagem
import osahner.domain.Sala
import osahner.domain.User

interface MensagemRepository: JpaRepository<Mensagem, Long> {
  fun findByDestinatario(destinatario: User): MutableList<Mensagem>

  fun findByRemetente(remetente: User): MutableList<Mensagem>

  fun findBySala(sala: Sala): MutableList<Mensagem>
}

