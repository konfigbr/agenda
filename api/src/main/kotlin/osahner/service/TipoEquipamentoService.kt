package osahner.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import osahner.domain.TipoEquipamento
import osahner.dto.TipoEquipamentoDto
import osahner.repository.TipoEquipamentoRepository
import java.util.*

@Component
class TipoEquipamentoService(
  private val tipoEquipamentoRepository: TipoEquipamentoRepository
) {
  fun list(): Collection<TipoEquipamento> = tipoEquipamentoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))

  fun findById(id: Long?): Optional<TipoEquipamento> = tipoEquipamentoRepository.findById(id)

  fun findByNome(nome: String?): Optional<TipoEquipamento> = tipoEquipamentoRepository.findByNome(nome)

  fun save(dto: TipoEquipamentoDto): TipoEquipamento = tipoEquipamentoRepository.saveAndFlush(TipoEquipamento.fromDTO(dto))

  fun delete(id: Long) = tipoEquipamentoRepository.deleteById(id)

  fun alter(id: Long, tipoEquipamento: TipoEquipamentoDto) : TipoEquipamento {
    var safeTipoEquipamento = tipoEquipamento.copy(id=id)
    return save(safeTipoEquipamento)
  }

  fun existsById(id: Long) = tipoEquipamentoRepository.existsById(id)

  fun existsByNome(nome: String?) = tipoEquipamentoRepository.existsByNome(nome)
}
