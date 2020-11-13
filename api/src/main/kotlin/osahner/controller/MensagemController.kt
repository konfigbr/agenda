package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.domain.Mensagem
import osahner.dto.AtivoDto
import osahner.dto.MensagemDto
import osahner.service.AtivoService
import osahner.service.MensagemService
import osahner.service.SalaService
import osahner.service.UserService

@RestController
@RequestMapping("/messages")
class MensagemController(
  private val mensagemService: MensagemService,
  private val salaService: SalaService,
  private val userService: UserService
) {

  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun list() = mensagemService.list().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getById(@PathVariable id: Long): ResponseEntity<MensagemDto> = mensagemService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: MensagemDto) = ResponseEntity.status(HttpStatus.CREATED).body(mensagemService.save(dto).toDTO())

  @PutMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: MensagemDto): ResponseEntity<MensagemDto> {
    if (mensagemService.existsById(id)) {
      val alteredItem = mensagemService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build()
  }

  @DeleteMapping(value = ["/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (mensagemService.existsById(id)){
      mensagemService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }

  @GetMapping(value = ["/rooms/{idSala}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getBySala(@PathVariable idSala: Long): Any? {

    val sala = salaService.findById(idSala).get()

    if (sala == null)
      return ResponseEntity.notFound().build<MensagemDto>()

    return mensagemService.findBySala(sala).map {
      ResponseEntity.ok(it.toDTO())
    }
  }

  @GetMapping(value = ["/recipients/{idDestinatario}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getByDestinatario(@PathVariable idDestinatario: Long): Any? {

    val destinatario = userService.findById(idDestinatario).get()

    if (destinatario == null)
      return ResponseEntity.notFound().build<MensagemDto>()

    return mensagemService.findByDestinatario(destinatario).map {
      ResponseEntity.ok(it.toDTO())
    }
  }

  @GetMapping(value = ["/senders/{idRemetente}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getByRemetente(@PathVariable idRemetente: Long): Any? {

    val remetente = userService.findById(idRemetente).get()

    if (remetente == null)
      return ResponseEntity.notFound().build<MensagemDto>()

    return mensagemService.findByRemetente(remetente).map {
      ResponseEntity.ok(it.toDTO())
    }
  }

}
