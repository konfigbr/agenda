package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.*

interface AgendaAssistenteRepository : JpaRepository<AgendaAssistente, Long>{

  @Query(value="select * from agenda_assistente a" +
    " where a.sala_id = ?1 " +
    "and  a.hora_do_dia between ?2 and ?3 " +
    "and  a.dia_da_semana in (?4) order by id", nativeQuery = true)
  fun findAllBySalaAndDiaDaSemanaInListAndHoraDoDiaBetween(salaId: Long?, inicio:Int?, fim: Int?, dias : Collection<Int>): List<AgendaAssistente>;

/*  @Query(value="select a.* from agenda_assistente a" +
    " where a.sala_id = ?1 ", nativeQuery = true)
  fun findBySalaId(salaId: Long?): MutableCollection<AgendaAssistente>;*/

  fun findBySalaId(salaId: Long?): MutableCollection<AgendaAssistente>;

  @Query(value="select * from agenda_assistente a" +
    " where a.sala_id = ?1 and a.group_id = ?2", nativeQuery = true)
  fun findBySalaIdAndgroupId(salaId: Long?, groupId: String?): MutableList<AgendaAssistente>;

  @Query(value="select * from agenda_assistente a" +
    " where a.hora_do_dia = ?1 and a.assistente_id = ?2", nativeQuery = true)
  fun findAllbyhoraDoDiaAndIdAssistente(horaDoDia: Int?, idAssistente:Long?): MutableCollection<AgendaAssistente>


  @Query(nativeQuery = true, value =
  "SELECT v.*, COUNT(*) AS total " +
    "FROM  agenda_assistente v, sala s " +
    " where v.sala_id = s.id and s.id = ?1 " +
    "GROUP BY assistente_id order by total desc limit 1")
  fun findAssistentePrincipalBySala(salaId: Long?): MutableList<AgendaAssistente>


  fun findAllBySalaAndDiaDaSemanaAndHoraDoDiaGreaterThanEqualAndHoraDoDiaLessThanOrderByHoraDoDiaAsc(sala: Sala, diaDaSemana: Int, horaDoDia: Int, horaMinimaAgendada: Int)
    : MutableList<AgendaAssistente>

  fun findByAssistente(assistente: Assistente?): MutableList<AgendaAssistente>;

}
