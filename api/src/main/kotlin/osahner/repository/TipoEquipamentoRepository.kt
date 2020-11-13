package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.TipoEquipamento
import osahner.domain.User
import java.util.*

interface TipoEquipamentoRepository : JpaRepository<TipoEquipamento, Long> {
  fun existsByNome(nome: String?): Boolean
  fun findByNome(nome: String?): Optional<TipoEquipamento>


  @Query(value="select te.* from tipo_equipamento te join ativo as atv on atv.tipo_equipamento_id = te.id", nativeQuery = true)
  fun findTipoEquipamentoWithAtivo(): MutableCollection<TipoEquipamento>
}
