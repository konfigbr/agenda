package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.CreditoUtilizado
import java.time.LocalDate
import java.time.LocalDateTime

@JsonIgnoreProperties("hibernateLazyInitializer")
data class FaturaDetalhesDto (
  var valorTotal: Double? = null,
  var descontos: Double? = null,
  var valor: Double? = null,
  var resumoCompraDetalhadoDTOS  : MutableList<ResumoCompraDetalhadoDto>? = null,
  var creditosUtilizados: MutableSet<CreditoUtilizado>? = null,
  var dataCobranca: LocalDate? = null,
  var dataCriada: LocalDateTime? = null
)
