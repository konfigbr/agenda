package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.CheckIn
import osahner.domain.TipoEquipamento
import osahner.domain.User
import java.util.*

interface CheckinRepository : JpaRepository<CheckIn, Long> {



}
