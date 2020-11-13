package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class PagarFaturaDto {

  var order            : String? = null
  var installments     : Int? = null
  var totalValue       : String? = null
  var discountValue    : String? = null
  var softDescriptor   : String? = null
  var saleType         : String? = null
  var creditCard       : PagarFaturaCartaoCreditoDto? = null
  var description      : String? = null
  var reference        : String? = null
  var date             : String? = null
  var id               : Long? = null
  var ipAddress        : String? = null
  var type             : String? = null
  var customer         : CustomerDto? = null

  }
