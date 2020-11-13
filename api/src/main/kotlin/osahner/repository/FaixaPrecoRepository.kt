package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.FaixaPreco
import osahner.domain.TabelaPreco

interface FaixaPrecoRepository: JpaRepository<FaixaPreco, Long> {
  fun findByTotalHorasLessThanEqualOrderByTotalHorasDesc(totalHoras: Int): Collection<FaixaPreco>

  fun findByTotalHoras(i: Int): FaixaPreco

  fun findByTabelaPrecoAndTotalHorasGreaterThanEqualOrderByTotalHorasAsc(tabelaPreco: TabelaPreco, totalHoras: Int): Collection<FaixaPreco>

  fun findByTabelaPrecoAndTotalHorasLessThanEqualOrderByTotalHorasDesc(tabelaPreco: TabelaPreco, totalHoras: Int): Collection<FaixaPreco>

  fun findByTabelaPrecoAndTotalHoras(tabelaPreco: TabelaPreco, i: Int): FaixaPreco
}
