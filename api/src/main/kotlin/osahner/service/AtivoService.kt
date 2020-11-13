package osahner.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import osahner.domain.Ativo
import osahner.domain.Sala
import osahner.dto.AtivoDto
import osahner.repository.AtivoRepository
import osahner.repository.TipoEquipamentoRepository
import java.util.*

@Component
class AtivoService(
  private val ativoRepository: AtivoRepository,
  private val tipoEquipamentoService: TipoEquipamentoService
) {
  fun list(): Collection<Ativo> = ativoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))

  fun findById(id: Long): Optional<Ativo> = ativoRepository.findById(id)

  fun save(dto: AtivoDto): Ativo {

    var tipoEquipamento = tipoEquipamentoService.findById(dto.tipoEquipamento?.id).get()

    var ativo = Ativo.fromDTO(dto)

    ativo.tipoEquipamento = tipoEquipamento

    return ativoRepository.saveAndFlush(ativo)
  }

  fun delete(id: Long) = ativoRepository.deleteById(id)

  fun alter(id: Long, ativoDto: AtivoDto) : Ativo {
    var safeAtivo = ativoDto.copy(id=id)
    return save(safeAtivo)
  }

  fun existsById(id: Long) = ativoRepository.existsById(id)
}
