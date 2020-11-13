package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import osahner.domain.*
import java.util.*

interface TabelaPrecoRepository : JpaRepository<TabelaPreco, Long> {

    @Modifying
    @Transactional
    @Query(value="delete from faixa_preco" +
            " where tabela_preco_id = ?1"
            , nativeQuery = true)
    fun deleteFaixaPrecoByIdTabela(id: Long?): Int
}
