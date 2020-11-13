package osahner.dto.justa.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class PagamentoRecorrenteResponseDto {

  var id            : String? = null
  var charges       : MutableList<ChargesRecorrenciaDto>? = null

}
