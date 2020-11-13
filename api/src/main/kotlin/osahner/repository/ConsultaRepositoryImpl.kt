package osahner.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import osahner.domain.Consulta
import osahner.enums.StatusConsulta
import java.time.LocalDateTime

import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


@Repository
class ConsultaRepositoryImpl: ConsultaRepositoryCustom {

    @Autowired
    lateinit var em: EntityManager

    override fun findByCriteria(medicoId: Long?, dataInicio: LocalDateTime?, dataFim: LocalDateTime?, salaId: Long?, status: StatusConsulta?, pacienteId: Long?): MutableList<Consulta> {
        val cb: CriteriaBuilder = em.getCriteriaBuilder()
        var cq: CriteriaQuery<Consulta> = cb.createQuery(Consulta::class.java)
        val consulta: Root<Consulta> = cq.from(Consulta::class.java)

        val predicates: MutableList<Predicate> = mutableListOf()

        predicates.add(cb.greaterThan(consulta.get("dataInicio"), dataInicio))
        predicates.add(cb.lessThanOrEqualTo(consulta.get("dataFim"), dataFim))

        // filtros opcionais
        if (medicoId != null) {
            predicates.add(cb.equal(consulta.get<Any>("refMedicoEntity").get<Any>("id"), medicoId))
        }

        if (salaId != null) {
            predicates.add(cb.equal(consulta
                    .get<Any>("refAgendamentoEntity")
                    .get<Any>("solicitacaoReserva")
                    .get<Any>("refSalaEntity").get<Any>("id"), salaId))
        }

        if (status != null) {
            predicates.add(cb.equal(consulta.get<Any>("status"), status))
        }

        if (pacienteId != null) {
            predicates.add(cb.equal(consulta.get<Any>("refPacienteEntity").get<Any>("id"), pacienteId))
        }

        cq.where(*predicates.toTypedArray())

        return em.createQuery(cq).resultList
    }
}

