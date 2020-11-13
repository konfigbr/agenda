package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class CobrancaProgramadaDto  {
  var date : String? = null
  var totalValue : Double? = null
  var installment : Int? = null
}
