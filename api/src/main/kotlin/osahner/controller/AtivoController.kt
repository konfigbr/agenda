package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.AtivoDto
import osahner.service.AtivoService

@RestController
@RequestMapping("/equipments")
class AtivoController(private val ativoService: AtivoService) {

  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun list() = ativoService.list().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getById(@PathVariable id: Long): ResponseEntity<AtivoDto> = ativoService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: AtivoDto) = ResponseEntity.status(HttpStatus.CREATED).body(ativoService.save(dto).toDTO())

  @PutMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: AtivoDto): ResponseEntity<AtivoDto> {
    if (ativoService.existsById(id)) {
      val alteredItem = ativoService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build()
  }

  @DeleteMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (ativoService.existsById(id)){
      ativoService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }
}
