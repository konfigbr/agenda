package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.*

interface TermoDeUsoRepository : JpaRepository<TermoDeUso, Long>
