package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class CustomerDto : HolderDto() {
  var ipAddress : String? = null

}
