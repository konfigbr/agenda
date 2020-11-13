package osahner.dto

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


data class AddressDto(

  var data: String?,

  var horaInicio: Int?,

  var horaFim: Int?,

  var valorPago: Double?,

  var assistente: String?,

  var profissional: String?,


  var checkinAssistente: String?,

  var checkinProfessional: String?,

  var checkoutAssitente: String?,

  var checkoutProfessional: String?

)
