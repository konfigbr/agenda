package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.EmpresaDto
import osahner.service.EmpresaService

@RestController
@RequestMapping("/company")
class EmpresaController(private val empresaService: EmpresaService) {

    @GetMapping(value = ["", "/"])
    fun list() = empresaService.list().map { it.toDTO() }

    @GetMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun getById(@PathVariable id: Long): ResponseEntity<EmpresaDto>? = empresaService.findById(id).map {
        ResponseEntity.ok(it.toDTO())
    }.orElse(ResponseEntity.notFound().build())

    @PutMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun update(@PathVariable id: Long, @RequestBody dto: EmpresaDto): Any? {
        if (empresaService.existsById(id)){
            val empresa = empresaService.findByCnpj(dto.cnpj)

           /* if (empresa.isPresent && empresa.get().id != id)
                return ResponseEntity.badRequest().body("Já existe uma empresa com este cnpj: " + dto.cnpj)
            */
            val alteredItem = empresaService.alter(id, dto).toDTO()
            return ResponseEntity.ok(alteredItem)

        } else
            return ResponseEntity.notFound().build<Unit>()
    }

    @DeleteMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
        if (empresaService.existsById(id)){
            empresaService.delete(id)
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }

    @PostMapping
    fun saveCompanyWithRepresentative(@RequestBody dto: EmpresaDto): Any? {
        if (empresaService.existsByCnpj(dto.cnpj)) {
            val empresa = empresaService.findByCnpj(dto.cnpj).get()

            val alteredItem = empresaService.alter(empresa.id!!, dto).toDTO()
            return ResponseEntity.ok(alteredItem)
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.save(dto).toDTO())
        }
    }
}
