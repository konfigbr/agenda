package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import osahner.domain.*
import osahner.enums.SolicitacaoStatus
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*


interface AgendaSalaRepository : JpaRepository<AgendaSala, Long>{

  @Query(value="select ags.* from agenda_sala as ags" +
    " where  ags.assistente_id = ?1 and ags.agendamento_id = ?2 " , nativeQuery = true)
  fun findByAssistenteAgendamento(assistenteId: Long?, agendamentoId: Long?): List<AgendaSala>

  fun findByAgendamentoIn(agendamentos: MutableList<Agendamento>): MutableList<AgendaSala>

  fun findByAgendamento(refAgendamentoEntity: Agendamento): MutableList<AgendaSala>

  @Query("SELECT ags FROM AgendaSala ags  WHERE ags.agendamento.id = ?1 order by inicio")
  fun findByAgendamento(id: Long): List<AgendaSala>


  @Query("SELECT ags FROM AgendaSala ags WHERE ags.agendamento.id = ?1 order by inicio")
  fun findAgendamento(agendamentoId: Long?): Collection<AgendaSala>

 // @Query("SELECT * FROM AgendaSala s WHERE s.sala_id = ?1 and inicio >= ?2 and fim <= ?3")
 // fun findAllBySalaAndInicioGreaterThanEqualsAndFimLessThanEquals(sala: Sala, inicio: LocalDateTime, fim:LocalDateTime ): MutableList<AgendaSala>


  @Query(value="select ags.* from agenda_sala as ags " +
    " where  ags.sala_id = ?1 and ags.inicio >= ?2  and ags.fim <= ?3" , nativeQuery = true)
  fun findBySalaAndInicioAndFim(salaId: Long?, inicio:LocalDateTime?, fim: LocalDateTime?): AgendaSala?

  @Query(value="select ags.* from agenda_sala as ags, agendamento a" +
    " where  ags.sala_id != ?1 and ags.inicio >= ?2 and ags.fim <= ?3  " +
    " and  ags.agendamento_id = a.id and a.status != 'CANCELADO' " +
    " and a.medico_id = ?4  order by ags.id ", nativeQuery = true)
  fun findAgendaEmOutrasSalasNoPeriodo(salaId: Long?, inicio:LocalDateTime?, fim: LocalDateTime?,medicoId: Long?): MutableList<AgendaSala>?;

  @Query(value="select ags.* from agenda_sala as ags" +
    " where  ags.sala_id = ?1 and ags.inicio >= ?2 and ags.fim <= ?3 " , nativeQuery = true)
  fun findAllBySalaAndInicioAndFim(salaId: Long?, inicio:LocalDateTime?, fim: LocalDateTime?): MutableList<AgendaSala>?


  @Query(value="select ags.* from agenda_sala as ags" +
    " join agendamento a on a.id = ags.agendamento_id " +
    " where ags.sala_id = ?1" +
    " and ags.inicio >= ?2" +
    " and ags.fim <= ?3" +
    " and a.status != 'PENDENTE' " , nativeQuery = true)
  fun findAllBySalaAndInicioAndFimAndAgendamentoNotPendente(salaId: Long?, inicio:LocalDateTime?, fim: LocalDateTime?): MutableList<AgendaSala>?

  @Query(value="select ags.* from agenda_sala as ags" +
    " where  ags.sala_id = ?1 and ags.inicio >= ?2 order by inicio limit 1 " , nativeQuery = true)
  fun findByInicioGreaterThanEqualsAndSala(salaId: Long?, inicio:LocalDateTime?): MutableList<AgendaSala>

  @Query("SELECT ags FROM AgendaSala ags WHERE ags.inicio >= ?1 and ags.fim <= ?2 order by inicio asc")
  fun findByInicioAndFimOrderByInicioAsc(inicio: LocalDateTime?, fim: LocalDateTime?): MutableList<AgendaSala>

  @Query(value="SELECT COUNT(distinct ags.id) FROM agenda_sala as ags" +
    " JOIN agendamento ag ON ag.id = ags.agendamento_id" +
    " WHERE ags.assistente_id = ?1" +
    " AND ags.inicio >= ?2" +
    " AND ags.fim <= ?3" +
    " AND ag.status = 'CONFIRMADO'", nativeQuery = true)
  fun findByAssistenteAndInicioGreaterThanEqualsAndFimLessThanEqualsAndConfirmed(assistenteId: Long, inicio: LocalDateTime, fim: LocalDateTime): Integer

  fun findAllBySalaAndInicioGreaterThanEqualAndFimLessThanEqual(sala: Sala, inicio: LocalDateTime, fim: LocalDateTime): MutableList<AgendaSala>?

  @Query(value="select ags.* from agenda_sala as ags" +
    " where ags.sala_id = ?1" +
    " and ags.inicio >= ?2" +
    " and ags.fim <= ?3" +
    " and ags.agendamento_id IS NULL" +
    " order by ags.inicio asc", nativeQuery = true)
  fun findBySalaAndInicioAndFimAndAgendamentoNull(salaId: Long?, inicio:LocalDateTime?, fim: LocalDateTime): MutableList<AgendaSala>?

  abstract fun findByCustoLessThanAndAgendamentoIn(valor: Double?, agendamentosMes: List<Agendamento>): MutableList<AgendaSala>?

  @Query(value="select ags.* from agenda_sala as ags" +
    " JOIN agendamento ag ON ag.id = ags.agendamento_id" +
    " JOIN solicitacao_reserva sr ON sr.id = ag.solicitacao_reserva_id" +
    " and sr.status = 'PENDENTE'" +
    " and sr.carrinho_id = ?1 " , nativeQuery = true)
  fun findAllBySolicitacaoReservaAndCarrinhoAndPendente(carrinho: Long): MutableList<AgendaSala>


  @Query(value="select ags.* from agenda_sala as ags" +
    " JOIN agenda_sala_tipo_equipamento ag ON ag.agenda_sala_id = ags.id" +
    " JOIN solicitacao_equipamento sr ON sr.id = ag.solicitacao_equipamento_id" +
    " and sr.status = 'PENDENTE'" +
    " and sr.carrinho_id = ?1 " , nativeQuery = true)
  fun findAllBySolicitacaoEquipamentoAndCarrinhoAndPendente(carrinhoCompra: Long?): MutableList<AgendaSala>


}
