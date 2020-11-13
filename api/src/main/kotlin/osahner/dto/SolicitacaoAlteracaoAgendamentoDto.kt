package osahner.dto

import osahner.domain.AgendaSala
import osahner.domain.CarrinhoCompra
import osahner.enums.SolicitacaoStatus
import java.time.LocalDateTime
import javax.persistence.Column


open class SolicitacaoAlteracaoAgendamentoDto {
  var sala: Long? = null
  var id: Long? = null
  var inicio : LocalDateTime? = null
  var fim: LocalDateTime? = null
  var agendamentoDTOs : MutableList<AgendamentoDto>? = arrayListOf()
  var erros : MutableList<ErroValidacaoReservaDto>?=arrayListOf()
  var validacoes : MutableList<ErroValidacaoReservaDto>? = arrayListOf()
  var status : SolicitacaoStatus? =null
  var carrinhoCompra : Long? = null
  var custo: Double? = null

}

