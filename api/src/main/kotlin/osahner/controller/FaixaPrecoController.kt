package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.FaixaPrecoDto
import osahner.dto.TipoEquipamentoDto
import osahner.service.FaixaPrecoService

@RestController
@RequestMapping("/price-ranges")
class FaixaPrecoController(private val faixaPrecoService: FaixaPrecoService) {

  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun list() = faixaPrecoService.list().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getById(@PathVariable id: Long): ResponseEntity<FaixaPrecoDto> = faixaPrecoService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: FaixaPrecoDto): Any? {

    return ResponseEntity.status(HttpStatus.CREATED).body(faixaPrecoService.save(dto).toDTO())
  }

  @PutMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: FaixaPrecoDto): Any? {
    if (faixaPrecoService.existsById(id)){

      val alteredItem = faixaPrecoService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)

    } else
      return ResponseEntity.notFound().build<Unit>()
  }

  @DeleteMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (faixaPrecoService.existsById(id)){
      faixaPrecoService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }
}
