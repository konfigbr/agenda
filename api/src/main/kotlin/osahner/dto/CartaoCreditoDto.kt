package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class CartaoCreditoDto {
  val carrinho: Long? = null
  var cardHolderName : String? = null
  var number : String? = null
  var expirationMonth : String? = null
  var expirationYear : String? = null
  var securityCode : String? = null
  var brandName : String? = null
  var holderName : String? = null
  var holderCpf : String? = null
  var holderEmail : String? = null
  var holderPhone : String? = null
  var mesmosDadosMedico : Boolean = false
  var mesmoDoMedico : Boolean = false
  var isAtendente : Boolean = false
}
