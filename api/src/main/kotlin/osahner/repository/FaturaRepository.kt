package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Fatura
import osahner.domain.Medico
import osahner.enums.StatusFatura
import java.time.LocalDate

interface FaturaRepository : JpaRepository<Fatura, Long> {
  fun findByMedicoOrderByDataCobranca(medico: Medico?): Collection<Fatura>?
  fun findByMedicoAndStatusNotOrderByDataCobranca(medico: Medico, paga: StatusFatura): MutableList<Fatura>?
  fun findByDataCobrancaAndMedicoAndStatus(dtCobranca: LocalDate, medico: Medico?, emAberto: StatusFatura): Fatura?
  fun findByMedicoAndStatusOrderById(medico: Medico?, emAberto: StatusFatura): MutableList<Fatura>?

}
