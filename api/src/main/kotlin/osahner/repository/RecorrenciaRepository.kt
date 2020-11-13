package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Credito
import osahner.domain.Medico
import osahner.domain.Recorrencia
import osahner.domain.User

interface RecorrenciaRepository : JpaRepository<Recorrencia, Long> {
   fun findByMedico(medico: Medico?): Recorrencia?

}
