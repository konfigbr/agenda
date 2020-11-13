package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Agendamento
import osahner.domain.Credito
import osahner.domain.Medico
import osahner.domain.User
import osahner.enums.CreditoTipo

interface CreditoRepository : JpaRepository<Credito, Long> {
  fun findAByValorDisponivelGreaterThanAndMedico( num: Double,medico: Medico): List<Credito>
  fun findByValorDisponivelGreaterThanAndMedicoOrderByDateCreated(valor: Double, medico: Medico): MutableList<Credito>
  //fun findByAgendamento( agendamento: Agendamento): MutableList<Credito>

  fun findByAgendamentoAndTipo( agendamento: Agendamento, tipo : CreditoTipo): MutableList<Credito>

}
