package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class PagamentoRecorrenteCardInfoDto {

  var cardNumber                : String? = null
  var brandName                 : String? = null
  var expirationMonth           : String? = null
  var expirationYear            : String? = null
  var securityCode              : String? = null
  var holderName                : String? = null
  var legalDocument             : String? = null
  var holder                    : HolderDto? = null
 }
