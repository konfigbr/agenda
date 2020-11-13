package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.Sala
import osahner.dto.SalaDto

interface SalaRepository : JpaRepository<Sala, Long>{
  @Query(value="select * from sala "+
    " where status = 'DISPONÍVEL' and reserva = 0 order by nome", nativeQuery = true)
  fun findAllByAvailable(): MutableCollection<Sala>

  @Query(value="select * from sala "+
    " ORDER BY especialidade_id ASC, id ASC", nativeQuery = true)
  fun findAllOrderByEspecialidadeAndId(): MutableCollection<Sala>

  @Query(value="select * from sala "+
    " where status = 'DISPONÍVEL' and reserva = 1 and especialidade_id = ?1 order by nome", nativeQuery = true)
  fun findAllDisponivelAndReservaAndEspecialidade(especialidade : Long?): MutableCollection<Sala>
}
