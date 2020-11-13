package osahner.dto

import osahner.domain.TabelaPreco
import osahner.enums.TipoAceiteEspecialidade


data class EspecialidadeDto (
  var id: Long? = null,

  var version: Long? = null,

  var nome: String? = null,

  var tabelaPreco: TabelaPreco? = null,

  var exibirCadastroProfissional: Boolean? = null,

  var tipoAceite: TipoAceiteEspecialidade? = null
)
