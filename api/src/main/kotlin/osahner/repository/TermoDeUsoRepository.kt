package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.*
import osahner.enums.TipoTermoDeUso
import java.util.*

interface TermoDeUsoRepository : JpaRepository<TermoDeUso, Long> {

    fun findByTipo(tipoTermoDeUso: TipoTermoDeUso): Optional<TermoDeUso>
}
