package osahner.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import osahner.domain.Empresa
import osahner.dto.EmpresaDto
import osahner.repository.EmpresaRepository
import java.util.*

@Component
class EmpresaService( private val empresaRepository: EmpresaRepository ) {
    fun list(): Collection<Empresa> = empresaRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))

    fun findById(id: Long?): Optional<Empresa> = empresaRepository.findById(id)

    fun findByCnpj(nome: String?): Optional<Empresa> = empresaRepository.findByCnpj(nome)

    fun save(dto: EmpresaDto): Empresa = empresaRepository.saveAndFlush(Empresa.fromDTO(dto))

    fun delete(id: Long) = empresaRepository.deleteById(id)

    fun alter(id: Long, empresa: EmpresaDto) : Empresa {
        var safeEmpresa = empresa.copy(id=id)
        return save(safeEmpresa)
    }

    fun existsById(id: Long) = empresaRepository.existsById(id)

    fun existsByCnpj(cnpj: String?) = empresaRepository.existsByCnpj(cnpj)
}
