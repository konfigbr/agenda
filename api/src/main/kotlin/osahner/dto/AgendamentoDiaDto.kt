package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendamentoDiaDto(

        var nomeSala: String?,

        var inicio: String?,

        var fim: String?,

        var nomeAssistente: String?,

        var profissional: String?,

        var especialidade: String?
)
