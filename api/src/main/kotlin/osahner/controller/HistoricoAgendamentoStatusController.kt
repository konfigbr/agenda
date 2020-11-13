package osahner.controller

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.config.SecurityProperties
import osahner.dto.CheckinDto
import osahner.dto.HistoricoAgendamentoStatusDto
import osahner.enums.AgendamentoStatus
import osahner.enums.StatusCheckin
import osahner.enums.StatusMedico
import osahner.exception.AgendamentoVazioException
import osahner.repository.MedicoRepository
import osahner.repository.UserRepository
import osahner.service.AgendamentoService
import osahner.service.AssistenteService
import osahner.service.HistoricoAgendamentoStatusService
import osahner.service.MedicoService
import java.time.LocalDateTime

@RestController
@CrossOrigin
@RequestMapping("/checkin-scheduling")
class HistoricoAgendamentoStatusController(private val  historicoAgendamentoStatusService: HistoricoAgendamentoStatusService,
                                           private val  medicoService: MedicoService,
                                           private val  agendamentoService: AgendamentoService,
                                           private val  assistenteService: AssistenteService,
                                           private val securityProperties: SecurityProperties,
                                           private val userRepository: UserRepository,
                                           private val medicoRepository: MedicoRepository) {

  @GetMapping(value = ["", "/"])
  @CrossOrigin
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun list() = historicoAgendamentoStatusService.list().map { it.toDTO() }

  @GetMapping(value = ["/{id}"])
  @CrossOrigin
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun edit(@PathVariable id: Long): ResponseEntity<HistoricoAgendamentoStatusDto> = historicoAgendamentoStatusService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())

 /* @PostMapping
  @CrossOrigin
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun save(@RequestBody dto: HistoricoAgendamentoStatusDto) = historicoAgendamentoStatusService.save(dto).toDTO()
*/
  @PutMapping(value = ["/{id}"])
  @CrossOrigin
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun update(@PathVariable id: Long, @RequestBody dto: HistoricoAgendamentoStatusDto): ResponseEntity<HistoricoAgendamentoStatusDto> {
    if (historicoAgendamentoStatusService.existsById(id)) {
      val alteredItem = historicoAgendamentoStatusService.alter(id, dto).toDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build()
  }

  @DeleteMapping(value = ["/{id}"])
  @CrossOrigin
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL')")
  fun delete(@PathVariable id: Long) {
    if (historicoAgendamentoStatusService.existsById(id))
      historicoAgendamentoStatusService.delete(id)
  }



  @PostMapping(value = ["/professional/checkin"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL')")
  @CrossOrigin
  fun saveCheckin(@RequestBody dto: CheckinDto):Any?{

    var ret : Any? = null
    try {
      val result =  historicoAgendamentoStatusService.saveCheckin(dto)

      ret = ResponseEntity.status(HttpStatus.OK).body(result)

    } catch (e: RuntimeException) {
      ret = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e)

      //throw e
    }
    return ret
  }

  @PostMapping(value = ["/professional/checkout"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL')")
  @CrossOrigin
  fun saveCheckout(@RequestBody dto: CheckinDto):Any?{
    var ret : Any? = null
    try {
      val result =  historicoAgendamentoStatusService.saveCheckout(dto)

      ret = ResponseEntity.status(HttpStatus.OK).body(result)

    } catch (e: RuntimeException) {
      ret = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e)

      //throw e
    }

    return ret
  }


  @PostMapping(value = ["/assistant/checkin"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL','ROLE_ASSISTENTE')")
  @CrossOrigin
  fun saveCheckinAssistant(@RequestBody dto: CheckinDto):Any?{

    var ret : Any? = null
    try {
      val result =  historicoAgendamentoStatusService.saveCheckinAssistant(dto)

      ret = ResponseEntity.status(HttpStatus.OK).body(result)

    } catch (e: RuntimeException) {
      ret = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e)

      //throw e
    }
    return ret
  }


  @PostMapping(value = ["/assistant/checkout"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL','ROLE_ASSISTENTE')")
  @CrossOrigin
  fun saveCheckoutAssistant(@RequestBody dto: CheckinDto):Any?{

    var ret : Any? = null
    try {
      val result =  historicoAgendamentoStatusService.saveCheckoutAssistant(dto)

      ret = ResponseEntity.status(HttpStatus.OK).body(result)

    } catch (e: RuntimeException) {
      ret = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e)

      //throw e
    }
    return ret
  }



  @PostMapping(value = ["/assistant/checkin-professional"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL','ROLE_ASSISTENTE')")
  @CrossOrigin
  fun saveCheckinDuplo(@RequestBody dto: CheckinDto):Any?{
    var ret : Any? = null
    try {
     val result =  historicoAgendamentoStatusService.saveCheckinDuplo(dto)

       ret = ResponseEntity.status(HttpStatus.OK).body(result)

    } catch (e: RuntimeException) {
       ret = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e)

      //throw e
    }

    return ret

  }

  @PostMapping(value = ["/assistant/checkout-professional"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL','ROLE_ASSISTENTE')")
  @CrossOrigin

  fun saveCheckoutDuplo(@RequestBody dto: CheckinDto):Any?{
    var ret : Any? = null
    try {
      val result =  historicoAgendamentoStatusService.saveCheckoutDuplo(dto)

      ret = ResponseEntity.status(HttpStatus.OK).body(result)

    } catch (e: RuntimeException) {
      ret = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e)

      //throw e
    }

    return ret

  }


  @GetMapping(value = ["/comments/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL','ROLE_ASSISTENTE')")
  @CrossOrigin
  fun observacoes(@PathVariable id: Long): ResponseEntity<HistoricoAgendamentoStatusDto> = historicoAgendamentoStatusService.findById(id).map {
    ResponseEntity.ok(it.toDTO())
  }.orElse(ResponseEntity.notFound().build())


}
