package osahner.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.*
import osahner.service.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@RestController
@RequestMapping("/pdf")
class PdfController(
  private val agendamentoAssistenteService: AgendamentoAssistenteService,
  private val assistenteService: AssistenteService,
  private val userService: UserService,
  private val exportPdfService: ExportPdfService,
  private val medicoService: MedicoService
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


  @GetMapping(value = ["/export/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL','ROLE_ATENDENTE','ROLE_ADMINISTRADOR')")
  fun pdfExport(@PathVariable id: Long): ResponseEntity<InputStreamResource?> {
      var headers: HttpHeaders = HttpHeaders()
      val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
      val profissional = medicoService.findById(id).get()
      var ret = exportPdfService.exportTerms(profissional)
    return  ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(InputStreamResource(ret))
  }
}
