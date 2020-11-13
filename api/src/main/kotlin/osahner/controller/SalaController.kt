package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.SalaDto
import osahner.service.SalaService

@RestController
@RequestMapping("/rooms")
class SalaController(private val salaService: SalaService) {

  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE','ROLE_ATENDENTE', 'ROLE_PROFISSIONAL')")
  fun list() = salaService.list().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ATENDENTE', 'ROLE_PROFISSIONAL')")
  fun getById(@PathVariable id: Long): ResponseEntity<SalaDto> = salaService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: SalaDto) = ResponseEntity.status(HttpStatus.CREATED).body(salaService.save(dto).toDTO())

  @PutMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: SalaDto): ResponseEntity<SalaDto> {
    if (salaService.existsById(id)) {
      val alteredItem = salaService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build()
  }

  @DeleteMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (salaService.existsById(id)){
      salaService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }
}
