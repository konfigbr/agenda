package osahner.dto.justa.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class PagamentoDataResponseDto {

  var finalizedAt : String? = null
  var status : String? = null
  var localCode : String? = null
  var gatewayName : String? = null
  var nsuCode : String? = null
  var authorizationCode : String? = null
  var gatewayIdentifier : String? = null
  var gatewayOrder : String? = null
  var acquirerIdentifier : String? = null
  var acquirerAffiliationCode : String? = null
  var acquirerName : String? = null
  var acquirerMessage : String? = null
  var acquirerReturnCode : String? = null
  var valueCents : Long? = null
  var installments : Long? = null
}
