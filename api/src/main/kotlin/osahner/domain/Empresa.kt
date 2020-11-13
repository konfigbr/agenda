package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.EmpresaDto
import javax.persistence.*

@Entity
@Table(name = "empresa")
@JsonIgnoreProperties(ignoreUnknown = true)
open class Empresa {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "cnpj", nullable = false)
    var cnpj: String? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null

    @Column(name = "razaoSocial", nullable = false)
    var razaoSocial: String? = null

    @Column(name = "cep", nullable = false)
    var cep: String? = null

    @Column(name = "logradouro", nullable = false)
    var logradouro: String? = null

    @Column(name = "numero", nullable = false)
    var numero: String? = null

    @Column(name = "bairro", nullable = false)
    var bairro: String? = null

    @Column(name = "municipio", nullable = false)
    var municipio: String? = null

    @Column(name = "uf", nullable = false)
    var uf: String? = null

    @Column(name = "situacao", nullable = false)
    var situacao: String? = null

    @ManyToOne
    @JoinColumn(name = "responsavel_id", referencedColumnName = "id", nullable = true)
    var responsavelTecnico: Medico? = null

    @ManyToOne
    @JoinColumn(name = "representante_id", referencedColumnName = "id", nullable = true)
    var representanteLegal: User? = null

    @ManyToOne
    @JoinColumn(name = "especialidade_id", referencedColumnName = "id", nullable = false)
    var refEspecialidadeEntity: Especialidade? = null

    @Column(name = "categoria", nullable = true)
    var categoria: String? = null

    @Column(name = "ufcrm", nullable = true)
    var ufcrm: String? = null

    @Column(name="crm", nullable = true)
    var crm: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Empresa

        if (id != other.id) return false
        if (cnpj != other.cnpj) return false
        if (nome != other.nome) return false
        if (razaoSocial != other.razaoSocial) return false
        if (cep != other.cep) return false
        if (logradouro != other.logradouro) return false
        if (numero != other.numero) return false
        if (bairro != other.bairro) return false
        if (municipio != other.municipio) return false
        if (uf != other.uf) return false
        if (situacao != other.situacao) return false
        if (responsavelTecnico != other.responsavelTecnico) return false
        if (representanteLegal != other.representanteLegal) return false
        if (refEspecialidadeEntity != other.refEspecialidadeEntity) return false
        if (categoria != other.categoria) return false
        if (ufcrm != other.ufcrm) return false
        if(crm != other.crm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (cnpj?.hashCode() ?: 0)
        result = 31 * result + (nome?.hashCode() ?: 0)
        result = 31 * result + (razaoSocial?.hashCode() ?: 0)
        result = 31 * result + (cep?.hashCode() ?: 0)
        result = 31 * result + (logradouro?.hashCode() ?: 0)
        result = 31 * result + (numero?.hashCode() ?: 0)
        result = 31 * result + (bairro?.hashCode() ?: 0)
        result = 31 * result + (municipio?.hashCode() ?: 0)
        result = 31 * result + (uf?.hashCode() ?: 0)
        result = 31 * result + (situacao?.hashCode() ?: 0)
        result = 31 * result + (responsavelTecnico?.hashCode() ?: 0)
        result = 31 * result + (representanteLegal?.hashCode() ?: 0)
        result = 31 * result + (refEspecialidadeEntity?.hashCode() ?: 0)
        result = 31 * result + (categoria?.hashCode() ?: 0)
        result = 31 * result + (ufcrm?.hashCode() ?: 0)
        result = 31 * result + (crm?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Empresa(id=$id, cnpj=$cnpj, nome=$nome, razaoSocial=$razaoSocial, cep=$cep, logradouro=$logradouro, numero=$numero, bairro=$bairro, municipio=$municipio, uf=$uf, situacao=$situacao, responsavelTecnico=$responsavelTecnico, representanteLegal=$representanteLegal, categoria=$categoria, ufcrm=$ufcrm, crm=$crm, refEspecialidadeEntity=$refEspecialidadeEntity)"
    }

    companion object {
        fun fromDTO(dto: EmpresaDto): Empresa {
            var empresa = Empresa()
            empresa.id = dto.id
            empresa.cnpj = dto.cnpj
            empresa.nome = dto.nome
            empresa.razaoSocial = dto.razaoSocial
            empresa.cep = dto.cep
            empresa.logradouro = dto.logradouro
            empresa.numero = dto.nome
            empresa.bairro = dto.bairro
            empresa.municipio = dto.municipio
            empresa.uf = dto.uf
            empresa.situacao = dto.situacao
            empresa.responsavelTecnico = dto.responsavelTecnico
            empresa.representanteLegal = dto.representanteLegal
            empresa.refEspecialidadeEntity = dto.refEspecialidadeEntity
            empresa.categoria = dto.categoria
            empresa.ufcrm = dto.ufcrm
            empresa.crm = dto.crm

            return empresa
        }
    }

    fun toDTO() = EmpresaDto(
        id = this.id,
        cnpj = this.cnpj,
        nome = this.nome,
        razaoSocial = this.razaoSocial,
        cep = this.cep,
        logradouro = this.logradouro,
        numero = this.nome,
        bairro = this.bairro,
        municipio = this.municipio,
        uf = this.uf,
        situacao = this.situacao,
        responsavelTecnico = this.responsavelTecnico,
        representanteLegal = this.representanteLegal,
        categoria = this.categoria,
        ufcrm = this.ufcrm,
        refEspecialidadeEntity = this.refEspecialidadeEntity,
        crm = this.crm
    )
}