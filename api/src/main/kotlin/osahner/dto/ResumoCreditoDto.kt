package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class ResumoCreditoDto {

  var abatimento : Double? = null
  var creditoRecuperado : Double? = null
  var  fatura : Fatura? = null
  var  creditoGanho : Double? = null

}
