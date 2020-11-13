package osahner.service

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import osahner.domain.CobrancaProgramada
import osahner.domain.Fatura
import osahner.domain.Recorrencia
import osahner.dto.CartaoCreditoDto
import osahner.dto.justa.request.*
import osahner.dto.justa.response.CobrancaProgramadaResponseDto
import osahner.dto.justa.response.PagamentoRecorrenteResponseDto
import osahner.dto.justa.response.PagamentoResponseDto
import osahner.enums.StatusFatura
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime


@Component
@Transactional
class PagamentoService() {
  private var token_type: String? = null
  private var oauth_token: String? = null
  private var expires_in: LocalDateTime? = null
  private val logger: Logger = LogManager.getLogger()

  fun criarPagamentoRecorrente(recorrencia: Recorrencia, cartaoCredito: CartaoCreditoDto): PagamentoRecorrenteResponseDto {
    println("Criar pagamento recorrente")

    oauthAuthentication()
    var body = requestPagamentoRecorrenteAsJSON(cartaoCredito, recorrencia)
    var resposta = connect( URL(getSubscriptionURL("/api/subscription/custom")),body,
      "POST","Bearer "+ this.oauth_token!!)

    val gson = Gson()

    var result : PagamentoRecorrenteResponseDto = gson.fromJson(resposta, PagamentoRecorrenteResponseDto::class.java)

    return result
  }



  fun criarCobrancaProgramada(recorrencia: Recorrencia, cp: CobrancaProgramada): CobrancaProgramadaResponseDto {
    println("Criar Cobranca Programada")

    oauthAuthentication()

    var body = CobrancaProgramadaDto()


    body.date = cp.dataCobranca.toString()
    body.totalValue = cp.valor
    body.installment = cp.sequenciaCobranca

    val gson = Gson()
    val jsonString = gson.toJson(body)

    println(jsonString)

    var resposta = connect( URL(getSubscriptionURL("/api/subscription/add/"+ recorrencia.justaRecorrenciaId)),
      jsonString,
      "POST", "Bearer "+ this.oauth_token!!)

    var result : CobrancaProgramadaResponseDto = gson.fromJson(resposta, CobrancaProgramadaResponseDto::class.java)

    return result
  }


  fun atualizarCobrancaProgramada(recorrencia: Recorrencia?, cp: CobrancaProgramada): String {

    println("Atualiza Cobranca Programada")

    oauthAuthentication()

    val body = AlterarCobrancaProgramadaDto()
    body.id = cp.justaCobrancaId
    body.subscriptionId = recorrencia?.justaRecorrenciaId
    body.newValue = cp.valor?.toInt() //String.format("%,.2f", cp.valor).replace("[\\.\\,]".toRegex(),"")
    body.newDate = cp.dataCobranca.toString()

    val gson = Gson()
    val jsonString = gson.toJson(body)

    println(jsonString)

    var resposta = connect( URL(getSubscriptionURL("/api/subscription/charge/custom")),
      jsonString,
      "PUT", "Bearer "+ this.oauth_token!!)
    return resposta
  }

  fun cancelarCobrancaProgramada(cp: CobrancaProgramada) : String {
    println("Cancelar Cobranca Programada")

    oauthAuthentication()

    var resposta = connect(URL(getSubscriptionURL("/api/subscription/charge/custom/cancel/"+cp.justaCobrancaId)),
      "",
      "PUT", "Bearer "+ this.oauth_token!!)
    return resposta

  }

