package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.AtivoDto
import osahner.dto.DescontoDto
import osahner.service.AtivoService
import osahner.service.DescontoService
import osahner.service.DocumentoProfissionalService

@RestController
@RequestMapping("/discounts")
class DescontoController(
        private val descontoService: DescontoService
) {

    @GetMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun getById(@PathVariable id: Long): ResponseEntity<DescontoDto> = descontoService.findById(id).map {
        ResponseEntity.ok(it.toDTO())
    }.orElse(ResponseEntity.notFound().build())

    @PutMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun update(@PathVariable id: Long, @RequestBody dto: DescontoDto): ResponseEntity<DescontoDto> {
        if (descontoService.existsById(id)) {
            val alteredItem = descontoService.alter(id, dto).toDTO()
            return ResponseEntity.ok(alteredItem)
        }
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
        if (descontoService.existsById(id)){
            descontoService.delete(id)
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }

    @GetMapping(value = ["/profissional/{idProfissional}"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
    fun getByIdProfissional(@PathVariable idProfissional: Long): ResponseEntity<List<DescontoDto>> =
        ResponseEntity.ok(descontoService.findByIdProfissional(idProfissional).map {
           it.toDTO()
        })

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
    fun saveDiscount(@RequestBody dto: DescontoDto): ResponseEntity<DescontoDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(descontoService.saveDiscount(dto).toDTO())
    }

    @PostMapping(value = ["/current/find"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
    fun existDescontoBetweenInicioAndFim(@RequestBody dto: DescontoDto): ResponseEntity<DescontoDto> {
        return ResponseEntity.status(HttpStatus.OK).body(descontoService.findDescontoBetweenInicioAndFim(dto)?.toDTO())
    }

    @GetMapping(value = ["/current/profissional/{idProfissional}"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
    fun getVigentesByIdProfissional(@PathVariable idProfissional: Long): ResponseEntity<List<DescontoDto>> =
            ResponseEntity.ok(descontoService.findAtivosByIdProfissional(idProfissional).map {
                it.toDTO()
            })

    @DeleteMapping(value = ["/cancel/{id}"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
    fun cancelarDesconto(@PathVariable id: Long): ResponseEntity<DescontoDto> {
        if (descontoService.existsById(id)) {
            val alteredItem = descontoService.cancelarDesconto(id).toDTO()
            return ResponseEntity.ok(alteredItem)
        }
        return ResponseEntity.notFound().build()
    }
}
