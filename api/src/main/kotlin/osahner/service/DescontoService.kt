package osahner.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import osahner.domain.Desconto
import osahner.domain.Medico
import osahner.dto.DescontoDto
import osahner.repository.DescontoRepository
import osahner.repository.MedicoRepository
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Component
class DescontoService(
        private val descontoRepository: DescontoRepository,
        private val medicoRepository: MedicoRepository
) {
    fun list(): Collection<Desconto> = descontoRepository.findAll(Sort.by(Sort.Direction.ASC, "dataInicio"))

    fun findById(id: Long): Optional<Desconto> = descontoRepository.findById(id)

    fun save(dto: DescontoDto): Desconto {
        var desconto = Desconto.fromDTO(dto)

        return descontoRepository.save(desconto)
    }

    fun delete(id: Long) = descontoRepository.deleteById(id)

    fun alter(id: Long, descontoDto: DescontoDto): Desconto {
        var safeDesconto = descontoDto.copy(id = id)
        return save(safeDesconto)
    }

    fun existsById(id: Long) = descontoRepository.existsById(id)

    fun findByIdProfissional(idProfissional: Long): MutableList<Desconto> {
        val profissional = medicoRepository.findById(idProfissional).get()
        if (profissional == null)
            throw RuntimeException("Profissional não encontrado")

        return descontoRepository.findByRefMedicoEntityOrderByDataInicioDesc(profissional)
    }

    fun findAtivosByIdProfissional(idProfissional: Long): MutableList<Desconto> {
        val profissional = medicoRepository.findById(idProfissional).get()
        if (profissional == null)
            throw RuntimeException("Profissional não encontrado")

        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(saoPauloZone)

        return descontoRepository.findByRefMedicoEntityAndAtivo(profissional.id!!, true, now)
    }

    fun saveDiscount(dto: DescontoDto): Desconto {
        val profissionalReq = medicoRepository.findById(dto.profissional?.id)
        if (!profissionalReq.isPresent)
            return throw RuntimeException("Profissional não encontrado")

        val profissional = profissionalReq.get()


        if (dto.dataFim!! < dto.dataInicio)
            return throw RuntimeException("Data fim não pode ser menor que a data de início")

        if (dto.percentual!! > 100 || dto.percentual!! < 0)
            return throw RuntimeException("Percentual deve valer entre 0% e 100%")

        dto.profissional = profissional.toDTO()

        val descontos = descontoRepository.findByProfissionalAndAtivoAndTipoAndInicioAndFim(profissional.id!!,true, dto.tipo.toString(), dto.dataInicio!!, dto.dataFim!!)

        if (descontos.isNotEmpty())
            return throw RuntimeException("Já existe um desconto vigente desse tipo para esse período")

        val desconto = Desconto.fromDTO(dto)

        desconto.dataInicio?.with(LocalTime.of(0,0))
        desconto.dataFim?.with(LocalTime.of(23,59))

        return descontoRepository.save(desconto)
    }

    fun findDescontoBetweenInicioAndFim(dto: DescontoDto): Desconto? {
        val profissional = medicoRepository.findById(dto.profissional?.id).get()
        if (profissional == null)
            return throw RuntimeException("Profissional não encontrado")

        val descontos = descontoRepository.findByProfissionalAndAtivoAndTipoAndInicioAndFim(profissional.id!!,true, dto.tipo.toString(), dto.dataInicio!!, dto.dataFim!!)

        if (descontos.isNotEmpty())
            return descontos[0]
        else
            return null
    }

    fun cancelarDesconto(id: Long): Desconto {
        val desconto = descontoRepository.findById(id).get()
        if (desconto == null)
            return throw RuntimeException("Desconto não encontrado")

        desconto.ativo = false

        return save(desconto.toDTO())
    }

    fun findVigenteAtivosByProfissional(profissional: Medico): MutableList<Desconto> {
        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(saoPauloZone)

        return descontoRepository.findVigenteByRefMedicoEntityAndAtivo(profissional.id!!, true, now)
    }

    fun findVigenteAtivosByProfissionalAndDate(profissional: Medico, date: LocalDateTime): MutableList<Desconto> {
        return descontoRepository.findVigenteByRefMedicoEntityAndAtivo(profissional.id!!, true, date)
    }
}
