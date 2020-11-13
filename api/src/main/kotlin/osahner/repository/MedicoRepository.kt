package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.*
import osahner.enums.StatusMedico
import java.util.*

interface MedicoRepository : JpaRepository<Medico, Long> {
  fun findByUser(user: User?): Optional<Medico>

  fun findByUrl(url: String?): Optional<Medico>

  fun findByRefEspecialidadeEntity(especialidade: Especialidade?): Collection<Medico>

  @Query(value="select * from medico a " +
    " where a.especialidade_id = ?1 "
    , nativeQuery = true)

  fun findByEspecialidadeEntity(especialidade: Long?): MutableList<Medico>

  @Query(value="select a.* from medico a" +
          " inner join user u on u.id = a.user_id" +
          " inner join empresa_medico em on em.medico_id = a.id" +
          " where a.ativo = 'S'" +
          " and em.empresa_id = ?1" +
          " order by u.nome"
          , nativeQuery = true)
  fun findByEmpresa(empresaId: Long?): MutableList<Medico>

  @Query(value="select a.* from medico a" +
          " inner join user u on u.id = a.user_id" +
          " inner join empresa e on e.responsavel_id = a.id" +
          " where a.ativo = 'S'" +
          " and e.id = ?1"
          , nativeQuery = true)
  fun findResponsavelByEmpresa(empresaId: Long?): Optional<Medico>

  fun findByStatus(status: StatusMedico): Collection<Medico>

  @Query(value="select a.* from medico a" +
    " inner join user u on u.id = a.user_id" +
    " where a.ativo = 'S'" +
    " order by u.nome"
    , nativeQuery = true)
  override fun findAll(): MutableList<Medico>

  @Query(value="select * from medico a" +
    " where a.ativo = 'S'" +
    " and a.id = ?1"
    , nativeQuery = true)
  override fun findById(id: Long?): Optional<Medico>

  fun existsByCpf(cpf: String?): Boolean

  fun existsByCrm(crm: String?): Boolean

  fun existsByUrl(url: String?): Boolean

  fun findByRamal(ramal: String): Optional<Medico>
}
