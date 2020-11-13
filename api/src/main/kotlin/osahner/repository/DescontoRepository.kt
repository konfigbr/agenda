package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import osahner.domain.*
import osahner.enums.StatusConsulta
import osahner.enums.TipoDesconto
import java.time.LocalDateTime

@Repository
interface DescontoRepository : JpaRepository<Desconto, Long> {
    fun findByRefMedicoEntityOrderByDataInicioDesc(profissional: Medico): MutableList<Desconto>

    @Query(value="select * from desconto d" +
            " where d.medico_id  = ?1" +
            " and d.ativo = ?2" +
            " and d.data_fim >= ?3", nativeQuery = true)
    fun findByRefMedicoEntityAndAtivo(idProfissional: Long, ativo: Boolean, data: LocalDateTime): MutableList<Desconto>

    @Query(value="select * from desconto " +
            " where medico_id  = ?1 " +
            " and ativo = ?2 " +
            " and tipo = ?3 " +
            " and ((data_inicio <= ?4 and data_fim >= ?4) or (data_inicio <= ?5 and data_fim >= ?5))", nativeQuery = true)
    fun findByProfissionalAndAtivoAndTipoAndInicioAndFim(idProfissional: Long, ativo: Boolean, tipo: String, inicio: LocalDateTime, fim: LocalDateTime): MutableList<Desconto>

    @Query(value="select * from desconto d" +
            "" +
            " where d.medico_id  = ?1" +
            " and d.ativo = ?2" +
            " and d.data_inicio <= ?3" +
            " and d.data_fim >= ?3", nativeQuery = true)
    fun findVigenteByRefMedicoEntityAndAtivo(idProfissional: Long, ativo: Boolean, data: LocalDateTime): MutableList<Desconto>
}