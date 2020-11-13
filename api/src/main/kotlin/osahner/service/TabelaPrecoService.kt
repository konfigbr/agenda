package osahner.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import osahner.domain.FaixaPreco
import osahner.domain.TabelaPreco
import osahner.dto.FaixaPrecoDto
import osahner.dto.TabelaPrecoDto
import osahner.repository.FaixaPrecoRepository
import osahner.repository.TabelaPrecoRepository
import java.util.*

@Component
class TabelaPrecoService(
        private val tabelaPrecoRepository: TabelaPrecoRepository,
        private val faixaPrecoRepository: FaixaPrecoRepository
) {
    fun list(): Collection<TabelaPreco> = tabelaPrecoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))

    fun findById(id: Long?): Optional<TabelaPreco> = tabelaPrecoRepository.findById(id)

    fun save(dto: TabelaPrecoDto): TabelaPreco {

        var tabelaPrecoDto = TabelaPrecoDto (
            id = dto.id,
            nome = dto.nome,
            faixaPrecos = mutableListOf()
        )
        var tabelaPreco = tabelaPrecoRepository.save(TabelaPreco.fromDTO(tabelaPrecoDto))

        tabelaPrecoRepository.deleteFaixaPrecoByIdTabela(tabelaPreco.id)

        var faixasPreco = mutableListOf<FaixaPreco>()
        dto.faixaPrecos?.forEach {
            val faixaPrecoDto = FaixaPrecoDto (
                totalHoras = it?.totalHoras,
                valor = it?.valor,
                tabelaPreco = tabelaPreco
            )
            val faixa = faixaPrecoRepository.save(FaixaPreco.fromDTO(faixaPrecoDto))
            faixasPreco.add(faixa)
        }

        tabelaPreco.faixaPrecos.addAll(faixasPreco)
        return tabelaPreco
    }

    fun delete(id: Long) = tabelaPrecoRepository.deleteById(id)

    fun alter(id: Long, tabelaPreco: TabelaPrecoDto) : TabelaPreco {
        var safeTabelaPreco = tabelaPreco.copy(id=id)
        return save(safeTabelaPreco)
    }

    fun existsById(id: Long) = tabelaPrecoRepository.existsById(id)
}
