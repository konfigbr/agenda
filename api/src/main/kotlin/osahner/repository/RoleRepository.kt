package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import osahner.domain.Role
import java.util.*

interface RoleRepository : JpaRepository<Role, Long> {

  fun findByRoleName(roleName: String?): Optional<Role>

  @Query(nativeQuery = true, value =
  "select u from user u INNER JOIN FETCH u.user_role as ur" +
    "  where ur.role_id = ?1 and u.ativo = 'S'")
  fun findByRoleNameAndAtivo(roleName: String?): Optional<Role>
  fun existsByRoleName(roleName: String?): Boolean
}
