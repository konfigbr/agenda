package osahner.dto

import osahner.domain.Fatura
import osahner.domain.Medico
import osahner.domain.User
import java.sql.Timestamp
import javax.persistence.*

data class CarrinhoDto(
  var id: Long?,

  var version: Long?,

  var valor: Double?,

  var dateCreated: Timestamp?,

  var custo: Double?,

  var lastUpdated: Timestamp?,

  var desconto: Double?,

  var createdById: Long?,

  var status: String?,

  var refUserEntity: Medico?,

  var refCreatedUserEntity: Medico?,

  var refFaturaEntities: MutableList<Fatura>?

  //var refSolicitacaoEntities: MutableList<Solicitacao>?

  )

