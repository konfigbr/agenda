package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.AgendaSala
import osahner.domain.Agendamento
import osahner.domain.Assistente
import osahner.domain.User
import osahner.dto.UserAdmAssistenteDto
import java.util.*

interface UserRepository : JpaRepository<User, Long> {

  @Query(value="SELECT u.* FROM user u " +
    "where u.ativo = 'S' and u.username = ?1", nativeQuery = true)
  fun findByUsernameAtivo(username: String?): Optional<User>

  fun findByUsername(username: String?): Optional<User>

  fun existsByUsername(username: String?): Boolean



  @Query(value="SELECT u.*" +
    " FROM user u" +
    " Join user_role ur on (u.id = ur.user_id)" +
    " join role  r on ( ur.role_id = r.id)" +
    " where u.ativo = 'S' and (r.role_name = 'ROLE_ADMINISTRADOR' or r.role_name = 'ROLE_ATENDENTE') order by u.nome", nativeQuery = true)
   fun findAdmAndAtendente(): MutableList<User>

  @Query(value="SELECT u.*" +
    " FROM user u" +
    " Join user_role ur on (u.id = ur.user_id)" +
    " join role  r on ( ur.role_id = r.id)" +
    " where (r.role_name = 'ROLE_ADMINISTRADOR' or r.role_name = 'ROLE_ATENDENTE')" +
    " and u.ativo = 'S' and u.id = ?1" +
    " order by u.nome", nativeQuery = true)
  fun findAdmOrAtendenteById(id: Long): Optional<User>

  @Query(value="select * from user a" +
    " where a.ativo = 'S'" +
    " order by a.nome"
    , nativeQuery = true)
  override fun findAll(): MutableList<User>

  @Query(value="select * from user a" +
    " where a.ativo = 'S'" +
    " and a.id = ?1" +
    " order by a.nome"
    , nativeQuery = true)
  override fun findById(id: Long?): Optional<User>
}

