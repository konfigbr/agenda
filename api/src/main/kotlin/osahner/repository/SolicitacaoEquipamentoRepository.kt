package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.CarrinhoCompra
import org.springframework.data.jpa.repository.Query
import osahner.domain.SolicitacaoEquipamento
import osahner.domain.SolicitacaoReserva
import osahner.domain.TipoEquipamento
import osahner.enums.SolicitacaoStatus

interface SolicitacaoEquipamentoRepository : JpaRepository<SolicitacaoEquipamento, Long>{
  fun findByRefCarrinhoCompraEntity(carrinhoCompra: CarrinhoCompra?): List<SolicitacaoEquipamento>
  fun findByRefCarrinhoCompraEntityAndStatus(carrinhoCompra: CarrinhoCompra?, status : SolicitacaoStatus): List<SolicitacaoEquipamento>


}
