package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import osahner.domain.Agendamento
import osahner.domain.Consulta
import osahner.domain.Medico
import osahner.domain.Paciente
import osahner.enums.StatusConsulta
import java.time.LocalDateTime

@Repository
interface ConsultaRepository : JpaRepository<Consulta, Long>, ConsultaRepositoryCustom {
    fun findByStatus(status: StatusConsulta) : MutableList<Consulta>

    fun findByRefPacienteEntityAndRefMedicoEntityOrderByDataInicioDesc(refPacienteEntity: Paciente, refMedicoEntity: Medico) : MutableList<Consulta>

    fun findByRefMedicoEntity(medico: Medico): MutableList<Consulta>

    @Query(value="select con.* from consulta as con" +
            " where con.medico_id = ?1 and con.data_inicio >= ?2 and con.data_fim <= ?3 and con.status != 'CANCELADA'" +
            " order by con.data_inicio", nativeQuery = true)
    fun findByRefMedicoEntityAndInicioAndFim(medicoId: Long?, inicio:LocalDateTime?, fim: LocalDateTime?): MutableList<Consulta>?

    fun findByRefAgendamentoEntity(agendamento: Agendamento): MutableList<Consulta>

    @Query(value="select con.* from consulta as con" +
            " where con.data_inicio >= ?1" +
            " order by con.data_inicio", nativeQuery = true)
    fun findByNumeroDias(dataLimite: LocalDateTime?): MutableList<Consulta>?

    @Query(value="select con.* from consulta as con" +
            " where con.data_inicio >= ?1 and con.medico_id = ?2" +
            " order by con.data_inicio", nativeQuery = true)
    fun findByNumeroDiasAndMedico(dataLimite: LocalDateTime?, idMedico: Long?): MutableList<Consulta>?

    @Query(value="select con.* from consulta as con" +
            " where con.data_inicio >= ?1 and con.medico_id = ?2 and con.status = 'SOLICITADA'" +
            " order by con.data_inicio", nativeQuery = true)
    fun findSolicitadaByMedico(dataLimite: LocalDateTime?, idMedico: Long?): MutableList<Consulta>?

    @Query(value="select con.* from consulta as con" +
            " where con.data_inicio >= ?1 and con.status = 'SOLICITADA'" +
            " order by con.data_inicio", nativeQuery = true)
    fun findSolicitadas(dataLimite: LocalDateTime?): MutableList<Consulta>?
}