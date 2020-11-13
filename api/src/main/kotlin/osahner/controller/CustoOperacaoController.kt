package osahner.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import osahner.domain.CarrinhoCompra
import osahner.service.CarrinhoService
import osahner.service.CustoOperacaoService

@RestController
@RequestMapping("/custos")
class CustoOperacaoController(
  private val custoOperacaoService: CustoOperacaoService,
  private val carrinhoService: CarrinhoService
) {

  @GetMapping(value = ["/{idCarrinhoCompra}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun calcularDesconto(@PathVariable idCarrinhoCompra: Long): Any? {
    val carrinho = carrinhoService.findById(idCarrinhoCompra)
    if (carrinho == null)
      return ResponseEntity.notFound().build<CarrinhoCompra>()

    return custoOperacaoService.calcular(carrinho.get())
  }
}
