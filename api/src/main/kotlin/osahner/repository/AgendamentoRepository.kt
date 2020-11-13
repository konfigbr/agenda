package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.Agendamento
import osahner.domain.Medico
import osahner.domain.SolicitacaoReserva
import osahner.enums.AgendamentoStatus
import osahner.enums.SolicitacaoStatus
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


interface AgendamentoRepository : JpaRepository<Agendamento, Long>{

  @Query(value="select distinct a from Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " where a.status = 'CONFIRMADO' and  ags.assistente.id = ?1" +
          " and ags.inicio >= ?2 and ags.inicio <= ?3")
  fun findByDiaAtualAssistente(assistenteId: Long?, inicio:LocalDateTime?, fim: LocalDateTime): List<Agendamento>

  @Query(value="select a from Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " where a.status = 'CONFIRMADO' and ags.assistente.id = ?1" +
          " and ags.inicio <= ?2 and ags.fim >= ?3 order by ags.inicio asc")
  fun findByDiaAtual(assistenteId: Long?, inicio:LocalDateTime?, fim:LocalDateTime?): List<Agendamento>

  @Query(value="select a from Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " where a.status = 'CONFIRMADO' and ags.assistente.id = ?1" +
          " and ags.inicio > ?2 and ags.inicio <= ?3 order by ags.inicio asc")
  fun findByDiaRestante(assistenteId: Long?, atual:LocalDateTime?, final:LocalDateTime?): List<Agendamento>

  @Query(value="select a from Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " where a.status = 'CONFIRMADO' and a.medico.id = ?1" +
          " and ags.inicio <= ?2 and ags.fim >= ?3 order by ags.inicio asc")
  fun findByDiaAtualProfissional(medicoId: Long?, inicio:LocalDateTime?, fim:LocalDateTime?): List<Agendamento>

  @Query(value="select distinct a from Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " where a.status = 'CONFIRMADO' "+
          " and ags.inicio >= ?1 and ags.fim <= ?2 order by ags.inicio asc")
  fun findDiaAtualByInicioAndFim(inicio:LocalDateTime?, fim:LocalDateTime?): List<Agendamento>

  @Query(value="select distinct a from Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " where a.status = 'CONFIRMADO' "+
          "and a.id = ?1" +
          " and ags.inicio >= ?2 and ags.fim <= ?3 order by ags.inicio asc")
  fun findDiaAtualByIdAndInicioAndFim(id:Long, inicio:LocalDateTime?, fim:LocalDateTime?): Agendamento

  @Query(value="select a from Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " where a.status = 'CONFIRMADO' and a.medico.id = ?1" +
          " and ags.inicio > ?2 and ags.inicio <= ?3 order by ags.inicio asc")
  fun findByDiaRestanteProfissional(medicoId: Long?, atual:LocalDateTime?, final:LocalDateTime?): List<Agendamento>

  @Query(value="SELECT distinct a FROM Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " WHERE a.medico.id = ?1" +
          " AND ags.inicio >= ?2" +
          " AND ags.fim <= ?3" +
          " AND a.status = 'CONFIRMADO'")
  fun findAllByMedicoAndInicioAndFim(idMedico: Long?, inicioMes: LocalDateTime, fimMes: LocalDateTime): MutableList<Agendamento>

  @Query(value="SELECT distinct a FROM Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " WHERE a.medico.cpf = ?1" +
          " AND ags.inicio >= ?2" +
          " AND ags.fim <= ?3")
  fun findAllByMedicoCpfAndInicioAndFim(cpf: String?, inicioMes: LocalDateTime, fimMes: LocalDateTime): MutableList<Agendamento>

  @Query(value="SELECT distinct a FROM Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " WHERE ags.inicio >= ?1" +
          " AND ags.fim <= ?2")
  fun findAllByInicioAndFim( inicioMes: LocalDateTime, fimMes: LocalDateTime): MutableList<Agendamento>

  @Query(value="select a from Agendamento a " +
          "INNER JOIN a.medico m  " +
          "where a.status = ?2 and m.id = ?1  group by a.id")
  fun findAllByMedicoAndStatus(medicoId: Long, status : SolicitacaoStatus): MutableList<Agendamento>
/*
  @Query(value="select a from Agendamento a " +
    "INNER JOIN a.medico m  " +
    "where a.status = ?2 and m.id = ?1  group by a.id")
  fun findAllByMedicoAndStatus(medicoId: Long, status : SolicitacaoStatus): MutableList<Agendamento>
*/

  fun findAllByStatusNotLikeAndSolicitacaoReservaIn(status: SolicitacaoStatus, listSolicitao: List<SolicitacaoReserva>): List<Agendamento>

  fun findAllByStatusLikeAndSolicitacaoReservaIn(status: SolicitacaoStatus, listSolicitao: List<SolicitacaoReserva>): List<Agendamento>

  @Query(value="SELECT distinct a FROM Agendamento a INNER JOIN FETCH a.agendaSalas as ags" +
          " WHERE a.status = 'CONFIRMADO'" +
          " AND a.medico.id = ?1" +
          " AND ags.inicio >= ?2" +
          " AND ags.fim <= ?3")
  fun findAllByMedicoAndInicioMesAndFimMes(idMedico: Long, inicioMes: LocalDateTime, fimMes: LocalDateTime): MutableList<Agendamento>



  @Query(value="SELECT a.* FROM agendamento a " +
          " WHERE a.solicitacao_reserva_id = ?1 order by id" , nativeQuery = true )
  fun findAllBySolicitacaoReserva(idSolicitacao: Long): MutableList<Agendamento>

  fun findByMedicoAndStatusAndAgendamentoStatusAtual(medico: Medico?, confirmado: SolicitacaoStatus, naoIniciado: AgendamentoStatus): MutableList<Agendamento>

  fun findByStatus( status: SolicitacaoStatus): MutableList<Agendamento>


  @Query(value="select a from Agendamento a INNER JOIN a.solicitacaoReserva as ag  " +
          "where a.status = ?1 and ag in ?2  group by a.id")
  fun findAllByStatusAndSolicitacaoReservaIn(status: SolicitacaoStatus, solicitacaoReservas: List<SolicitacaoReserva>): MutableList<Agendamento>

  @Query(value="select a from Agendamento a INNER JOIN a.medico m INNER JOIN a.agendaSalas as ag " +
          " where a.id != ?1 and m = ?2 and a.dia BETWEEN ?3 AND ?4  and a.status = ?5")
  fun findByIdNotEqualAndMedicoAndDiaBetween(id: Long?, medico: Medico?, toLocalDate: LocalDate?, toLocalDate1: LocalDate?, status: SolicitacaoStatus): List<Agendamento>


}