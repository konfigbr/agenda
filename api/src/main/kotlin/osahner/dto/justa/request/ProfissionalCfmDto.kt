package osahner.dto.justa.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class ProfissionalCfmDto  {
    var inscricao : String? = null
    var uf : String? = null
}
