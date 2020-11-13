package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import osahner.domain.*
import java.time.LocalDateTime

@Repository
interface ParametroRepository : JpaRepository<Parametro, Long> {

    fun findByTipo(tipo: String): Parametro?
}