package osahner.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.domain.Consulta
import osahner.domain.Medico
import osahner.dto.ConsultaDto
import osahner.dto.CreateConsultaDto
import osahner.dto.TrocaStatusConsultaDto
import osahner.enums.StatusConsulta
import osahner.repository.MedicoRepository
import osahner.service.ConsultaService
import osahner.service.PacienteService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.validation.Valid

@RestController
@RequestMapping("/medical-appointments")
class ConsultaController (
        private val consultaService: ConsultaService,
        private val medicoRepository: MedicoRepository,
        private val pacienteService: PacienteService

) {
    @GetMapping(value = ["", "/"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
    fun listMedicalAppointments(
            @RequestParam medicoId: Long?,
            @RequestParam(required = true) dataInicio: String,
            @RequestParam(required = true) dataFim: String,
            @RequestParam salaId: Long?,
            @RequestParam status: StatusConsulta?,
            @RequestParam pacienteId: Long?
    ): ResponseEntity<MutableList<ConsultaDto>> {
        val consultas = consultaService.listMedicalAppointments(
                medicoId,
                LocalDateTime.parse(dataInicio, DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.parse(dataFim, DateTimeFormatter.ISO_DATE_TIME),
                salaId,
                status,
                pacienteId
        ).map { it.toDTO() }.toMutableList()

        return ResponseEntity.ok(consultas)
    }

    @GetMapping(value = ["/{id}"])
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    fun getById(@PathVariable id: Long): ResponseEntity<ConsultaDto?> = ResponseEntity.ok(consultaService.findById(id)?.toDTO())

    @GetMapping(value = ["/professional/{idProfessional}"])
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    fun getByPacientLoggedAndProfessional(@PathVariable idProfessional: Long): ResponseEntity<Collection<ConsultaDto>> =
            ResponseEntity.ok(consultaService.findByPatientLoggedAndProfessional(idProfessional).map { it.toDTO() })

    @DeleteMapping(value = ["/cancelAppointment/{idAppointment}"])
    @PreAuthorize("hasAnyAuthority('ROLE_PACIENTE','ROLE_PROFISSIONAL','ROLE_ATENDENTE','ROLE_ADMINISTRADOR')")
    fun cancelAppointment(@PathVariable idAppointment: Long): Any? =
            ResponseEntity.ok(consultaService.cancelAppointment(idAppointment))

    @GetMapping(value = ["/busyTimes"])
    fun findHorariosOcupadosByProfissional(@RequestParam idProfissional: Long, @RequestParam inicio: String, @RequestParam fim: String): Any? {
        val professional = medicoRepository.findById(idProfissional).get()
        val dataInicio = LocalDateTime.parse(inicio,DateTimeFormatter.ISO_DATE_TIME)
        val dataFim = LocalDateTime.parse(fim,DateTimeFormatter.ISO_DATE_TIME)

        if(professional == null)
            return ResponseEntity.notFound().build<Medico>()

        return consultaService.findHorariosOcupadosByProfissional(professional, dataInicio, dataFim)
    }

    @GetMapping(value = ["/availableTimes"])
    fun findHorariosDisponiveisByProfissional(@RequestParam idProfissional: Long, @RequestParam inicio: String, @RequestParam fim: String): Any? {
        val professional = medicoRepository.findById(idProfissional).get()
        val dataInicio = LocalDateTime.parse(inicio,DateTimeFormatter.ISO_DATE_TIME)
        val dataFim = LocalDateTime.parse(fim,DateTimeFormatter.ISO_DATE_TIME)

        if(professional == null)
            return ResponseEntity.notFound().build<Medico>()

        return consultaService.findHorariosDisponiveisByProfissional(professional, dataInicio, dataFim)
    }

    @GetMapping(value = ["/with-limit"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
    fun findWithLimite(): ResponseEntity<Collection<ConsultaDto>> =
            ResponseEntity.ok(consultaService.findWithLimite()?.map { it.toDTO() })

    @GetMapping(value = ["/with-limit/professional"])
    @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL')")
    fun findWithLimiteByProfissional(): ResponseEntity<Collection<ConsultaDto>> =
            ResponseEntity.ok(consultaService.findWithLimiteByProfissional()?.map { it.toDTO() })

    @GetMapping(value = ["/statusRequested"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ATENDENTE')")
    fun findByStatusRequestedSolicitada(): ResponseEntity<Collection<ConsultaDto>> =
            ResponseEntity.ok(consultaService.findSolicitadas()?.map { it.toDTO() })

    @GetMapping(value = ["/statusRequested/me"])
    @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL')")
    fun findByStatusRequestedSolicitadaByProfissional(): ResponseEntity<Collection<ConsultaDto>> =
            ResponseEntity.ok(consultaService.findSolicitadaByProfissional()?.map { it.toDTO() })

    @PutMapping(value = ["/changeStatus/{idAppointment}"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
    fun changeStatus(@PathVariable idAppointment: Long, @RequestBody dto: TrocaStatusConsultaDto): Any {
        if (consultaService.existsById(idAppointment))
            return ResponseEntity.ok(consultaService.changeStatus(idAppointment,dto))
        else
            return ResponseEntity.notFound().build<Unit>()
    }

    @PutMapping(value = ["/rescheduling/{idConsulta}"])
    @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL', 'ROLE_ATENDENTE', 'ROLE_ADMINISTRADOR')")
    fun reschedulingMedicalAppointment(@PathVariable idConsulta: Long, @Valid @RequestBody createConsultaDto: CreateConsultaDto) :
            ResponseEntity<ConsultaDto> {
        val consulta = consultaService.reschedulingMedicalAppointment(idConsulta, createConsultaDto)
        return ResponseEntity.ok(consulta.toDTO())
    }

    @PostMapping(value = ["/{medicoId}/with-patient"])
    @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL', 'ROLE_ATENDENTE', 'ROLE_ADMINISTRADOR')")
    fun saveMedicalAppointment(@PathVariable medicoId: Long, @Valid @RequestBody createConsultaDto: CreateConsultaDto) :
            ResponseEntity<ConsultaDto> {
        val paciente = pacienteService.findById(createConsultaDto.pacienteId).get()

        val consulta = consultaService.saveMedicalAppointment(medicoId, createConsultaDto, paciente)
        return ResponseEntity.ok(consulta.toDTO())
    }
}