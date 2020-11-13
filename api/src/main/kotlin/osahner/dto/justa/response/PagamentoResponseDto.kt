package osahner.dto.justa.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class PagamentoResponseDto {

  lateinit var message : String
  lateinit var data : PagamentoDataResponseDto

}
