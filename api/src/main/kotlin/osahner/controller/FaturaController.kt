package osahner.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.domain.CarrinhoCompra
import osahner.domain.Fatura
import osahner.service.CarrinhoService
import osahner.service.FaturaService
import osahner.service.MedicoService

@RestController
@RequestMapping("/invoices")
class FaturaController(
  private val faturaService: FaturaService,
  private val medicoService: MedicoService,
  private val carrinhoService: CarrinhoService
) {

  @GetMapping(value = ["/professionals/{idProfessional}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun getByProfissional(@PathVariable idProfessional: Long): Any? {
    if (!medicoService.existsById(idProfessional))
      return ResponseEntity.badRequest().body("Profissional não encontrado")

    val profissional = medicoService.findById(idProfessional)

    return faturaService.findByMedico(profissional.get())?.map {
      it.toDTO()
    }
  }

  @GetMapping(value = ["/detailInvoice/{idFatura}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun detalharFatura(@PathVariable idFatura: Long): Any? {
    val fatura = faturaService.findById(idFatura).get()
    if (fatura == null)
      return ResponseEntity.notFound().build<Fatura>()

    return faturaService.detalharFatura(fatura)
  }

  @GetMapping(value = ["/detalhado/{idCarrinho}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun detalhado(@PathVariable idCarrinho: Long): Any? {
    val carrinho = carrinhoService.findById(idCarrinho)

    if (carrinho == null)
      return ResponseEntity.notFound().build<CarrinhoCompra>()

    return carrinhoService.detalharPorMesESalaEHorario(carrinho.get())
  }

  @GetMapping(value = ["/{idCarrinho}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun detalharPorMes(@PathVariable idCarrinho: Long): Any? {
    val carrinho = carrinhoService.findById(idCarrinho)

    if (carrinho == null)
      return ResponseEntity.notFound().build<CarrinhoCompra>()

    return carrinhoService.resumoCompraPorMes(carrinho.get())
  }

  @GetMapping(value = ["/precosEquipamentosUtilizados/{idCarrinho}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun precosEquipamentosUtilizados(@PathVariable idCarrinho: Long): Any? {
    val carrinho = carrinhoService.findById(idCarrinho)

    if (carrinho == null)
      return ResponseEntity.notFound().build<CarrinhoCompra>()

    return carrinhoService.precosEquipamentosUtilizados(carrinho.get())
  }
}


