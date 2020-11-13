package osahner.domain

import osahner.dto.AddressDto
import osahner.toMap
import osahner.toStringArray
import osahner.writeValueAsString
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Address(
  @Id
  @GeneratedValue

  var data: String?,

  var horaInicio: Int?,

  var horaFim: Int?,

  var valorPago: Double?,

  var assistente: String?,

  var profissional: String?,

  var salaPronta: LocalDateTime?,

  var salaDesocupada: LocalDateTime?



) {
 /* fun toDTO() = AddressDto(


    data = this.data,
    horaInicio = this.horaInicio,
    horaFim = this.horaFim,
    valorPago = this.valorPago,
    assistente = this.assistente,
    profissional = this.profissional,
    salaPronta = this.salaPronta,
    salaDesocupada = salaDesocupada


  )

  companion object {
    fun fromDTO(dto: AddressDto) = Address(

      data = dto.data,
      horaInicio = dto.horaInicio,
      horaFim = dto.horaFim,
      valorPago = dto.valorPago,
      assistente = dto.assistente,
      profissional = dto.profissional,
      salaPronta = dto.salaPronta,
      salaDesocupada = dto.salaDesocupada
    )
  }*/
}
