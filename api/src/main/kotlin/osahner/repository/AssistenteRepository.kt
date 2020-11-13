package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.Assistente
import osahner.domain.Medico
import osahner.domain.User
import java.util.*

interface AssistenteRepository : JpaRepository<Assistente, Long>{
  fun findByUser(user: User?): Optional<Assistente>

  fun findByVirtual(virtual: Boolean?): Optional<Assistente>

  @Query(value="select a.* from assistente a" +
    " inner join user u on u.id = a.user_id"+
    " where a.ativo = 'S'" +
    " order by u.nome"
    , nativeQuery = true)
  override fun findAll(): MutableList<Assistente>

  @Query(value="select a.* from assistente a" +
    " where a.ativo = 'S'" +
    " and a.id = ?1"
    , nativeQuery = true)
  override fun findById(id: Long?): Optional<Assistente>

  @Query(value="select a.* from assistente a" +
          " where a.manutencao = 1"
          , nativeQuery = true)
  fun findByManutencao(): Optional<Assistente>

 /* @Query(value="select a.* from agendamento a join agenda_sala as ags" +
    " where a.status = 'confirmado' and  ags.assistente_id = ?1 " +
    "and  ags.inicio between ?2 and ?3 order by inicio", nativeQuery = true)
  fun findAllByAssistenteDisponiveis(sala: Sala, inicio: LocalDateTime, fim:LocalDateTime )
*/
}

