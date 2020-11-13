package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class ChargesItemDto {

  var date           : String? = null
  var totalValue     : String? = null
  var installment    : String? = null

}
