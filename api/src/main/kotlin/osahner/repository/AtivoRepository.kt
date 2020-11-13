package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.Ativo
import osahner.domain.TipoEquipamento

interface AtivoRepository : JpaRepository<Ativo, Long> {
   @Query(value="SELECT count(b) FROM Ativo b WHERE b.tipoEquipamento = ?1")
   fun countByTipoEquipamento(tipoEquipamento: TipoEquipamento?): Long
   fun findByTipoEquipamentoOrderById(tipoEquipamento: TipoEquipamento): MutableList<Ativo>

}
