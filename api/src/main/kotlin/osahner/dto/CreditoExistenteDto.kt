package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class CreditoExistenteDto {

  var creditoUtilizado : Double? = null
  var  faturas : MutableList<ResumoFaturaDto>? = mutableListOf()

}
