package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.AgendaSala
import osahner.domain.AgendaSalaTipoEquipamento
import osahner.domain.TipoEquipamento
import osahner.dto.AgendaSalaTipoEquipamentoDto
import osahner.dto.EquipamentosDisponiveisDto
import osahner.dto.HorarioDisponivelDto
import osahner.dto.TipoEquipamentoDto
import osahner.enums.SolicitacaoStatus
import osahner.repository.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.HashMap

@Component
class SolicitacaoEquipamentoService(
  private val solicitacaoEquipamentoRepository: SolicitacaoEquipamentoRepository,
  private val agendaSalaRepository: AgendaSalaRepository,
  private val ativoRepository: AtivoRepository,
  private val agendaSalaTipoEquipamentoRepository: AgendaSalaTipoEquipamentoRepository,
  private val tipoEquipamentoRepository: TipoEquipamentoRepository) {

 fun listarTiposEquipamentosDisponiveis(inicio : LocalDateTime?, fim: LocalDateTime?): MutableList<EquipamentosDisponiveisDto?> {

    var equipamentos = mutableListOf<EquipamentosDisponiveisDto?>()
    var horarios = mutableListOf<String>()
    horarios.add(inicio?.toLocalTime().toString())
    horarios.add(fim?.toLocalTime().toString())
    var tipoEquipamentos = tipoEquipamentoRepository.findTipoEquipamentoWithAtivo()
    var mapaTipoEquipamentoHorariosDisponiveis: HashMap<TipoEquipamento, MutableList<HorarioDisponivelDto?>> = hashMapOf()
    var agendaSalas = agendaSalaRepository.findByInicioAndFimOrderByInicioAsc(inicio, fim)
    var horarioDisponivelDto = HorarioDisponivelDto(inicio?.toLocalTime(), fim?.toLocalTime())
    var horariosDisponiveisDto = mutableListOf<HorarioDisponivelDto?>()
        horariosDisponiveisDto.add(horarioDisponivelDto)

      tipoEquipamentos.forEach{
      mapaTipoEquipamentoHorariosDisponiveis?.put(it, horariosDisponiveisDto)
    }

    tipoEquipamentos.forEach{  tipoEquipamento ->

      var totalAtivosPorTipoEquipamento: Long = ativoRepository.countByTipoEquipamento(tipoEquipamento)
      //verificar
      var agendaPorTipoEquipamento = agendaSalaTipoEquipamentoRepository.findAllByTipoEquipamentoAndAgendaSalaInListAndStatusNotEqual(SolicitacaoStatus.CANCELADO, tipoEquipamento, agendaSalas)

      agendaPorTipoEquipamento?.forEach { agendaSalaTipoEquipamento ->
        var agendaSalaComEquipamento = agendaSalaTipoEquipamento.agendaSala
        var totalUsado: Int = 0
        var agendaSalasMesmoHorario = agendaSalas?.filter { it.inicio == agendaSalaComEquipamento?.inicio && it.fim == agendaSalaComEquipamento?.fim }

        agendaSalasMesmoHorario?.forEach{ag->

          if(ag.agendaSalaTipoEquipamentos?.count { it.tipoEquipamento == tipoEquipamento}!! != 0){
            totalUsado += ag.agendaSalaTipoEquipamentos?.count { it.tipoEquipamento == tipoEquipamento}!!
          }else{
            totalUsado +=0
          }

        }

        if (totalUsado >=  totalAtivosPorTipoEquipamento){
          var horariosDisponiveis: MutableCollection<HorarioDisponivelDto?>? = mapaTipoEquipamentoHorariosDisponiveis?.get(tipoEquipamento)
          var horaFilter = horariosDisponiveis?.filter { it?.inicio!! <= agendaSalaComEquipamento?.inicio?.toLocalTime() && agendaSalaComEquipamento?.fim?.toLocalTime()!! <= it.fim }
          var horarioDisponivel: HorarioDisponivelDto? = null
          if (horaFilter != null && horaFilter?.size != 0)
            horarioDisponivel = horaFilter?.first()
          horariosDisponiveis?.remove(horarioDisponivel)

          if (horarioDisponivel == null){

            mapaTipoEquipamentoHorariosDisponiveis.forEach{

              var equipamento: EquipamentosDisponiveisDto = EquipamentosDisponiveisDto(null, null,null)
              equipamento.horarios = horarios
              equipamento.id = it.key.id
              equipamento.nome = it.key.nome

              equipamentos.add(equipamento)
            }
            return equipamentos
          }
          if ((horarioDisponivel?.inicio != agendaSalaComEquipamento?.inicio?.toLocalTime()) || horarioDisponivel?.fim != agendaSalaComEquipamento?.fim?.toLocalTime()) {
              if (horarioDisponivel?.inicio != agendaSalaComEquipamento?.inicio?.toLocalTime()) {
                var divisao1: HorarioDisponivelDto  =  HorarioDisponivelDto( horarioDisponivel?.inicio, agendaSalaComEquipamento?.inicio?.toLocalTime())
                horariosDisponiveis?.add(divisao1)
              }
              if (agendaSalaComEquipamento?.fim?.toLocalTime() != horarioDisponivel?.fim) {
                var divisao2: HorarioDisponivelDto = HorarioDisponivelDto(agendaSalaComEquipamento?.fim?.toLocalTime(),horarioDisponivel?.fim)
                horariosDisponiveis?.add(divisao2)
              }
            }
            if (horariosDisponiveis?.size == 0) {
              mapaTipoEquipamentoHorariosDisponiveis?.remove(tipoEquipamento)
            }
          }
        }
      }


        mapaTipoEquipamentoHorariosDisponiveis.forEach{

          var equipamento: EquipamentosDisponiveisDto = EquipamentosDisponiveisDto(null, null,null)
          equipamento.horarios = horarios
          equipamento.id = it.key.id
          equipamento.nome = it.key.nome

          equipamentos.add(equipamento)
        }

        return equipamentos
    }



  fun listarEquipamentos(agendamento : Long) : List<AgendaSalaTipoEquipamentoDto> {
    var agendaSalas = agendaSalaRepository.findByAgendamento(agendamento)
    val itens =  agendaSalaTipoEquipamentoRepository.findByAgendaSalaAndStatusNotEqual(agendaSalas, SolicitacaoStatus.CANCELADO)
    val result :  MutableList<AgendaSalaTipoEquipamentoDto> = mutableListOf()
    itens.groupBy { it.tipoEquipamento }.forEach { tipoEquipamento, listAgendaSalaTipoEq ->
      var agendaSalaTipoEquipamento = AgendaSalaTipoEquipamentoDto()
      agendaSalaTipoEquipamento.id = listAgendaSalaTipoEq.first().id
      agendaSalaTipoEquipamento.nomeTipoEquipamento = tipoEquipamento?.nome
      agendaSalaTipoEquipamento.tipoEquipamentoId = tipoEquipamento?.id
      agendaSalaTipoEquipamento.custo = 0.0
      listAgendaSalaTipoEq.forEach{
       agendaSalaTipoEquipamento.custo = agendaSalaTipoEquipamento.custo?.plus(it.valor!!)
     }

      val agendaSalas = listAgendaSalaTipoEq.map {it.agendaSala}
      agendaSalaTipoEquipamento.inicio = agendaSalas.minBy { ag -> ag?.inicio!! }?.inicio
      agendaSalaTipoEquipamento.fim = agendaSalas.maxBy { ag -> ag?.fim!! }?.fim

      result.add(agendaSalaTipoEquipamento)
    }

    return result
  }

}