  @Throws(Exception::class)
  fun pagar(fatura: Fatura, cartaoCredito: CartaoCreditoDto) : Fatura {
    logger.info("Pagar")

    oauthAuthentication()
    val url =  URL(getCheckoutURL("/payment"))
    val data = requestPaymentDataAsJSON(fatura, cartaoCredito)
    val resposta = connect(url, data, "POST", getAuthorizationToken())


    val gson = Gson()
    val result : PagamentoResponseDto = gson.fromJson(resposta, PagamentoResponseDto::class.java)


    val json = JSONObject(resposta)

    var message : String = result.message

    var status  : String? = result.data.status

    if (message.equals("Sucesso") && status.equals("AUTHORIZED")) {
      var localCode : String? =  result.data.localCode
      fatura.uuidTransacao = localCode
      fatura.status = StatusFatura.PAGA
    } else { // erro
      val acquirerMessage =   result.data.acquirerMessage
      throw RuntimeException("ERROR: "+status+" - "+ acquirerMessage )
    }
    return fatura
  }

  fun oauthAuthentication() {
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    if (this.expires_in != null &&
      LocalDateTime.now(saoPauloZone).isBefore(this.expires_in))
      return

    //Sandbox
    var username =  "nitryx"
    var password =  "6812a6a463aa4377869d47ebcff9b3f2"
    var authorization =  "ZGZjMjY0MmEwMTZhNGZkM2ExMTIwYzc0NzExZWVhNGM6MWVjYWUwNmQzMTE0NDUzYWEwYWQwMjg3ZTM1Njg3OTg="

    /* //Prod
    var username = "opt-doc"
    var password = "ce23ea7e07f3483c8018c2ad02b693ea"
    var authorization = "YzhmMDU0OGFjOTk2YjRlY2Q3ZWI3ZWZmMzExMmI2NGY6OGI3ZGYzNTJiNzljZmE3Yzg5ZmE0OTY0Y2ZiYWI0OTQ="
    */

    var postData : String = "grant_type=password" + "&username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8")
    var url =  URL(getOAuthURL("/oauth/token"))

    val connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod("POST")
    connection.doOutput = true
    connection.setRequestProperty("Accept", "application/json");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    connection.setRequestProperty("Authorization", "Basic " + authorization)
    connection.setRequestProperty( "Content-Length", Integer.toString(postData.length));
    connection.connect()

    var writer = connection.outputStream
    writer.write(postData.toByteArray(Charsets.UTF_8))
    writer.flush()
    writer.close()

    var connectionRC = connection.getResponseCode()

