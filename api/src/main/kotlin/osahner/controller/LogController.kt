package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.TipoEquipamentoDto
import osahner.service.TipoEquipamentoService

@RestController
@RequestMapping("/audit")
class LogController(private val tipoEquipamentoService: TipoEquipamentoService) {

  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun list() = tipoEquipamentoService.list().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getById(@PathVariable id: Long): ResponseEntity<TipoEquipamentoDto> = tipoEquipamentoService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: TipoEquipamentoDto): Any? {
    if (tipoEquipamentoService.existsByNome(dto.nome)) {
      return ResponseEntity.badRequest().body("Já existe um equipamento com este nome: " + dto.nome)
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(tipoEquipamentoService.save(dto).toDTO())
  }

  @PutMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: TipoEquipamentoDto): Any? {
    if (tipoEquipamentoService.existsById(id)){
      val tipo = tipoEquipamentoService.findByNome(dto.nome)

      if (tipo.isPresent && tipo.get().id != id)
          return ResponseEntity.badRequest().body("Já existe um equipamento com este nome: " + dto.nome)

      val alteredItem = tipoEquipamentoService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)

    } else
      return ResponseEntity.notFound().build<Unit>()
  }

  @DeleteMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (tipoEquipamentoService.existsById(id)){
      tipoEquipamentoService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }
}
