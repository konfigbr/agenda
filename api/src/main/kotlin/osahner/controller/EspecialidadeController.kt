package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.EspecialidadeDto
import osahner.service.EspecialidadeService

@RestController
@RequestMapping("/specialties")
class EspecialidadeController(private val especialidadeService: EspecialidadeService) {

  @GetMapping(value = ["", "/"])
  fun list() = especialidadeService.list().map { it.toDTO() }

  @GetMapping(value = ["/professionals"])
  fun listProfessionalSpecialties() = especialidadeService.listProfessionalSpecialties().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getById(@PathVariable id: Long): ResponseEntity<EspecialidadeDto> = especialidadeService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: EspecialidadeDto): Any? {
    if (especialidadeService.existsByNome(dto.nome)) {
      return ResponseEntity.badRequest().body("Já existe uma especialidade com este nome: " + dto.nome)
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(especialidadeService.save(dto).toDTO())
  }

  @PutMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: EspecialidadeDto): Any? {
    if (especialidadeService.existsById(id)){
      val especialidade = especialidadeService.findByNome(dto.nome)

      if (especialidade.isPresent && especialidade.get().id != id)
          return ResponseEntity.badRequest().body("Já existe uma especialidade com este nome: " + dto.nome)

      val alteredItem = especialidadeService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)

    } else
      return ResponseEntity.notFound().build<Unit>()
  }

  @DeleteMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (especialidadeService.existsById(id)){
      especialidadeService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }
}
