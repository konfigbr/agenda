package osahner.dto

class DetalhadoDto {
  var custo: Double? = null
  var detalhes: MutableList<ResumoCompraDetalhadoDto> = mutableListOf()
  var mesReferencia: String? = null
}
