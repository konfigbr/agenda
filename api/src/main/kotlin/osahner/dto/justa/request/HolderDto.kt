package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.justa.request.HolderAddressDto

@JsonIgnoreProperties(ignoreUnknown = true)
open class HolderDto {

  var name      : String? = null
  var phone     : String? = null
  var email     : String? = null
  var cpf       : String? = null
  var address       : HolderAddressDto? = null

}
