package osahner.service
import com.google.gson.Gson

import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import osahner.domain.*
import osahner.dto.*
import osahner.enums.CreditoTipo
import osahner.enums.SolicitacaoStatus
import osahner.enums.StatusFatura
import osahner.enums.StatusJusta
import osahner.repository.*
import osahner.repository.AgendaSalaRepository
import osahner.repository.FaturaRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class FaturaService(
  private val faturaRepository: FaturaRepository,
  private val agendaSalaRepository: AgendaSalaRepository,
  private val agendamentoRepository: AgendamentoRepository,
  private val agendaSalaTipoEquipamentoRepository: AgendaSalaTipoEquipamentoRepository,
  private val solicitacaoReservaRepository: SolicitacaoReservaRepository,
  private val solicitacaoEquipamentoRepository: SolicitacaoEquipamentoRepository,
  private val creditoRepository: CreditoRepository,
  private val creditoUtilizadoRepository: CreditoUtilizadoRepository,
  private val recorrenciaRepository: RecorrenciaRepository,
  private val cobrancaProgramadaRepository: CobrancaProgramadaRepository,
  private val custoOperacaoService: CustoOperacaoService,
  private val pagamentoService: PagamentoService

) {

  val mesAnoPattern  : String = "MM/yyyy"
  val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern(mesAnoPattern)

  fun findById(id: Long?): Optional<Fatura> = faturaRepository.findById(id)

  fun existsById(id: Long?): Boolean = faturaRepository.existsById(id)

  fun findByMedico(medico: Medico): Collection<Fatura>? = faturaRepository.findByMedicoOrderByDataCobranca(medico)

  fun   detalharFatura(fatura: Fatura): Any? {
    var faturaDTO = FaturaDetalhesDto(valor = fatura.valor, resumoCompraDetalhadoDTOS = mutableListOf<ResumoCompraDetalhadoDto>())

    if (! fatura.refAgendamentoEntities?.isEmpty()!!) {

      var agendasSalas = mutableListOf<AgendaSala>()

      fatura.refAgendamentoEntities?.map {
        agendasSalas.addAll(agendaSalaRepository.findByAgendamento(it))
      } ?: listOf<AgendaSala>()

      agendasSalas.groupBy { it.sala }.forEach { sala, agendasPorSala ->
        agendasPorSala.forEach {
          val custoEsquipamento = it.agendaSalaTipoEquipamentos?.filter { agte -> agte.fatura != null && agte.fatura == fatura  }?.sumByDouble { agte -> agte.valor!! } ?: 0.0
          var nomeEquipamentos = String()
          it.agendaSalaTipoEquipamentos?.filter { agte -> agte.fatura != null && agte.fatura == fatura  }?.forEachIndexed { index, it ->
            if (index == 0)
              nomeEquipamentos = nomeEquipamentos.plus(it.tipoEquipamento?.nome)
            else
              nomeEquipamentos = nomeEquipamentos.plus(" / ").plus(it.tipoEquipamento?.nome)
          }

          //val custoEsquipamento= it.agendaSalaTipoEquipamentos?.filter { agte -> agte.status != SolicitacaoStatus.CANCELADO || agte.valor != null || agte.valor!! > 0.0   }?.sumByDouble { agte -> agte.valor!! } ?: 0.0
          val total = it.custo!! + custoEsquipamento
          val resumoCompraDetalhadoDTO = ResumoCompraDetalhadoDto(
            sala = sala,
            data = it.inicio?.toLocalDate(),
            horaInicio = it.inicio?.toLocalTime(),
            horaFim = it.fim?.toLocalTime(),
            custo = it.custo,
            custoEquipamentos = custoEsquipamento,
            total = total,
            nomeEquipamentos = nomeEquipamentos
          )
          faturaDTO.resumoCompraDetalhadoDTOS?.add(resumoCompraDetalhadoDTO)
        }
      }
    }
    var agendasSalas2: List<AgendaSala> = fatura.agendaSalaTipoEquipamento?.map { it.agendaSala }?.filterNotNull()?.distinct()
      ?: listOf()

    agendasSalas2.groupBy { it.sala }.forEach { sala, agenda ->
      agenda.forEach{ ag ->

        val atual = faturaDTO.resumoCompraDetalhadoDTOS?.find { it.sala == sala && it.data?.isEqual(ag.inicio?.toLocalDate())!! && it.horaInicio == ag.inicio?.toLocalTime() && it.horaFim == ag.fim?.toLocalTime() }

        if (atual == null) {
          val custoEsquipamento = ag.agendaSalaTipoEquipamentos?.filter { agte -> agte.status == SolicitacaoStatus.CONFIRMADO }?.sumByDouble { agte -> agte.valor!! }
            ?: 0.0
          var nomeEquipamentos = String()
          ag.agendaSalaTipoEquipamentos?.filter { agte -> agte.status == SolicitacaoStatus.CONFIRMADO }?.forEachIndexed { index, it ->
            if (index == 0)
              nomeEquipamentos = nomeEquipamentos.plus(it.tipoEquipamento?.nome)
            else
              nomeEquipamentos = nomeEquipamentos.plus(" / ").plus(it.tipoEquipamento?.nome)
          }

          val resumoCompraDetalhadoDTO = ResumoCompraDetalhadoDto(
            sala = sala,
            data = ag.inicio?.toLocalDate(),
            horaInicio = ag.inicio?.toLocalTime(),
            horaFim = ag.fim?.toLocalTime(),
            custo = null,
            custoEquipamentos = custoEsquipamento,
            total = custoEsquipamento,
            nomeEquipamentos = nomeEquipamentos
          )
          faturaDTO.resumoCompraDetalhadoDTOS?.add(resumoCompraDetalhadoDTO)
        }
      }
    }

    faturaDTO.creditosUtilizados = fatura.creditoUtilizado
    faturaDTO.descontos = faturaDTO.creditosUtilizados?.sumByDouble { it.valor!! }
    faturaDTO.valorTotal = faturaDTO.resumoCompraDetalhadoDTOS?.sumByDouble { it.total!! }
    faturaDTO.dataCriada = fatura.dateCreated
    faturaDTO.dataCobranca = fatura.dataCobranca

    return faturaDTO
  }

  fun gerarCreditoOuCancelamento(agendamento : Agendamento) : ResumoCreditoDto {
    val resumo = ResumoCreditoDto()

    // cancelar equipamentos adicionados
    val agendaSalasTiposEquipamentos = agendaSalaTipoEquipamentoRepository.findAByStatusAndAgendaSalaIn(SolicitacaoStatus.CONFIRMADO, agendamento.agendaSalas)
    gerarCreditoOuCancelamentoList(agendaSalasTiposEquipamentos,agendamento)

    if (agendamento.refFaturaEntity != null) {
      var fatura = faturaRepository.findById(agendamento.refFaturaEntity?.id).get()
      resumo.fatura = fatura

      if (fatura.status == StatusFatura.EM_ABERTO) {
        fatura.valor = agendamento.custo?.let { fatura.valor?.minus(it) }

        if (fatura.valor!! < 0) {
          resumo.creditoRecuperado = Math.abs(fatura.valor!!)
          recuperarCredito(fatura)
        }

        val recorrencia = recorrenciaRepository.findByMedico(agendamento.medico)

        var cp = recorrencia?.cobrancasProgramadas?.find { it.dataCobranca!!.equals(fatura.dataCobranca) }
        if (cp != null) {
          if (fatura.valor!! <= 0) {
            //JUSTA
            pagamentoService.cancelarCobrancaProgramada(cp)

            cobrancaProgramadaRepository.delete(cp)

          } else {
            cp.valor = fatura.valor

            //JUSTA
            pagamentoService.atualizarCobrancaProgramada(recorrencia, cp)
            cobrancaProgramadaRepository.save(cp)
          }

          //cobrancaProgramadaRepository.save(cp)
        }

        fatura.refAgendamentoEntities?.remove(agendamento)
        faturaRepository.save(fatura)

        resumo.abatimento = agendamento.custo
      } else {
        val multaEstorno = custoOperacaoService.calcularMultaEstorno(agendamento)
        val valorAposEstorno = agendamento.custo?.minus(multaEstorno)
        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val credito = Credito()
        credito.agendamento = agendamento
        credito.medico = agendamento.medico
        credito.valorTotal = valorAposEstorno
        credito.valorDisponivel = valorAposEstorno
        credito.dateCreated = LocalDateTime.now(saoPauloZone)
        credito.lastUpdated = LocalDateTime.now(saoPauloZone)
        credito.tipo = CreditoTipo.CANCELAMENTO
        credito.version = 1

        creditoRepository.save(credito)

        resumo.creditoGanho = credito.valorTotal
      }
    }
    return resumo
  }

  fun aplicarCreditosExistentes(medico : Medico) : CreditoExistenteDto {
    val resumo = CreditoExistenteDto()
    val faturasNaoPagas = faturaRepository.findByMedicoAndStatusNotOrderByDataCobranca(medico, StatusFatura.PAGA)
    val creditos = listarCreditosDisponiveis(medico)

    while (faturasNaoPagas?.size!! > 0 && creditos.size > 0) {
      var fatura = faturasNaoPagas?.removeAt(0)
      var resumoFatura = ResumoFaturaDto()

      while (fatura.valor!! > 0 && creditos.size > 0) {
        var credito = creditos.get(0)
        var descontoMaximo = Math.min(credito.valorDisponivel!!, fatura.valor!!)
        var creditoUtilizado = fatura.creditoUtilizado?.find { it.id == credito.id }
        if (creditoUtilizado == null) {
          creditoUtilizado = CreditoUtilizado()
          creditoUtilizado.credito = credito
          creditoUtilizado.uuidTransacao =  System.currentTimeMillis()
        }

        fatura.valor = fatura.valor!! - descontoMaximo
        credito.valorDisponivel = credito.valorDisponivel!! - descontoMaximo
        resumo.creditoUtilizado = resumo.creditoUtilizado?.plus(descontoMaximo)

        if (creditoUtilizado.id == null) {
          creditoUtilizado.valor = descontoMaximo
        } else {
          creditoUtilizado.valor = creditoUtilizado.valor?.plus(descontoMaximo)
        }
        creditoUtilizado.fatura = fatura
        fatura.creditoUtilizado!!.add(creditoUtilizado)
        if (fatura.valor == 0.0) {
          fatura.status = StatusFatura.PAGA
        } else {
          fatura.status = StatusFatura.EM_ABERTO
        }


        if (credito.valorDisponivel == 0.0) {
          creditos.removeAt(0)
        }
      }


      if (fatura.valor != resumoFatura.antes) {
        resumoFatura.depois = fatura.valor
        resumo.faturas?.add(resumoFatura)
      }
    }

    return resumo
  }

  // @Transactional
  fun  gerar(carrinhoCompra: CarrinhoCompra, creditCard: CartaoCreditoDto) {

    var desconto = carrinhoCompra.desconto
    val hoje = LocalDate.now()
    val creditos = if (desconto!! > 0.0)  listarCreditosDisponiveis(carrinhoCompra.medico!!) else mutableListOf()


    var recorrenciaMedico : Recorrencia? =  recorrenciaRepository.findByMedico(carrinhoCompra.medico)

    val cobrancas : MutableList<CobrancaProgramada> = mutableListOf()
    var seq : Int = 1

    var faturas : MutableList<Fatura> = mutableListOf()

    var resumo = resumoCompraPorMes(carrinhoCompra)
    resumo.resumosMensais?.forEach {  it ->
      var agendamentoMesCorrente = it.mesAno!!.month == hoje.month && it.mesAno!!.year == hoje.year

      var dtCobranca : LocalDate =  if (agendamentoMesCorrente)  hoje else  it.mesAno!!.withDayOfMonth(1)
      var fatura = faturaRepository.findByDataCobrancaAndMedicoAndStatus(dtCobranca, carrinhoCompra.medico, StatusFatura.EM_ABERTO)
      val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
      if (fatura == null) {
        fatura =  Fatura()
        fatura.valor = it.custo
        fatura.dataCobranca = dtCobranca
        fatura.status = StatusFatura.EM_ABERTO
        fatura.medico = carrinhoCompra.medico
        fatura.refCarrinhoCompraEntity = carrinhoCompra
        fatura.idPagamento = UUID.randomUUID().toString()
        fatura.lastUpdated= LocalDateTime.now(saoPauloZone)
        fatura.dateCreated = LocalDateTime.now(saoPauloZone)
        fatura.version = 1
        faturaRepository.save(fatura)
      } else {
        fatura.lastUpdated= LocalDateTime.now(saoPauloZone)
        fatura.valor = it.custo?.let { it1 -> fatura.valor?.plus(it1) }
      }

      it.agendamentos?.forEach { ag ->
        fatura.refAgendamentoEntities?.add(ag)
        ag.refFaturaEntity = fatura
      }

      it.equipamentos.forEach {
        fatura.agendaSalaTipoEquipamento?.add(it)
        it.fatura = fatura
      }

      while (desconto > 0 && fatura.valor!! > 0 && creditos.size > 0) {
        var credito = creditos.removeAt(0)
        var descontoMaximo = credito.valorDisponivel?.let { it1 -> Math.min(it1, fatura.valor!!) }?.let { it2 -> Math.min(desconto, it2) }

        var creditoUtilizado = fatura.creditoUtilizado?.find{ it.id == credito.id }
        if (creditoUtilizado == null) {
          creditoUtilizado = CreditoUtilizado()
          creditoUtilizado.credito = credito
          creditoUtilizado.fatura = fatura
        }


        if (descontoMaximo != null) {
          desconto -= descontoMaximo
          fatura.valor = fatura.valor!! - descontoMaximo
          credito.valorDisponivel = credito.valorDisponivel?.minus(descontoMaximo)

          if (creditoUtilizado.id == null) {
            creditoUtilizado.valor = descontoMaximo
          } else {
            creditoUtilizado.valor = creditoUtilizado.valor?.plus(descontoMaximo)
          }

        }


        fatura.creditoUtilizado?.add(creditoUtilizado)
        faturaRepository.save(fatura)

      }

      if (agendamentoMesCorrente) {
        //JUSTA
        if (fatura.valor!! > 0)
          pagamentoService.pagar(fatura, creditCard)

        //fatura.status = StatusFatura.PAGA

        if (recorrenciaMedico == null) {
          recorrenciaMedico = Recorrencia()
          recorrenciaMedico?.medico = carrinhoCompra.medico
          recorrenciaMedico?.descricao = "Recorrencia " + (carrinhoCompra.medico?.toCustomDTO()?.nome ?: "")
          recorrenciaMedico?.version = 1

          //JUSTA
          var resposta = pagamentoService.criarPagamentoRecorrente(recorrenciaMedico!!, creditCard)
             recorrenciaMedico!!.justaRecorrenciaId = resposta.id


          recorrenciaRepository.save(recorrenciaMedico)
        }

        // alterar status para paga
      } else {
        var c =  CobrancaProgramada()
        c.dataCobranca = fatura.dataCobranca?.year?.let { it1 -> LocalDate.of(it1, fatura.dataCobranca?.month, fatura.dataCobranca!!.dayOfMonth) }
        c.valor = fatura.valor
        c.sequenciaCobranca = seq++
        c.fatura = fatura
        cobrancas.add(c)
      }

      faturas.add(fatura)
    }

    if (desconto > 0) {
      //log.warn("O desconto calculado para o carrinho ${carrinhoCompra.id} não foi completamente aplicado. Restaram ${desconto} reais")
    }

    faturaRepository.saveAll(faturas)

    if (!cobrancas.isEmpty()) {
      if (recorrenciaMedico == null) {
        var  recorrenciaMedico = Recorrencia()
        recorrenciaMedico.medico = carrinhoCompra.medico
        recorrenciaMedico.descricao = "Recorrencia " + (carrinhoCompra.medico?.toCustomDTO()?.nome ?: "")
        recorrenciaMedico.cobrancasProgramadas?.addAll(cobrancas)
        recorrenciaMedico.cobrancasProgramadas?.forEach{
          it.recorrencia = recorrenciaMedico
        }
        recorrenciaMedico.version = 1

        //JUSTA
        var resposta = pagamentoService.criarPagamentoRecorrente(recorrenciaMedico, creditCard)
           recorrenciaMedico.justaRecorrenciaId = resposta.id


        recorrenciaMedico.cobrancasProgramadas?.forEach { c ->
              var idx = c.sequenciaCobranca!!.minus(1)

              var charge = resposta.charges?.get(idx)
              c.recorrencia = recorrenciaMedico
              c.justaCobrancaId = charge?.id
          }

        recorrenciaRepository.save(recorrenciaMedico)
      } else {
        cobrancas.forEach { cp ->
          var cpMedico : CobrancaProgramada? = null
          if (!recorrenciaMedico!!.cobrancasProgramadas.isNullOrEmpty()) {
            cpMedico = recorrenciaMedico!!.cobrancasProgramadas?.get(cp.sequenciaCobranca!! - 1)
          }
          if (cpMedico == null) {
            cp.recorrencia = recorrenciaMedico

            //JUSTA
            var resposta = pagamentoService.criarCobrancaProgramada(recorrenciaMedico!!, cp)

            var idx = cp.sequenciaCobranca?.minus(1)

            var cpAtualizada = idx?.let { resposta.charges?.get(it) }

             cp.justaCobrancaId = cpAtualizada?.id


            cobrancaProgramadaRepository.save(cp)
          } else {
            cpMedico?.valor = cp.valor
            //JUSTA
            var resposta = pagamentoService.atualizarCobrancaProgramada(recorrenciaMedico, cpMedico)
            cobrancaProgramadaRepository.save(cpMedico)

          }
        }
      }
    }

  }

  fun gerarCreditoOuCancelamentoList(agendaSalaTipoEquipamentos : List<AgendaSalaTipoEquipamento>,agendamento : Agendamento ) : ResumoCreditoDto{
    val resumo = ResumoCreditoDto()
    resumo.creditoGanho = 0.0
    val confirmados = agendaSalaTipoEquipamentos.filter { it.status == SolicitacaoStatus.CONFIRMADO }

    confirmados.groupBy { it.fatura }.forEach { fatura, agendamentoEquipamentosPorFatura ->
      val custoAgendamento = agendamentoEquipamentosPorFatura.sumByDouble { it.valor!! }
      val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
      if (fatura?.status == StatusFatura.PAGA) {
        val credito = Credito()
        credito.valorTotal = custoAgendamento
        credito.valorDisponivel = custoAgendamento
        credito.creditoAgendaSalaTipoEquipamento = mutableListOf()
        credito.version = 1
        credito.dateCreated = LocalDateTime.now(saoPauloZone)
        credito.lastUpdated = LocalDateTime.now(saoPauloZone)
        credito.tipo = CreditoTipo.CANCELAMENTO
        credito.agendamento = agendamento

        agendamentoEquipamentosPorFatura.forEach {
          var creditoAgenda = CreditoAgendaSalaTipoEquipamento()
          creditoAgenda.credito = credito
          creditoAgenda.agendaSalaTipoEquipamento = it
          credito.creditoAgendaSalaTipoEquipamento!!.add(creditoAgenda)
        }
        credito.medico = fatura.medico
        creditoRepository.save(credito)
        resumo.fatura = fatura
        resumo.creditoGanho = resumo.creditoGanho?.plus(credito.valorTotal!!)

      } else {
        fatura?.valor = fatura?.valor?.minus(custoAgendamento)

        if (fatura?.valor!! < 0.0) {
          recuperarCredito(fatura)
        }

        agendamentoEquipamentosPorFatura.forEach{
          fatura.agendaSalaTipoEquipamento?.remove(it)
        }
      }
    }
    return resumo
  }

  private fun recuperarCredito(fatura : Fatura) {
    if (fatura?.valor!! >= 0)
      return

    var creditoARecuperar = Math.abs(fatura.valor!!)
    var creditosARemover : MutableList<CreditoUtilizado> = mutableListOf()

    var i = 0

    while (i < fatura.creditoUtilizado?.size!! && creditoARecuperar > 0) {
      var creditoUtilizado = fatura.creditoUtilizado?.toList()?.get(i)
      var diferenca = creditoUtilizado?.valor?.minus(creditoARecuperar)

      if (diferenca != null && diferenca > 0.0) {
        creditoUtilizado?.valor = diferenca
        creditoUtilizado?.credito?.valorDisponivel = creditoUtilizado?.credito?.valorDisponivel?.plus(creditoARecuperar)
        creditoARecuperar = 0.0
      } else {
        creditoARecuperar -= creditoUtilizado?.valor!!
        creditoUtilizado.credito?.valorDisponivel = creditoUtilizado.credito?.valorDisponivel?.plus(creditoUtilizado.valor!!)
        creditosARemover.add(creditoUtilizado)
      }
      i++
    }

    creditosARemover.forEach {
      creditoUtilizadoRepository.delete(it)
      fatura.creditoUtilizado!!.remove(it)
    }

    fatura.valor = 0.0
  }



  fun listarCreditosDisponiveis(medico : Medico): MutableList<Credito> {
    return  creditoRepository.findByValorDisponivelGreaterThanAndMedicoOrderByDateCreated( 0.0,medico)

  }

  fun resumoCompraPorMes(carrinhoCompra : CarrinhoCompra) : ResumoCompraDto {
    val resumoCompraDTO =  ResumoCompraDto()
    resumoCompraDTO.valor = carrinhoCompra.valor
    resumoCompraDTO.desconto = carrinhoCompra.desconto

    val desconto = carrinhoCompra.desconto

    // custo agendamento de salas por mês
    val solicitacaoReservas = solicitacaoReservaRepository.findByRefCarrinhoCompraEntityAndStatus(carrinhoCompra,SolicitacaoStatus.PENDENTE)
    val agendamentos : MutableList<Agendamento> = if (!solicitacaoReservas.isEmpty())  agendamentoRepository.findAllByStatusAndSolicitacaoReservaIn(SolicitacaoStatus.PENDENTE,solicitacaoReservas ) else mutableListOf()

    agendamentos.groupBy { it.dia?.format(DateTimeFormatter.ofPattern(mesAnoPattern)) }.forEach{ mesAno, agendamentosMes ->
      var primeiroDiaMes = mesAno?.split('/')?.get(1)?.toInt()?.let { LocalDate.of(it, mesAno?.split('/')?.get(0)?.toInt(), 1) }
      var resumoMes =  ResumoMensalDto()
      resumoMes.mesAno = primeiroDiaMes
      resumoMes.custo = agendamentosMes.sumByDouble{ it.custo }
      resumoMes.agendamentos?.addAll(agendamentosMes)
      resumoCompraDTO.resumosMensais?.add(resumoMes)
    }

    // custo de equipamentos agendados por mês
    var solicitacaoEquipamentos = solicitacaoEquipamentoRepository.findByRefCarrinhoCompraEntityAndStatus(carrinhoCompra,SolicitacaoStatus.PENDENTE)
    var equipamentos : MutableList<AgendaSalaTipoEquipamento> = if (!solicitacaoEquipamentos.isEmpty())  agendaSalaTipoEquipamentoRepository.findAllByStatusAndSolicitacaoEquipamentoIn(SolicitacaoStatus.PENDENTE,solicitacaoEquipamentos) else mutableListOf()

    equipamentos.groupBy { it.agendaSala?.inicio?.format(DateTimeFormatter.ofPattern(mesAnoPattern)) }.forEach { mesAno, equipamentosMes ->

      var primeiroDiaMes = mesAno?.split('/')?.get(1)?.toInt()?.let { LocalDate.of(it, mesAno?.split('/')?.get(0)?.toInt(), 1) }

      var resumoMensalDTO = resumoCompraDTO.resumosMensais.find { it.mesAno!!.isEqual(primeiroDiaMes) }

      if (resumoMensalDTO == null) {
        resumoMensalDTO = ResumoMensalDto()
        resumoMensalDTO.mesAno = primeiroDiaMes
        equipamentosMes.forEach {
          resumoMensalDTO.custo = resumoMensalDTO.custo?.plus(it.valor!!)
        }
        resumoMensalDTO.equipamentos?.addAll(equipamentosMes)
        resumoCompraDTO.resumosMensais.add(resumoMensalDTO)
      } else {
        equipamentosMes.forEach {
          resumoMensalDTO.custo = resumoMensalDTO.custo?.plus(it.valor!!)
        }
        resumoMensalDTO.equipamentos.addAll(equipamentosMes)
      }
    }

    return resumoCompraDTO
  }

  // @Transactional
  fun gerar(carrinhoCompra: CarrinhoCompra) {

    var desconto = carrinhoCompra.desconto
    val hoje = LocalDate.now()
    val creditos = if (desconto!! > 0.0)  listarCreditosDisponiveis(carrinhoCompra.medico!!) else mutableListOf()

    var recorrenciaMedico : Recorrencia? =  recorrenciaRepository.findByMedico(carrinhoCompra.medico)

    if (recorrenciaMedico == null) {
      throw RuntimeException("Não existe recorrência cadastrada para o médico na JUSTA !")
    }

    val cobrancas : MutableList<CobrancaProgramada> = mutableListOf()
    var seq : Int = 1

    var faturas : MutableList<Fatura> = mutableListOf()

    var resumo = resumoCompraPorMes(carrinhoCompra)
    resumo.resumosMensais?.forEach {  it ->
      var agendamentoMesCorrente = it.mesAno!!.month == hoje.month && it.mesAno!!.year == hoje.year

      var dtCobranca : LocalDate =  LocalDate.now().plusDays(1)// .plit.mesAno!!.withDayOfMonth(1)
      var fatura = faturaRepository.findByDataCobrancaAndMedicoAndStatus(dtCobranca,carrinhoCompra.medico, StatusFatura.EM_ABERTO)
      //if (fatura == null) {
        //dtCobranca = it.mesAno!!.withDayOfMonth(1).plusMonths(1)
        //fatura = faturaRepository.findByDataCobrancaAndMedicoAndStatus(dtCobranca, carrinhoCompra.medico, StatusFatura.EM_ABERTO)
      //}

      val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
      if (fatura == null) {
        fatura =  Fatura()
        fatura.valor = it.custo
        fatura.dataCobranca = dtCobranca
        fatura.status = StatusFatura.EM_ABERTO
        fatura.medico = carrinhoCompra.medico
        fatura.refCarrinhoCompraEntity = carrinhoCompra
        fatura.idPagamento = UUID.randomUUID().toString()
        fatura.lastUpdated= LocalDateTime.now(saoPauloZone)
        fatura.dateCreated = LocalDateTime.now(saoPauloZone)
        fatura.version = 1
        faturaRepository.save(fatura)
      } else {
        fatura.lastUpdated= LocalDateTime.now(saoPauloZone)
        fatura.valor = it.custo?.let { it1 -> fatura.valor?.plus(it1) }
      }

      it.agendamentos?.forEach { ag ->
        fatura.refAgendamentoEntities?.add(ag)
        ag.refFaturaEntity = fatura
      }

      it.equipamentos.forEach {
        fatura.agendaSalaTipoEquipamento?.add(it)
        it.fatura = fatura
      }

      while (desconto > 0 && fatura.valor!! > 0 && creditos.size > 0) {
        var credito = creditos.removeAt(0)
        var descontoMaximo = credito.valorDisponivel?.let { it1 -> Math.min(it1, fatura.valor!!) }?.let { it2 -> Math.min(desconto, it2) }

        var creditoUtilizado = fatura.creditoUtilizado?.find{ it.id == credito.id }
        if (creditoUtilizado == null) {
          creditoUtilizado = CreditoUtilizado()
          creditoUtilizado.credito = credito
          creditoUtilizado.fatura = fatura
        }


        if (descontoMaximo != null) {
          desconto -= descontoMaximo
          fatura.valor = fatura.valor!! - descontoMaximo
          credito.valorDisponivel = credito.valorDisponivel?.minus(descontoMaximo)

          if (creditoUtilizado.id == null) {
            creditoUtilizado.valor = descontoMaximo
          } else {
            creditoUtilizado.valor = creditoUtilizado.valor?.plus(descontoMaximo)
          }

        }


        fatura.creditoUtilizado?.add(creditoUtilizado)
        faturaRepository.save(fatura)

      }


      var c =  CobrancaProgramada()
      c.dataCobranca = fatura.dataCobranca?.year?.let { it1 -> LocalDate.of(it1, fatura.dataCobranca?.month, fatura.dataCobranca!!.dayOfMonth) }
      c.valor = fatura.valor
      c.sequenciaCobranca = seq++
      c.fatura = fatura
      cobrancas.add(c)

      faturas.add(fatura)
    }

    if (desconto > 0) {
      //log.warn("O desconto calculado para o carrinho ${carrinhoCompra.id} não foi completamente aplicado. Restaram ${desconto} reais")
    }

    faturaRepository.saveAll(faturas)

    if (!cobrancas.isEmpty()) {

      cobrancas.forEach { cp ->
        var cpMedico : CobrancaProgramada? = null
        if (!recorrenciaMedico!!.cobrancasProgramadas.isNullOrEmpty()) {
          cpMedico = recorrenciaMedico!!.cobrancasProgramadas?.get(cp.sequenciaCobranca!! - 1)
        }
        if (cpMedico == null) {
          cp.recorrencia = recorrenciaMedico

          //JUSTA
          var resposta = pagamentoService.criarCobrancaProgramada(recorrenciaMedico, cp)

          var idx = cp.sequenciaCobranca?.minus(1)

          var cpAtualizada = idx?.let { resposta.charges?.get(it) }

          cp.justaCobrancaId = cpAtualizada?.id


          cobrancaProgramadaRepository.save(cp)
        } else {
          cpMedico?.valor = cp.valor
          //JUSTA
          var resposta = pagamentoService.atualizarCobrancaProgramada(recorrenciaMedico, cp)
          cobrancaProgramadaRepository.save(cpMedico)

        }

      }
    }
  }

  /*
   Valores do Status JUSTA:
   VALIDATED,
   PROCESSING,
   AUTHORIZED,
   NOT_AUTHORIZED,
   REQUESTED_CANCELLATION,
   CANCELED,
   GENERATED,
   ERROR
   */
  fun  atualizarStatusFatura( idTransacaoJusta: String?, authorizationCode: String?, status: StatusJusta) {
    println("Atualiza Status da Fatura")

    var cp: CobrancaProgramada  = cobrancaProgramadaRepository.findByJustaCobrancaId(idTransacaoJusta).get()
    if (cp == null) {
      println("Cobrança não encontrada com o número de ordem: " + idTransacaoJusta + " - authorizationCode: " + authorizationCode)
      return;
    }
    var fatura: Fatura? = cp.fatura
    if (status == StatusJusta.AUTHORIZED) {
      fatura?.status = StatusFatura.PAGA
      fatura?.uuidTransacao = authorizationCode
    } else
      if (status == StatusJusta.NOT_AUTHORIZED ||
        status == StatusJusta.ERROR) {
        fatura?.status = StatusFatura.INCONSISTENCIA_PGTO
      } else
        if (status == StatusJusta.CANCELED) {
          fatura?.status = StatusFatura.CANCELADA
        } else
          return;
    faturaRepository.save(fatura)

  }
}
