package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class HolderAddressDto {

  var type             : String? = null
  var street           : String? = null
  var number           : String? = null
  var complement       : String? = null
  var district         : String? = null
  var zipCode          : String? = null
  var state            : String? = null
  var city            : String? = null
  var country          : String? = null

}
