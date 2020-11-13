package osahner.dto

import com.opencsv.bean.CsvBindByName
import osahner.domain.Address
import java.time.LocalDate
import java.time.LocalDateTime

class AddressImportDto {
  //@CsvBindByName(required = true)
  var id: Long? = null


  var data: String? = null


  var horaInicio: Int? = null


  var horaFim: Int? = null


  var valorPago: Double? = null


  var assistente: String? = null


  var profissional: String? = null


  var salaPronta: LocalDateTime? = null


  var salaDesocupada: LocalDateTime? = null



  fun toAddress() = Address(

    data = this.data,
    horaInicio = this.horaInicio,
    horaFim = this.horaFim,
    valorPago = this.valorPago,
    assistente = this.assistente,
    profissional = this.profissional,
    salaPronta = this.salaPronta,
    salaDesocupada = salaDesocupada
  )
}
