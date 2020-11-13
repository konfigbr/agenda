package osahner.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.SerializationFeature
import osahner.dto.SolicitacaoDto
import osahner.dto.TipoEquipamentoDto
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "solicitacao_equipamento")
@JsonIgnoreProperties(ignoreUnknown = true)
open class SolicitacaoEquipamento  : SolicitacaoAbstractJpa<Long>() {


    @get:JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tipo_equipamento_id", referencedColumnName = "id", insertable = true, updatable = true)
    var tipoEquipamento: TipoEquipamento? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "fim = $fim " +

                    "dateCreated = $dateCreated " +
                    "custo = $custo " +

                    "status = $status " +
                    "inicio = $inicio " +

                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42



}

