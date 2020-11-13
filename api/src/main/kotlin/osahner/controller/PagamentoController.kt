package osahner.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.domain.CarrinhoCompra
import osahner.domain.Fatura
import osahner.dto.AgendamentoRequestDto
import osahner.dto.CartaoCreditoDto
import osahner.dto.JustaRequestDto
import osahner.dto.PushPagamentoDto
import osahner.service.CarrinhoService
import osahner.service.FaturaService
import osahner.service.MedicoService
import java.awt.SystemColor.text

@RestController
@RequestMapping("/justa")
class PagamentoController(
  private val faturaService: FaturaService,
  private val carrinhoService: CarrinhoService
) {
  @PostMapping(value = ["/pay-schedule"])
  fun pagarEProgramar(@RequestBody params: CartaoCreditoDto): Any? {
    try {
        println("Params:" + params)

      val creditCard =  CartaoCreditoDto()
      creditCard.number = params.number
      creditCard.brandName = params.brandName
      creditCard.expirationMonth = params.expirationMonth
      creditCard.expirationYear = params.expirationYear
      creditCard.securityCode = params.securityCode
      creditCard.cardHolderName = params.cardHolderName
      creditCard.holderName = params.holderName
      creditCard.holderCpf = params.holderCpf
      creditCard.holderEmail = params.holderEmail
      creditCard.holderPhone = params.holderPhone
      creditCard.mesmosDadosMedico = params.mesmoDoMedico

      carrinhoService.salvar(creditCard)
    //  ResponseEntity.ok().contentType(MediaType.parseMediaType("application/json"))
   //   render(status: OK, contentType: "application/json", text: [])

    } catch (e: Exception) {
      return ResponseEntity.badRequest().contentType(MediaType.parseMediaType("application/json"))//.build<Any>()
     // render(status: INTERNAL_SERVER_ERROR, contentType: "application/json", text: e.getMessage())
    }
      return  ResponseEntity.ok().contentType(MediaType.parseMediaType("application/json"))//.build<Any>()
  }


  @PostMapping(value = ["/push-payment"])
  fun pushPagamento(@RequestBody params: PushPagamentoDto): Any? {

    try {

      println("Params:" + params)

        var mid = params.mid    // Merchant identidficador do estabelecimento que receberá a transação
        var flagName = params.flagName  // bandeira do cartão
        var order = params.order // número da transação
        var installments = params.installments // parcelas
        var totalValue = params.totalValue
        var subTotalValue = params.subTotalValue
        var customerName = params.customerName
        var customerPhone = params.customerPhone
        var customerMail = params.customerMail
        var customerCpf = params.customerCpf
        var saleType = params.saleType
        var type = params.type
        var ipAddress = params.ipAddress
        var description = params.description
        var dateTransaction = params.dateTransaction
        var status = params.status
        var nsuCode = params.nsuCode
        var nsuGateway = params.nsuGateway
        var authorizationCode = params.authorizationCode

        if (saleType == "RECURRENT_CREDIT")
          faturaService.atualizarStatusFatura(order, authorizationCode, status)


    } catch (e: Exception) {
      ResponseEntity.badRequest().body("Não foi possivel atualizar Status da fatura")
    }
    return  ResponseEntity.ok().contentType(MediaType.parseMediaType("application/json"))
  }



}


