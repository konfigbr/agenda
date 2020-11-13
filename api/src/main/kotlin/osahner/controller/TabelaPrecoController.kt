package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.domain.FaixaPreco
import osahner.dto.FaixaPrecoDto
import osahner.dto.TabelaPrecoDto
import osahner.dto.TipoEquipamentoDto
import osahner.service.FaixaPrecoService
import osahner.service.TabelaPrecoService

@RestController
@RequestMapping("/prices-list")
class TabelaPrecoController(private val tabelaPrecoService: TabelaPrecoService) {

    @GetMapping(value = ["", "/"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun list() = tabelaPrecoService.list().map { it.toDTO() }

    @GetMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun getById(@PathVariable id: Long): ResponseEntity<TabelaPrecoDto> = tabelaPrecoService.findById(id).map {
        ResponseEntity.ok(it.toDTO())
    }.orElse(ResponseEntity.notFound().build())

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun save(@RequestBody dto: TabelaPrecoDto) = ResponseEntity.status(HttpStatus.CREATED).body(tabelaPrecoService.save(dto).toDTO())


    @PutMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun update(@PathVariable id: Long, @RequestBody dto: TabelaPrecoDto): Any? {
        if (tabelaPrecoService.existsById(id)){
            val alteredItem = tabelaPrecoService.alter(id, dto).toDTO()
            return ResponseEntity.ok(alteredItem)
        } else
            return ResponseEntity.notFound().build<Unit>()
    }

    @DeleteMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
        if (tabelaPrecoService.existsById(id)){
            tabelaPrecoService.delete(id)
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }
}
