package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.StatusFatura
import osahner.enums.StatusJusta
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class PushPagamentoDto {
  var mid : Int? = null
  var flagName : String? = null
  var cardNumber : String? = null
  var order : String? = null
  var installments : Int? = null
  var subTotalValue : Int? = null
  var holderName : String? = null
  var holderCpf : String? = null
  var holderPhone : String? = null
  var holderMail : String? = null
  var holderAddressStreet : String? = null
  var holderAddressNumber : String? = null
  var holderAddressComplement : String? = null
  var holderAddressDistrict : String? = null
  var holderAddressZipCode : String? = null
  var holderAddressCity : String? = null
  var holderAddressState : String? = null
  var holderAddressCountry : String? = null
  var holderAddressType : String? = null
  var holderBirthday : String? = null
  var customerName : String? = null
  var customerPhone : String? = null
  var customerMail : String? = null
  var customerCpf : String? = null
  var customerAddressStreet : String? = null
  var customerAddressNumber : String? = null
  var customerAddressComplement : String? = null
  var customerAddressDistrict : String? = null
  var customerAddressZipCode : String? = null
  var customerAddressCity : String? = null
  var customerAddressState : String? = null
  var customerAddressCountry : String? = null
  var customerAddressType : String? = null
  var customerBirthday : String? = null
  var saleType : String? = null
  var type : String? = null
  var ipAddress : String? = null
  var description : String? = null
  var dateTransaction : String? = null
  var latitude : String? = null
  var longitude : String? = null
  var status : StatusJusta = StatusJusta.PROCESSING
  var nsuCode : String? = null
  var nsuGateway : String? = null
  var authorizationCode : String? = null
  var acquirer : String? = null
  var totalValue: Double? = null
}

