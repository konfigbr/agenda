package osahner.exception

import osahner.dto.ErroValidacaoReservaDto
import osahner.dto.SolicitacaoAgendamentoDto


data class ErroRealocarAgendamentoException(val extraData: MutableList<ErroValidacaoReservaDto>) : Exception() {
  constructor(extraData: MutableList<ErroValidacaoReservaDto>, cause: Throwable) : this(extraData) {
    initCause(cause)
  }
}


