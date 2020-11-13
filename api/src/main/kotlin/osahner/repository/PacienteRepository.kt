package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.Assistente
import osahner.domain.Paciente
import osahner.domain.User
import java.util.*

interface PacienteRepository: JpaRepository<Paciente, Long> {
  fun findByUser(user: User?): Optional<Paciente>

  @Query(value="select a.* from paciente a" +
    " where a.ativo = 'S'" +
    " and a.id = ?1"
    , nativeQuery = true)
  override fun findById(id: Long?): Optional<Paciente>

}
