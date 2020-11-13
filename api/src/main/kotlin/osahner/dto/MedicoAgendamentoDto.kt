package osahner.dto

import osahner.domain.AgendaSala
import osahner.domain.Sala
import java.time.LocalDateTime


open class MedicoAgendamentoDto {
  var sala: Sala? = null
  var inicio : String? = null
  var fim: String? = null

}