    if (connectionRC == HttpURLConnection.HTTP_OK) {
      //log.info(new InputStreamReader(connection.inputStream).readLines())
      var resposta = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
      val json = JSONObject(resposta)
      println("Token " + resposta)
      this.oauth_token = json.get("access_token").toString()
      this.token_type = json.get("token_type").toString()
      this.expires_in = LocalDateTime.now(saoPauloZone).plusSeconds(json.get("expires_in").toString().toLong())

    } else {
      var error = connection.errorStream.bufferedReader(Charsets.UTF_8).readText()
      throw RuntimeException("ERROR: Erro de oAuth. Justa API return" + connectionRC +" - "+connection.responseMessage+ " - " +error )
    }

  }


  private fun getOAuthURL(transacao : String) : String {
    return "https://gateway-sandbox.justa.com.vc" + transacao
  }

  private fun getCheckoutURL(transacao : String) : String {
    return "https://gateway-sandbox.justa.com.vc/gateway-web/transaction" + transacao
  }

  private fun getSubscriptionURL(transacao : String) : String {
    return "https://gateway-sandbox.justa.com.vc" + transacao
  }

  private fun getAuthorizationToken() : String {
    //SandBox
    return "MzUwOTUxMDAwMDAxMjM6NXhqYWhGNlt3JSVS"

    //Producao
    //return "MzUwOTUxMDAwMDAxMjM6MXBOJkloMjBfNTVa"

    //return "MzUwOTUxMDAwMDAxMjM6NXhqYWhGNlt3JSVS"
    //return "bml0cnl4LXN0Zzo1eGphaEY2W3clJVI="
  }

  private fun connect(url : URL , bodyData : String, requestMethod : String, token : String) : String {
    var resposta : String? = null
    val connection = url.openConnection() as HttpURLConnection
    connection.setRequestMethod(requestMethod)
    connection.setRequestProperty("Authorization", token);
    connection.setRequestProperty("Token", getAuthorizationToken());

    if (requestMethod == "POST" || requestMethod == "PUT") {
      connection.doOutput = true
      connection.setRequestProperty("Content-Type", "application/JSON; charset=UTF-8")

      var writer = connection.outputStream
      writer.write(bodyData.toByteArray(Charsets.UTF_8))
      writer.flush()
      writer.close()

    } else {
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
    }

    connection.connect()

    val connectionRC = connection.getResponseCode()

    if (connectionRC == HttpURLConnection.HTTP_OK) {
      //log.info(new InputStreamReader(connection.inputStream).readLines())
      resposta = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
      logger.info("Resposta " + resposta)
    } else {
      var error = connection.errorStream.bufferedReader(Charsets.UTF_8).readText()
      logger.info(error)
      throw RuntimeException("ERROR: Erro de oAuth. Justa API return " + connectionRC +" - "+ connection.responseMessage )
    }

    return resposta
  }

  private fun requestPaymentDataAsJSON(fatura : Fatura, cartaoCredito : CartaoCreditoDto) : String {
    var  medico = fatura.medico
    var customerName :String =  ""
    var holderName : String = ""

    if (cartaoCredito.mesmosDadosMedico) {
      customerName = medico!!.user!!.nome!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
      holderName = cartaoCredito.cardHolderName!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
    } else {
      customerName = cartaoCredito.holderName!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
      holderName = cartaoCredito.cardHolderName!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
    }

    var pagamento = PagarFaturaDto()

    pagamento.order = fatura.idPagamento
    pagamento.installments = 1
    pagamento.totalValue = String.format("%,.2f", fatura.valor).replace("[\\.\\,]".toRegex(),"")
    pagamento.discountValue = "0"
    pagamento.softDescriptor= "OptDoc"
    pagamento.saleType= "CREDIT"
    pagamento.creditCard = PagarFaturaCartaoCreditoDto()
    pagamento.creditCard?.number = cartaoCredito.number
    pagamento.creditCard?.expirationMonth = cartaoCredito.expirationMonth
    pagamento.creditCard?.expirationYear = cartaoCredito.expirationYear
    pagamento.creditCard?.securityCode = cartaoCredito.securityCode
    pagamento.creditCard?.holderName = holderName
    pagamento.creditCard?.brandName = cartaoCredito.brandName
    pagamento.creditCard!!.holder = HolderDto()
    pagamento.creditCard!!.holder?.name = customerName
    pagamento.creditCard!!.holder?.phone = if (cartaoCredito.mesmosDadosMedico)  medico?.telefone else cartaoCredito.holderPhone
    pagamento.creditCard!!.holder?.email = if (cartaoCredito.mesmosDadosMedico)  medico?.user?.username else cartaoCredito.holderEmail
    pagamento.creditCard!!.holder?.cpf = cartaoCredito.holderCpf
    pagamento.description= "Fatura - " + fatura.idPagamento
    pagamento.reference= fatura.idPagamento
    pagamento.date= LocalDate.now().toString()
    pagamento.id= fatura?.refCarrinhoCompraEntity?.id // como a fatura ainda não está persistida, não tem ID.
    pagamento.ipAddress =  InetAddress.getLocalHost().getHostAddress()
    pagamento.type= "CREDIT_CARD"
    pagamento.customer = CustomerDto()
    pagamento?.customer?.name = customerName
    pagamento?.customer?.phone = if (cartaoCredito.mesmosDadosMedico)  medico?.telefone else cartaoCredito.holderPhone
    pagamento?.customer?.email = if (cartaoCredito.mesmosDadosMedico)  medico?.user?.username else cartaoCredito.holderEmail
    pagamento?.customer?.cpf = cartaoCredito.holderCpf

    val gson = Gson()
    val jsonString = gson.toJson(pagamento)

    logger.info(jsonString)
    return jsonString
  }

  private fun requestPagamentoRecorrenteAsJSON(cartaoCredito: CartaoCreditoDto, recorrencia: Recorrencia): String {
    var medico = recorrencia.medico
    var customerName = ""
    var holderName = ""

    if (cartaoCredito.mesmosDadosMedico) {
      customerName = medico!!.user!!.nome!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
      holderName = cartaoCredito.cardHolderName!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
    } else {
      customerName = cartaoCredito.holderName!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
      holderName = cartaoCredito.cardHolderName!!.replace("[^A-zÀ-ú ]+".toRegex(), "")
    }

    var param = PagamentoRecorrenteDto()
    param.legalDocument = medico?.cpf
    param.category = "B2B"
    param.softDescriptor = medico!!.user?.nome

    val cardInfo = PagamentoRecorrenteCardInfoDto()

    cardInfo.cardNumber = cartaoCredito.number
    cardInfo.brandName = cartaoCredito.brandName
    cardInfo.expirationMonth = cartaoCredito.expirationMonth
    cardInfo.expirationYear = cartaoCredito.expirationYear
    cardInfo.securityCode = cartaoCredito.securityCode
    cardInfo.holderName = holderName
    cardInfo.legalDocument = medico.cpf

    val holder = HolderDto()
    holder.name = holderName
    holder.phone = if (cartaoCredito.mesmosDadosMedico)  medico?.telefone else cartaoCredito.holderPhone
    holder.email = if (cartaoCredito.mesmosDadosMedico)  medico?.user?.username else cartaoCredito.holderEmail
    holder.cpf =cartaoCredito.holderCpf

    val address = HolderAddressDto()

    address.type = "COMERCIAL"
    address.street = medico.logradouro
    address.number = "252"
    address.complement = "Complemento"
    address.district = "Bairro"
    address.zipCode = medico.cep
    address.city = "CAMPINAS"
    address.state = "SP"
    address.country = "BRASIL"


    holder.address = address
    cardInfo.holder = holder
    param.cardInfo = cardInfo

    val customer = CustomerDto()

    customer.name = customerName
    customer.phone = if (cartaoCredito.mesmosDadosMedico)  medico?.telefone else cartaoCredito.holderPhone
    customer.email = if (cartaoCredito.mesmosDadosMedico)  medico?.user?.username else cartaoCredito.holderEmail
    customer.cpf = cartaoCredito.holderCpf
    customer.ipAddress = InetAddress.getLocalHost().getHostAddress()

    val customerAddress = HolderAddressDto()

    customer.address = customerAddress

    customerAddress.type = "COMERCIAL"
    customerAddress.street = medico.logradouro
    customerAddress.number = "252"
    customerAddress.complement = ""
    customerAddress.district = ""
    customerAddress.zipCode = medico.cep
    customerAddress.city = "CAMPINAS"
    customerAddress.state = "SP"
    customerAddress.country = "BRASIL"

    param.customer = customer

    val charges : MutableList<ChargesItemDto> = mutableListOf()

    recorrencia.cobrancasProgramadas?.forEach {
      var chargeItem = ChargesItemDto()
      chargeItem.date = it.dataCobranca.toString()
      chargeItem.totalValue = String.format("%,.2f", it.valor).replace("[\\.\\,]".toRegex(),"")
      chargeItem.installment = it.sequenciaCobranca.toString()

      charges.add(chargeItem)
    }
    param.charges = charges

    val gson = Gson()
    val jsonString = gson.toJson(param)

    logger.info(jsonString)
    return jsonString

  }

}
/*
fun main(args : Array<String>) {
 // val pag = PagamentoService()
 // pag.oauthAuthentication()
 // println("Hello World")

  val command = "curl -X GET https://postman-echo.com/get?foo1=bar1&foo2=bar2"
  val processBuilder = ProcessBuilder(*command.split(" ".toRegex()).toTypedArray())

}
 */


