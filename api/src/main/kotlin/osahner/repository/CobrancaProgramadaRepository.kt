package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.CobrancaProgramada
import osahner.domain.Sala
import osahner.domain.User
import osahner.dto.SalaDto
import java.util.*

interface CobrancaProgramadaRepository : JpaRepository<CobrancaProgramada, Long>{

  @Query(value="SELECT cp.* FROM cobranca_programada cp " +
    " WHERE cp.justa_cobranca_id = ?1 order by data_cobranca" , nativeQuery = true )
  fun findByJustaCobrancaId(idTransacaoJusta: String?): Optional<CobrancaProgramada>
}
