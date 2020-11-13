package osahner.dto

import osahner.domain.AgendaSala
import java.time.LocalDateTime


open class SolicitacaoEquipamentoDto {
  var carrinho : Long? = null
  var agenda: MutableList<AgendaSala> = arrayListOf()
  var erros: MutableList<ErroValidacaoEquipamentoDto> = arrayListOf()
}

