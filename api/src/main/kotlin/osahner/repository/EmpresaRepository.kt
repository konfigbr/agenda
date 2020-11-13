package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Empresa
import java.util.*

interface EmpresaRepository : JpaRepository<Empresa, Long> {
    fun findByCnpj(cnpj: String?): Optional<Empresa>
    fun existsByCnpj(cnpj: String?): Boolean
}
