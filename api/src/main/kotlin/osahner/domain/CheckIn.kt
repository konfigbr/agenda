package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Proxy
import osahner.dto.AgendaSalaDto
import osahner.dto.AgendamentoDto
import osahner.dto.CheckinDto
import osahner.enums.StatusCheckin
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "checkin")
open class CheckIn {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: StatusCheckin? = null

    @Column(name = "horario", nullable = false)
    var horario: LocalDateTime? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    var createUser: User? = null

    @get:JsonIgnore
    @ManyToOne
    @JoinColumn(name = "agendamento_sala_id", referencedColumnName = "id", nullable = false, insertable = true, updatable = true)
    var agendamento: AgendaSala? = null




  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CheckIn

    if (id != other.id) return false
    if (status != other.status) return false
    if (horario != other.horario) return false
    if (agendamento != other.agendamento) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + (status?.hashCode() ?: 0)
    result = 31 * result + (horario?.hashCode() ?: 0)
    result = 31 * result + (agendamento?.hashCode() ?: 0)
    return result
  }





}

