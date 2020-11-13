package osahner.domain

import javax.persistence.*

@Entity
@Table(name = "credito_agenda_sala_tipo_equipamento")
open class CreditoAgendaSalaTipoEquipamento {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credito_agenda_sala_tipo_equipamentos_id", referencedColumnName = "id")
    var credito: Credito? = null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_sala_tipo_equipamento_id", referencedColumnName = "id")
    var agendaSalaTipoEquipamento: AgendaSalaTipoEquipamento? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "creditoAgendaSalaTipoEquipamentosId = $credito " +
                    "agendaSalaTipoEquipamentoId = $agendaSalaTipoEquipamento " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CreditoAgendaSalaTipoEquipamento

        if (credito != other.credito) return false
        if (agendaSalaTipoEquipamento != other.agendaSalaTipoEquipamento) return false

        return true
    }

}

