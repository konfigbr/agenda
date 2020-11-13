package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class AlterarCobrancaProgramadaDto  {
  var id : String? = null
  var subscriptionId : String? = null
  var newValue : Int? = null
  var newDate : String? = null
}
