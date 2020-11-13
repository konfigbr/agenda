package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp
import javax.persistence.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class SolicitacaoDto(
  var id: Long?,

  var version: Long?,

  var fim: Timestamp?,

  var dateCreated: Timestamp?,

  var custo: Double?,

  var status: String?,

  var inicio: Timestamp?,


  //var refAgendamentoEntities: MutableList<Agendamento>?,

  var refCarrinhoCompraEntity: CarrinhoCompra?,

  var refUserEntity: Medico?,

  var refSalaEntity: Sala?,

  var refTipoEquipamentoEntity: TipoEquipamento?

)



