package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.AgendaAssistente
import osahner.domain.AgendaSala
import osahner.domain.AgendaSalaTipoEquipamento
import osahner.domain.Agendamento
import osahner.enums.SolicitacaoStatus
import osahner.domain.*
import java.time.LocalDateTime
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


interface AgendaSalaTipoEquipamentoRepository : JpaRepository<AgendaSalaTipoEquipamento, Long>{

  @Query(value="select * from agenda_sala_tipo_equipamento as ags" +
    " where  ags.agenda_sala_id in ?1  order by id " , nativeQuery = true)
  fun findByAgendamento( agendamentoSalaId: Long?): List<AgendaSalaTipoEquipamento>;

  fun findByAgendaSala( agendaSala: AgendaSala?): List<AgendaSalaTipoEquipamento>;

  fun findByAgendaSalaIn( agendaSala: MutableList<AgendaSala>?): List<AgendaSalaTipoEquipamento>;

  @Query(value="select ags.* from agenda_sala_tipo_equipamento as ags" +
    " where ags.agenda_sala_id = ?1 and ags.status != 'CANCELADO'" , nativeQuery = true)
  fun findAllByAgendamentoAndStatus( agendaSalaId: Long?): MutableList<AgendaSalaTipoEquipamento>;


  @Query(value="select aste.* from agenda_sala_tipo_equipamento aste" +
    " where aste.agenda_sala_id = ?1" +
    " and aste.status != 'CANCELADO'" , nativeQuery = true)
  fun findAllByAgendaSalaAndNotCancelado( agendaSalaId: Long?): MutableList<AgendaSalaTipoEquipamento>;

  @Query(value="select c from AgendaSalaTipoEquipamento c inner join c.agendaSala o where c.status != ?1 and o in ?2")
  fun findByStatusNotEqualsAndAgendaSalaIn(status : SolicitacaoStatus, agendaSala : MutableList<AgendaSala>) : MutableList<AgendaSalaTipoEquipamento>

  @Query(value="select ags.* from agenda_sala as ags" +
    "join agenda_sala_tipo_equipamento as age on age.agenda_sala_id = ags.id "+
    " where status != CANCELADO" , nativeQuery = true)
  fun findAgendaSalaComEquipamento(): MutableCollection<AgendaSala>

  fun findBySolicitacaoEquipamento(it: SolicitacaoEquipamento) : List<AgendaSalaTipoEquipamento>


  @Query(value="select c from AgendaSalaTipoEquipamento c inner join c.agendaSala o inner join c.tipoEquipamento tp where tp in ?1 and c.status in ?2 and o in ?3")
  fun findByStatusAgendaSalaTipoEquipamento(tipoEquipamento : MutableList<TipoEquipamento>, status : List<SolicitacaoStatus>, agendaSala : MutableList<AgendaSala>?) : MutableList<AgendaSalaTipoEquipamento>

  @Query(value="select c from AgendaSalaTipoEquipamento c inner join c.agendaSala o inner join c.tipoEquipamento tp where c.status != ?2 and o in ?1 order by c.id")
  fun findByAgendaSalaAndStatusNotEqual(agendaSalas: List<AgendaSala>, cancelado: SolicitacaoStatus) : List<AgendaSalaTipoEquipamento>

  @Query(value="select a from AgendaSalaTipoEquipamento a INNER JOIN a.solicitacaoEquipamento as ag  " +
    "where a.status = ?1 and ag in ?2  group by a.id")
  fun findAllByStatusAndSolicitacaoEquipamentoIn(status: SolicitacaoStatus, solicitacaoEquipamento: List<SolicitacaoEquipamento>): MutableList<AgendaSalaTipoEquipamento>

  @Query(value="select c from AgendaSalaTipoEquipamento c inner join c.agendaSala o where c.status = ?1 and o in ?2")
  fun findAByStatusAndAgendaSalaIn(confirmado: Any, agendasSala: MutableList<AgendaSala>?):  MutableList<AgendaSalaTipoEquipamento>

  @Query(value="select c from AgendaSalaTipoEquipamento c inner join c.agendaSala o inner join c.tipoEquipamento tp where c.status != ?1 and tp in ?2 and o in ?3")
  fun findAllByTipoEquipamentoAndAgendaSalaInListAndStatusNotEqual(status: SolicitacaoStatus, tipoEquipamento: TipoEquipamento, agendasSalas: MutableCollection<AgendaSala>?): MutableList<AgendaSalaTipoEquipamento>

  @Query(value="SELECT ag.tipoEquipamento.id ,AVG(ag.valor) from AgendaSalaTipoEquipamento ag" +
    " INNER JOIN ag.solicitacaoEquipamento as se" +
    " WHERE se.refCarrinhoCompraEntity = ?1" +
    " AND ag.status = 'PENDENTE'" +
    " GROUP BY ag.tipoEquipamento ")
  fun findByCarrinhoAndPendente (carrinho: CarrinhoCompra): Any?

  @Query(value="select c from AgendaSalaTipoEquipamento c inner join c.solicitacaoEquipamento o where c.status = ?1 and o in ?2")
  fun findByStatusAndSolicitacaoEquipamentoIn(status: SolicitacaoStatus, solicitacaoEquipamento: List<SolicitacaoEquipamento>) : MutableList<AgendaSalaTipoEquipamento>

 }

