
package osahner.service
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import osahner.config.AppConfiguration
import osahner.domain.*
import osahner.dto.*
import osahner.enums.StatusMedico
import osahner.enums.StatusTermoDeUso
import osahner.enums.TipoDocumento
import osahner.repository.*
import osahner.utils.Utils
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@Component
@Transactional
class MedicoService(
  private val userRepository: UserRepository,
  private val medicoRepository : MedicoRepository,
  private val especialidadeRepository: EspecialidadeRepository,
  private val termoDeUsoRepository: TermoDeUsoRepository,
  private val descontoService: DescontoService
  //private val poiExportService: PoiExportService

) {
  private fun getCurrentUserEmail(): String? = SecurityContextHolder.getContext().authentication.principal as String

  fun list(): Collection<Medico> = medicoRepository.findAll()

  fun listAprovacao(): Collection<Medico> = medicoRepository.findByStatus(status = StatusMedico.AGUARDANDO_APROVAÇÃO)

  @Transactional
  fun createNewProfessional(idUser: Long?, idEspecialidade: Long?, documento: String?, tipoDocumento: TipoDocumento?): Medico {
    val user = userRepository.findById(idUser).get()

    var especialidade: Especialidade? = null
    if (idEspecialidade != null)
      especialidade = especialidadeRepository.findById(idEspecialidade).get()

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val now = LocalDateTime.now(saoPauloZone)

    val professional = Medico()
    professional.user = user
    professional.status = StatusMedico.AGUARDANDO_APROVAÇÃO
    professional.ativo = "S"
    professional.refEspecialidadeEntity = especialidade
    professional.tipoDocumento = tipoDocumento
    professional.cpf = documento
    professional.dateCreated = now

    return medicoRepository.saveAndFlush(professional)
  }

  @Transactional
  fun save(dto: MedicoCustomDto): Medico {
    var especialidade = especialidadeRepository.findById(dto.especialidade?.id).get()
    var user = userRepository.findById(dto.idUser).get()
    user.nome = dto.nome
    user.username = dto.username

    if (dto.tipoDocumento == TipoDocumento.CPF && !Utils.checkCpf(dto.cpf!!))
      throw RuntimeException("CPF Inválido")
    else if (dto.tipoDocumento == TipoDocumento.CNPJ && !Utils.checkCnpj(dto.cpf!!))
      throw RuntimeException("CNPJ Inválido")

    val alteredUser =  userRepository.saveAndFlush(user)

    var medico = Medico.fromCustomDTO(dto)
    medico.refEspecialidadeEntity = especialidade
    medico.user = alteredUser

    return medicoRepository.saveAndFlush(medico)
  }

  @Transactional
  fun saveStatus(id: Long, status: StatusMedico): Medico {

    var medico: Medico = medicoRepository.findById(id).get()
    medico.status= status
    return medicoRepository.save(medico)
  }

  @Transactional
  fun alter(idProfessional: Long, medicoCustomDto: MedicoCustomDto): Medico {
    var safeProfessional = medicoCustomDto.copy(id=idProfessional)
    return save(safeProfessional)
  }

  @Transactional
  fun alterStatusAprovado(idMedico: Long): Medico {
    var status = StatusMedico.APROVADO
    return saveStatus(idMedico, status )
  }

  @Transactional
  fun alterStatusReprovado(idMedico: Long): Medico {
    var status = StatusMedico.NEGADO
    return saveStatus(idMedico, status )
  }

  fun atualizarTermo(idMedico: Long): Medico {
    var status = StatusTermoDeUso.ACEITO
    return saveStatusTermo(idMedico, status )
  }

  fun atualizarAtivo(idMedico: Long): Medico {
    var ativo = "N"
    return saveAtivo(idMedico, ativo )
  }

  fun negarTermo(idMedico: Long): Medico {
    var status = StatusTermoDeUso.RECUSADO
    return saveStatusTermo(idMedico, status )
  }

  fun saveStatusTermo(id: Long, status: StatusTermoDeUso): Medico {

    var medico: Medico = medicoRepository.findById(id).get()
    medico.termoUsoPrivacidade= status
    return medicoRepository.save(medico)
  }

  fun saveAtivo(id: Long, ativo: String): Medico {

    var medico: Medico = medicoRepository.findById(id).get()
    var user : User = userRepository.findById(medico.user?.id).get()
    user.ativo = ativo
    userRepository.save(user)
    medico.ativo= ativo
    return medicoRepository.save(medico)
  }

  fun pesquisarMedicos(especialidade: Long?): MutableList<RelatorioProfissionaisDto>{
    var medico = mutableListOf<Medico>()
    var zero: Long? = 0
   // var especialidadeMedico = especialidadeRepository.findById(especialidade).get()
    var dtoRet = mutableListOf<RelatorioProfissionaisDto>()
    if (especialidade == zero){
      medico = medicoRepository.findAll()
    }else{
      medico = medicoRepository.findByEspecialidadeEntity(especialidade)
    }

    medico.forEach {
      var dto: RelatorioProfissionaisDto = RelatorioProfissionaisDto(null,null,null,null,null,null,null)
      dto.nome = it.user?.nome
      dto.rg = it.rg
      dto.cpf = it.cpf
      dto.especialidade = it.refEspecialidadeEntity?.nome
      dto.documentoProfissional = it.crm
      dto.telefone = it.telefone
      dto.email = it.user?.username

      dtoRet.add(dto)
    }

    return dtoRet
  }

  fun existsById(id: Long) = medicoRepository.existsById(id)

  fun findById(id: Long): Optional<Medico> = medicoRepository.findById(id)

  fun findByrefUserEntity(user: User): Optional<Medico> = medicoRepository.findByUser(user)

  fun findByRefEspecialidadeEntity(especialidade: Especialidade): Collection<Medico> = medicoRepository.findByRefEspecialidadeEntity(especialidade)

  /*fun toWorkbook(especialidadeId: String?): Workbook {
    var dtos: MutableList<AddressDto> = mutableListOf()
    val result = this.pesquisarMedicos(especialidadeId?.toLong())
    val format = SimpleDateFormat("dd/MM/yyy")


    return poiExportService.buildExcelDocumentProfissionais(
      "Export Relatório Profissionais",
      listOf("nome", "rg", "cpf", "especialidade", "documentoProfissional", "telefone", "email"),
      listOf("Nome", "RG", "CPF", "Especialidade", "Documento Profissional", "Telefone", "E-mail"),
      result
    )
  }*/

  fun existsByCpf(cpf: String?): Boolean = medicoRepository.existsByCpf(cpf)

  fun existsByCrm(crm: String?): Boolean = medicoRepository.existsByCrm(crm)

  fun existsByUrl(url: String?): Boolean = medicoRepository.existsByUrl(url)

  fun getTermosDeUso() = termoDeUsoRepository.findAll()

  fun findByUrl(url: String) = medicoRepository.findByUrl(url)

  fun listWithDescontoVigente(): List<MedicoCustomDto> {
      val medicos = medicoRepository.findAll().map { it.toCustomDTO() }

      medicos.forEach {
        val descontos = descontoService.findAtivosByIdProfissional(it.id!!)

        it.hasDescontoVigente = descontos.isNotEmpty()
      }

    return medicos
  }
}

