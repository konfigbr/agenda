package osahner.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.*
import osahner.enums.StatusFatura
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "fatura")
@JsonIgnoreProperties(ignoreUnknown = true)
open class Fatura {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = null

    @Column(name = "valor", nullable = false)
    var valor: Double? = null


    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null

    @Column(name = "last_updated", nullable = false)
    var lastUpdated: LocalDateTime? = null

    @Column(name = "data_cobranca", nullable = false)
    var dataCobranca: LocalDate? = null

    @Column(name = "id_pagamento", nullable = true)
    var idPagamento: String? = null

    @Column(name = "uuid_transacao", nullable = true)
    var uuidTransacao: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: StatusFatura? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "fatura",fetch = FetchType.EAGER)
    var agendaSalaTipoEquipamento: MutableSet<AgendaSalaTipoEquipamento>? = mutableSetOf()

    @get:JsonIgnore
    @OneToMany(mappedBy = "refFaturaEntity",fetch = FetchType.EAGER)
    var refAgendamentoEntities: MutableSet<Agendamento>? = mutableSetOf()

    @get:JsonIgnore
    @OneToMany(mappedBy = "fatura", cascade = [CascadeType.ALL],fetch = FetchType.EAGER)
    var creditoUtilizado: MutableSet<CreditoUtilizado>? = mutableSetOf()

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "carrinho_compra_id", referencedColumnName = "id")
    @JsonBackReference
    var refCarrinhoCompraEntity: CarrinhoCompra? = null

    @get:JsonIgnore
    @ManyToOne
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    var medico: Medico? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "valor = $valor " +
                    "dateCreated = $dateCreated " +
                    "lastUpdated = $lastUpdated " +
                    "dataCobranca = $dataCobranca " +
                    "idPagamento = $idPagamento " +
                    "uuidTransacao = $uuidTransacao " +
                    "status = $status " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Fatura

        if (id != other.id) return false
        if (version != other.version) return false
        if (valor != other.valor) return false
        if (dateCreated != other.dateCreated) return false
        if (lastUpdated != other.lastUpdated) return false
        if (dataCobranca != other.dataCobranca) return false
        if (idPagamento != other.idPagamento) return false
        if (uuidTransacao != other.uuidTransacao) return false
        if (status != other.status) return false

        return true
    }

  fun toDTO() = FaturaDto(
    id = this.id,
    version = this.version,
    valor = this.valor,
    dateCreated = this.dateCreated,
    lastUpdated = this.lastUpdated,
    dataCobranca = this.dataCobranca,
    idPagamento = this.idPagamento,
    uuidTransacao = this.uuidTransacao,
    status = this.status,
    carrinhoCompra = this.refCarrinhoCompraEntity?.toDTO(),
    medico = this.medico?.toDTO()
  )

  companion object {
    fun fromDTO(dto: FaturaDto): Fatura {
      var fatura : Fatura = Fatura()

      fatura.id = dto.id
      fatura.version = dto.version
      fatura.valor = dto.valor
      fatura.dateCreated = dto.dateCreated
      fatura.lastUpdated = dto.lastUpdated
      fatura.dataCobranca = dto.dataCobranca
      fatura.idPagamento = dto.idPagamento
      fatura.uuidTransacao = dto.uuidTransacao
      fatura.status = dto.status

      return fatura
    }
  }
}

