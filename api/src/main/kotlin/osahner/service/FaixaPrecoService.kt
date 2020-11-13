package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.FaixaPreco
import osahner.domain.TipoEquipamento
import osahner.dto.FaixaPrecoDto
import osahner.dto.TipoEquipamentoDto
import osahner.repository.FaixaPrecoRepository
import osahner.repository.TipoEquipamentoRepository
import java.util.*

@Component
class FaixaPrecoService(
  private val faixaPrecoRepository: FaixaPrecoRepository
) {
  fun list(): Collection<FaixaPreco> = faixaPrecoRepository.findAll()

  fun findById(id: Long?): Optional<FaixaPreco> = faixaPrecoRepository.findById(id)

  fun save(dto: FaixaPrecoDto): FaixaPreco = faixaPrecoRepository.save(FaixaPreco.fromDTO(dto))

  fun delete(id: Long) = faixaPrecoRepository.deleteById(id)

  fun alter(id: Long, faixaPreco: FaixaPrecoDto) : FaixaPreco {
    var safeFaixaPreco = faixaPreco.copy(id=id)
    return save(safeFaixaPreco)
  }

  fun existsById(id: Long) = faixaPrecoRepository.existsById(id)


}
