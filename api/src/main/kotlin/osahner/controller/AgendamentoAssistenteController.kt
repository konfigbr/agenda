package osahner.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.*
import osahner.service.AgendamentoAssistenteService
import osahner.service.AssistenteService
import osahner.service.UserService

@RestController
@RequestMapping("/assistants_scheduling")
class AgendamentoAssistenteController(
  private val agendamentoAssistenteService: AgendamentoAssistenteService,
  private val assistenteService: AssistenteService,
  private val userService: UserService
) {

  @GetMapping(value = ["/current"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun atual(): Any? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    val assistente = assistenteService.findByrefUserEntity(currentUser).get()

    if(assistente == null)
      return ResponseEntity.notFound().build<AssistenteDto>()

    return ResponseEntity.ok(agendamentoAssistenteService.agendamentoAtual(assistente.id!!))
  }

  @GetMapping(value = ["/list"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun listarAgendamentos(@RequestParam id: Long?, @RequestParam inicio: String?, @RequestParam fim: String? ): List<AgendamentoCustomDto> {
    val dto = AgendamentoAssistenteDto(
      id = id,
      inicio = inicio,
      fim = fim
    )

    return agendamentoAssistenteService.listarAgendamentos(dto).map { it }
  }
  /*@GetMapping(value = ["/ok/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun ok(@PathVariable id: Long) : ResponseEntity<Unit>{
     val result =  agendamentoAssistenteService.darOk(id)
     return if (result)  ResponseEntity.ok().build() else ResponseEntity.notFound().build()
  }

  @GetMapping(value = ["/liberarSala/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun liberarSala(@PathVariable id: Long) : ResponseEntity<Unit> {
    val result =  agendamentoAssistenteService.liberarSala(id)
    return if (result)  ResponseEntity.ok().build() else ResponseEntity.notFound().build()
  }*/
}
