package osahner.dto

import osahner.domain.Medico

data class EmpresaERepresentanteDto (
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

        var representanteLegal: Medico? = null
)
