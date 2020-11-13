package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.CarrinhoCompra
import osahner.domain.Medico
import osahner.domain.User
import osahner.enums.SolicitacaoStatus
import java.util.*

interface CarrinhoRepository : JpaRepository<CarrinhoCompra, Long> {
   fun findByStatusAndMedico(status: SolicitacaoStatus, currentUser: Medico): CarrinhoCompra?

  fun findByStatus(status: SolicitacaoStatus): MutableList<CarrinhoCompra>?

  @Query(value="select c.* from carrinho_compra as c" +
    " join solicitacao_reserva as sr on sr.carrinho_id = c.id" +
    " join agendamento as a on a.solicitacao_reserva_id = sr.id" +
    " where a.id = ?1", nativeQuery = true)
   fun findByAgendamento(idAgendamento: Long): CarrinhoCompra?
}
