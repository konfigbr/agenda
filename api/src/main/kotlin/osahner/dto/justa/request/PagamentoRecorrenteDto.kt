package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class PagamentoRecorrenteDto {

  var legalDocument            : String? = null
  var category                 : String? = null
  var softDescriptor           : String? = null
  var cardInfo                 : PagamentoRecorrenteCardInfoDto? = null
  var customer                 : CustomerDto? = null
  var charges                  : MutableList<ChargesItemDto>? = null

}
