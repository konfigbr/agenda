package osahner.controller

import osahner.config.SecurityProperties
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.AtivoDto
import osahner.dto.CheckinDto
import osahner.enums.StatusCheckin
import osahner.service.CheckinService
import java.time.LocalDateTime
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import osahner.repository.UserRepository

@RestController
@RequestMapping("/checkin")
class CheckinController(private val checkinService: CheckinService,
                        private val securityProperties: SecurityProperties,
                        private val userRepository: UserRepository) {



  /*@PostMapping(value = ["/checkin"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun saveCheckin(@RequestBody dto: CheckinDto):Any{

    var confirmaruser = checkinService.confirmaUsuario(dto.agendamentoId, dto.userId)

    if(confirmaruser == false){

      return ResponseEntity.badRequest().body("Usuário diferente dos cadastrados no agendamento")
    }

    dto.horario = LocalDateTime.now()
    dto.status = StatusCheckin.CHECKIN

    return ResponseEntity.status(HttpStatus.CREATED).body(checkinService.save(dto).toDTO())
  }

  @PostMapping(value = ["/checkinDuplo"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun saveCheckinDuplo(@RequestBody dto: CheckinDto):Any{

    val claims = Jwts.parserBuilder()
      .setSigningKey(Keys.hmacShaKeyFor(securityProperties.secret.toByteArray()))
      .build()
      .parseClaimsJws(dto.token?.replace(securityProperties.tokenPrefix, ""))

    var username = claims.body.subject
    var user = userRepository.findByUsername(username).get()


    var confirmaruser = checkinService.confirmaUsuarioCheckinDuplo(dto.agendamentoId, dto.userId)

    if(confirmaruser == false){

      return ResponseEntity.badRequest().body("Usuário diferente dos cadastrados no agendamento")
    }

    dto.horario = LocalDateTime.now()
    dto.status = StatusCheckin.CHECKIN

    return ResponseEntity.status(HttpStatus.CREATED).body(checkinService.save(dto).toDTO())
  }


  @PostMapping(value = ["/checkoutDuplo"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun saveCheckoutDuplo(@RequestBody dto: CheckinDto):Any{

    val claims = Jwts.parserBuilder()
      .setSigningKey(Keys.hmacShaKeyFor(securityProperties.secret.toByteArray()))
      .build()
      .parseClaimsJws(dto.token?.replace(securityProperties.tokenPrefix, ""))

    var username = claims.body.subject
    var user = userRepository.findByUsername(username).get()


    var confirmaruser = checkinService.confirmaUsuarioCheckinDuplo(dto.agendamentoId, dto.userId)

    if(confirmaruser == false){

      return ResponseEntity.badRequest().body("Usuário diferente dos cadastrados no agendamento")
    }

    dto.horario = LocalDateTime.now()
    dto.status = StatusCheckin.CHECKOUT

    return ResponseEntity.status(HttpStatus.CREATED).body(checkinService.save(dto).toDTO())
  }

  @PostMapping(value = ["/checkout"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun saveCheckout(@RequestBody dto: CheckinDto):Any{

    var confirmaruser = checkinService.confirmaUsuario(dto.agendamentoId, dto.userId)

    if(confirmaruser == false){

      return ResponseEntity.badRequest().body("Usuário diferente dos cadastrados no agendamento")
    }

    dto.horario = LocalDateTime.now()
    dto.status = StatusCheckin.CHECKOUT

    return ResponseEntity.status(HttpStatus.CREATED).body(checkinService.save(dto).toDTO())
  }



  @DeleteMapping(value = ["/remover/{id}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
    if (checkinService.existsById(id)){
      checkinService.delete(id)
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.notFound().build()
  }*/
}
