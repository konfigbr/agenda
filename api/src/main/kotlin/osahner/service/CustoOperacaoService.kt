package osahner.service

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import osahner.domain.*
import osahner.dto.CustoDescontoDto
import osahner.enums.CreditoTipo
import osahner.enums.PeriodoAgendamento
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoDesconto
import osahner.repository.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class CustoOperacaoService(
        private val agendamentoService: AgendamentoService,
        private val solicitacaoReservaRepository: SolicitacaoReservaRepository,
        private val agendaSalaRepository: AgendaSalaRepository,
        private val creditoRepository: CreditoRepository,
        private val medicoRepository: MedicoRepository,
        private val solicitacaoEquipamentoRepository: SolicitacaoEquipamentoRepository,
        private val faixaPrecoRepository: FaixaPrecoRepository,
        private val agendaSalaTipoEquipamentoRepository: AgendaSalaTipoEquipamentoRepository,
        private val agendamentoRepository: AgendamentoRepository,
        private val descontoService: DescontoService

) {

    fun calcular(carrinhoCompra: CarrinhoCompra): Double? {
        val custoEDesconto = this.calcularCusto(carrinhoCompra)

        carrinhoCompra.custo = custoEDesconto.custo
        carrinhoCompra.desconto = this.calcularDesconto(carrinhoCompra)
        carrinhoCompra.valor = carrinhoCompra.custo!! - carrinhoCompra.desconto!!

        return custoEDesconto.descontoTotal
    }

    fun calcularDesconto(carrinhoCompra: CarrinhoCompra): Double? {
        val creditoDisponivel = calcularCredito(carrinhoCompra.medico?.id)

        if (creditoDisponivel >= carrinhoCompra.custo!!)
            return carrinhoCompra.custo
        else
            return creditoDisponivel
    }

    private fun calcularCusto(carrinhoCompra: CarrinhoCompra): CustoDescontoDto {
        var custo = 0.0
        val medico = carrinhoCompra.medico
        var descontoTotal = 0.00

        // val solicitacoes = carrinhoCompra.solicitacoesReserva solicitacaoReservaRepository.findByRefCarrinhoCompraEntityAgendamentos(carrinhoCompra).filter { it.status != SolicitacaoStatus.CANCELADO }

        val solicitacoes = carrinhoCompra.solicitacoesReserva?.filter { it.status != SolicitacaoStatus.CANCELADO  }
        // def agendamentos = (solicitacoes) ? Agendamento.findAllByStatusNotEqualAndSolicitacaoReservaInList(SolicitacaoStatus.CANCELADO, solicitacoes) : []


        var agendamentos = listOf<Agendamento>()
        if (solicitacoes?.isNotEmpty()!!)
            agendamentos = agendamentoService.findAllByStatusNotLikeAndSolicitacaoReservaIn(SolicitacaoStatus.CANCELADO, solicitacoes)

        agendamentos.groupBy { it.dia?.format(DateTimeFormatter.ofPattern("MM/yyyy")) }.forEach { mesAno, agendamentosMes ->
            val dia1 = LocalDate.of(mesAno?.split('/')?.get(1)?.toInt()!!, mesAno?.split('/')?.get(0).toInt(), 1)
            val inicioMes = dia1.atStartOfDay()
            val fimMes = dia1.plusDays((dia1.lengthOfMonth() - 1).toLong()).atTime(23, 59)

            val mapAgendamentoHoras = agendamentoService.calcularHorasAgendadas(medico?.id!!, inicioMes, fimMes)

            var totalHorasPagas = 0
            mapAgendamentoHoras.values.forEach{
                totalHorasPagas = totalHorasPagas +  it
            }

            val totalHorasSolicitadas = agendamentosMes.sumBy { ag -> ag.agendaSalas?.size!! }

            val totalHoras = totalHorasPagas + totalHorasSolicitadas

            /* FAIXA PREÇO ANTIGA
            val faixaPreco = faixaPrecoRepository.findByTotalHorasLessThanEqualOrderByTotalHorasDesc(totalHoras).first()
            var valor = faixaPreco?.valor
            */

            val agendas : MutableList<Agendamento> = mutableListOf()
            agendas.addAll(agendamentosMes)
            //agendas.addAll(mapAgendamentoHoras.keys.toMutableList())

            agendas.forEach { ag ->
                var custoCorrente = ag.custo

                //BUSCA DESCONTO
                val descontos = descontoService.findVigenteAtivosByProfissionalAndDate(medico,ag.agendaSalas?.first()?.inicio!!)

                val descPeriodo = descontos.find { it.tipo == TipoDesconto.PERIODO }?.percentual?.div(100.00)
                val descHoraAvulsa = descontos.find { it.tipo == TipoDesconto.HORA_AVULSA }?.percentual?.div(100.00)

                //BUSCAR FAIXA PRECO PELA ESPECIALIDADE DA SALA
                var faixaPrecoList = faixaPrecoRepository
                        .findByTabelaPrecoAndTotalHorasGreaterThanEqualOrderByTotalHorasAsc(ag.agendaSalas?.first()?.sala?.refEspecialidadeEntity?.tabelaPreco!!, totalHoras)

                if (faixaPrecoList.isEmpty())
                    faixaPrecoList = faixaPrecoRepository
                            .findByTabelaPrecoAndTotalHorasLessThanEqualOrderByTotalHorasDesc(ag.agendaSalas?.first()?.sala?.refEspecialidadeEntity?.tabelaPreco!!, totalHoras)

                val faixaPreco = faixaPrecoList.first()

                // Calcula o custo
                if (ag.agendaSalas!!.size <= 2 && custoCorrente == 0.0){

                    val faixaPrecoNova = faixaPrecoRepository
                            .findByTabelaPrecoAndTotalHoras(ag.agendaSalas?.first()?.sala?.refEspecialidadeEntity?.tabelaPreco!!, ag.agendaSalas!!.size)

                    ag.agendaSalas?.forEach {
                        it.custo = if(descHoraAvulsa != null) faixaPrecoNova?.valor?.minus(faixaPrecoNova?.valor!! * descHoraAvulsa) else faixaPrecoNova?.valor
                        descontoTotal += if(descHoraAvulsa != null) faixaPrecoNova?.valor!! * descHoraAvulsa else 0.00
                    }
                    ag.custo = ag.agendaSalas?.sumByDouble { it -> it.custo!! }!!

                }else if ((custoCorrente == 0.0 || custoCorrente != faixaPreco.valor) && ag.agendaSalas!!.size > 2) {
                    ag.agendaSalas?.forEach {

                        it.custo = if(descPeriodo != null) faixaPreco?.valor?.minus(faixaPreco?.valor!! * descPeriodo) else faixaPreco?.valor
                        descontoTotal += if(descPeriodo != null) faixaPreco?.valor!! * descPeriodo else 0.00
                    }
                    ag.custo = ag.agendaSalas?.sumByDouble { it -> it.custo!! }!!
                    custoCorrente = ag.custo

                }
                //ag.solicitacaoReserva?.custo = ag.solicitacaoReserva?.custo + ag.custo

                //gerarCredito(ag, custoCorrente, faixaPreco)
            }
        }

        solicitacoes.forEach { sol ->
            sol.custo = sol.refAgendamentoEntities?.sumByDouble { it.custo!! } ?: 0.0
        }

        custo = custo + (solicitacoes.sumByDouble { sol -> sol.custo!! } ?: 0.0)

        val solicitacoesEquipamentos = carrinhoCompra.solicitacoesEquipamento?.filter { it.status != SolicitacaoStatus.CANCELADO }


        solicitacoesEquipamentos?.forEach {
            var agendaSalasTiposEquipamentos = agendaSalaTipoEquipamentoRepository.findBySolicitacaoEquipamento(it)

            agendaSalasTiposEquipamentos.forEach {agte ->
                it.custo = it.custo!! + agte.valor!!  ?: 0.0
            }

        }
        custo = custo + (solicitacoesEquipamentos?.sumByDouble { sol -> sol.custo!! } ?:  0.0)

        return CustoDescontoDto(custo = custo, descontoTotal = descontoTotal)
    }

    fun calcularMultaEstorno(agendamento: Agendamento): Double {

        val dia1 = agendamento.dia?.withDayOfMonth(1)
        val inicioMes = dia1?.withDayOfMonth(1)?.atStartOfDay()
        val fimMes = dia1?.plusDays((dia1.lengthOfMonth()-1).toLong())?.atTime(23, 59)

        val agendamentosMes = agendamentoRepository.findByIdNotEqualAndMedicoAndDiaBetween(agendamento.id, agendamento.medico, inicioMes?.toLocalDate(), fimMes?.toLocalDate(), SolicitacaoStatus.CONFIRMADO)
        val totalHoras = agendamentosMes.sumBy { it.agendaSalas!!.size } ?: 0

        var multaEstorno = 0.0

        if (!agendamentosMes.isEmpty()) {
            /* FAIXA PREÇO ANTIGA
                val faixaPreco = if (totalHoras > 0) faixaPrecoRepository.findByTotalHorasLessThanEqualOrderByTotalHorasDesc(totalHoras).first() else faixaPrecoRepository.findByTotalHoras(1)
                var agendasSala = agendaSalaRepository.findByCustoLessThanAndAgendamentoIn(faixaPreco.valor, agendamentosMes)*/

            var agendasSala = agendaSalaRepository.findByAgendamentoIn(agendamentosMes.toMutableList())

            agendasSala?.forEach {
                //BUSCAR FAIXA PRECO PELA ESPECIALIDADE DA SALA
                var faixaPreco: FaixaPreco? = null
                var faixaPrecoCusto = 0.0
                if (totalHoras > 0){
                    var faixaPrecoList = faixaPrecoRepository
                            .findByTabelaPrecoAndTotalHorasGreaterThanEqualOrderByTotalHorasAsc(it.sala?.refEspecialidadeEntity?.tabelaPreco!!, totalHoras)

                    if (faixaPrecoList.isEmpty())
                        faixaPrecoList = faixaPrecoRepository
                                .findByTabelaPrecoAndTotalHorasLessThanEqualOrderByTotalHorasDesc(it.sala?.refEspecialidadeEntity?.tabelaPreco!!, totalHoras)

                    faixaPreco = faixaPrecoList.first()

                } else
                    faixaPreco = faixaPrecoRepository.findByTabelaPrecoAndTotalHoras(it.sala?.refEspecialidadeEntity?.tabelaPreco!!,1)

                //BUSCA DESCONTO
                val descontos = descontoService.findVigenteAtivosByProfissionalAndDate(agendamento.medico!!, it.inicio!!)

                val descPeriodo = descontos.find { it.tipo == TipoDesconto.PERIODO }?.percentual?.div(100.00)
                val descHoraAvulsa = descontos.find { it.tipo == TipoDesconto.HORA_AVULSA }?.percentual?.div(100.00)

                if (it.agendamento?.agendaSalas?.size!! <= 2)
                    faixaPrecoCusto = (if(descHoraAvulsa != null) faixaPreco?.valor?.minus(faixaPreco?.valor!! * descHoraAvulsa) else faixaPreco?.valor)!!
                else
                    faixaPrecoCusto = (if(descPeriodo != null) faixaPreco?.valor?.minus(faixaPreco?.valor!! * descPeriodo) else faixaPreco?.valor)!!


                if (it.custo!! < faixaPrecoCusto) {
                    multaEstorno += (it.custo?.let { it1 -> faixaPrecoCusto.minus(it1) }!!)
                    it.custo = faixaPrecoCusto
                }
            }
        }

        return multaEstorno
    }


    @Transactional(readOnly = true)
    fun calcularCredito(idUser: Long?): Double {
        val medico = medicoRepository.findById(idUser).get()

        val creditos = creditoRepository.findAByValorDisponivelGreaterThanAndMedico( 0.0,medico)

        return creditos.sumByDouble { it.valorDisponivel!! } ?: 0.0
    }

    fun gerarCredito (ag: Agendamento, custoCorrente: Double, faixaPreco: Double){
        var creditosGerados = creditoRepository.findByAgendamentoAndTipo(ag,CreditoTipo.HORAS).filter { it.creditoAgendaSalaTipoEquipamento.isEmpty() }

        var totalCreditos : Double =  0.0
        creditosGerados.forEach {
            totalCreditos = totalCreditos + it.valorTotal!!
        }

        var totalHorasAgendamento : Int?  = ag.agendaSalas?.size

        var totalValorAgendamento : Double  = custoCorrente

        var custoNaFaixa : Double  = (totalHorasAgendamento!! * faixaPreco)

        var diff : Double =  totalValorAgendamento - custoNaFaixa

        if (diff.toDouble() > totalCreditos) {
            // Gerar credito com a diferença
            var diffCredito = diff.minus(totalCreditos)
            val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
            val credito = Credito()
            credito.valorTotal = diffCredito
            credito.valorDisponivel = diffCredito
            credito.version = 1
            credito.dateCreated = LocalDateTime.now(saoPauloZone)
            credito.lastUpdated = LocalDateTime.now(saoPauloZone)
            credito.agendamento = ag
            credito.medico = ag.medico
            credito.tipo = CreditoTipo.HORAS
            creditoRepository.save(credito)
        }
    }
}