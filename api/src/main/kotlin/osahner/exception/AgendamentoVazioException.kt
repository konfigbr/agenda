package osahner.exception

import osahner.dto.SolicitacaoAgendamentoDto


data class AgendamentoVazioException(val extraData: SolicitacaoAgendamentoDto) : Exception() {
  constructor(extraData: SolicitacaoAgendamentoDto, cause: Throwable) : this(extraData) {
    initCause(cause)
  }
}


