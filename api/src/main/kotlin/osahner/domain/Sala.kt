package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Proxy
import osahner.dto.SalaDto
import osahner.enums.SalaStatus
import javax.persistence.*

@Entity
@Table(name = "sala")
@JsonIgnoreProperties(ignoreUnknown = true)
@Proxy(lazy=false)
open class Sala {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = true)
    var version: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: SalaStatus? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null

    @Column(name = "reserva", nullable = false)
    var reserva: Int? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "sala", fetch = FetchType.LAZY)
    var refAgendaAssistenteEntities: MutableList<AgendaAssistente>? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "sala")
    var refAgendaSalaEntities: MutableList<AgendaSala>? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "sala", fetch = FetchType.LAZY)
    var refMensagemEntities: MutableList<Mensagem>? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especialidade_id", referencedColumnName = "id")
    var refEspecialidadeEntity: Especialidade? = null
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "localizacao_id", referencedColumnName = "id")
    var refLocalEntity: Local? = null
    // @OneToMany(mappedBy = "refSalaEntity", fetch = FetchType.LAZY)
    // var refSolicitacaoEntities: MutableList<Solicitacao>? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assistente_virtual_id", referencedColumnName = "id")
    var assistenteVirtual: Assistente? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "status = $status " +
                    "nome = $nome " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Sala

        if (id != other.id) return false
        if (version != other.version) return false
        if (status != other.status) return false
        if (nome != other.nome) return false

        return true
    }

  fun toDTO() = SalaDto(
     id = this.id,
     version = this.version,
     status = this.status,
     nome = this.nome,
     especialidade = this.refEspecialidadeEntity?.toDTO(),
     local = this.refLocalEntity?.toDTO(),
     reserva = this.reserva,
     assistenteVirtual = this.assistenteVirtual
    )

  companion object {
    fun fromDTO(dto: SalaDto): Sala {
      var sala = Sala()

      sala.id = dto.id
      sala.version = dto.version
      sala.status = dto.status
      sala.nome = dto.nome
      sala.reserva = dto.reserva
      sala.assistenteVirtual = dto.assistenteVirtual
      return sala
    }
  }
}

