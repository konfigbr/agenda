package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class PagarFaturaCartaoCreditoDto {

  var number            : String? = null
  var expirationMonth   : String? = null
  var expirationYear    : String? = null
  var securityCode      : String? = null
  var holderName        : String? = null
  var brandName         : String? = null
  var holder            : HolderDto? = null

}
