package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Credito
import osahner.domain.CreditoUtilizado
import osahner.domain.User

interface CreditoUtilizadoRepository : JpaRepository<CreditoUtilizado, Long> {
 
}
