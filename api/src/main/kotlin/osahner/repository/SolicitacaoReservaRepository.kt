package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.*
import osahner.enums.SolicitacaoStatus


interface SolicitacaoReservaRepository : JpaRepository<SolicitacaoReserva, Long>{
  fun findByRefCarrinhoCompraEntity(carrinhoCompra: CarrinhoCompra?): List<SolicitacaoReserva>
  fun findByRefCarrinhoCompraEntityAndStatus(carrinhoCompra: CarrinhoCompra?, status : SolicitacaoStatus): List<SolicitacaoReserva>
  @Query(value="select a from SolicitacaoReserva a INNER JOIN FETCH a.refAgendamentoEntities as ag  " +
    "where a.refCarrinhoCompraEntity = ?1")
  fun findByRefCarrinhoCompraEntityAgendamentos(carrinhoCompra: CarrinhoCompra?): List<SolicitacaoReserva>

}
