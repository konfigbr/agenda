package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.LocalDto
import osahner.service.LocalService

@RestController
@RequestMapping("/locals")
class LocalController(private val localService: LocalService) {

  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun list() = localService.list().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getById(@PathVariable id: Long): ResponseEntity<LocalDto> = localService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: LocalDto): Any? {
    if (localService.existsByNome(dto.nome)) {
      return ResponseEntity.badRequest().body("Já existe um local com este nome: " + dto.nome)
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(localService.save(dto).toDTO())
  }

  @PutMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: LocalDto): Any? {
    if (localService.existsById(id)){
      val local = localService.findByNome(dto.nome)

      if (local.isPresent && local.get().id != id)
          return ResponseEntity.badRequest().body("Já existe um local com este nome: " + dto.nome)

      val alteredItem = localService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)

    } else
      return ResponseEntity.notFound().build<Unit>()
  }

  @DeleteMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (localService.existsById(id)){
      localService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }
}
