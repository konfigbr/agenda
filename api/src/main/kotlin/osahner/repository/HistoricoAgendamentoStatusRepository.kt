package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.*
import java.time.LocalDateTime
import java.util.*

interface HistoricoAgendamentoStatusRepository : JpaRepository<HistoricoAgendamentoStatus, Long>{

  @Query(value="select * from historico_agendamento_status a" +
    " where a.agendamento_id = ?1 "
   , nativeQuery = true)
  fun findByIdAgendamento(idAgendamento: Long?): MutableList<HistoricoAgendamentoStatus?>


}
