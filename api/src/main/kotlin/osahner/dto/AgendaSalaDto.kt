package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendaSalaDto(

        var id: Long?,

        var version: Long?,

        var custo: Double?,

        var inicio: LocalDateTime?,

        var fim: LocalDateTime?,

        var fixo: Boolean?,

        var refUserEntity: Assistente?,


        var refManutencaoEntity: Manutencao?,

        var refAgendamentoEntity: Agendamento?,

        var refSalaEntity: Sala?,

        var refAgendaSalaTipoEquipamentoEntities: MutableSet<AgendaSalaTipoEquipamento>?










)
