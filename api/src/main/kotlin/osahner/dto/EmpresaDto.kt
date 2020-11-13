package osahner.dto

import osahner.domain.Especialidade
import osahner.domain.Medico
import osahner.domain.User

data class EmpresaDto (
    var id: Long? = null,

    var cnpj: String? = null,

    var nome: String? = null,

    var razaoSocial: String? = null,

    var cep: String? = null,

    var logradouro: String? = null,

    var numero: String? = null,

    var bairro: String? = null,

    var municipio: String? = null,

    var uf: String? = null,

    var situacao: String? = null,

    var responsavelTecnico: Medico? = null,

    var representanteLegal: User? = null,

    var ufcrm: String? = null,
    var crm: String? = null,
    var categoria: String? = null,
    var refEspecialidadeEntity: Especialidade? = null
)
