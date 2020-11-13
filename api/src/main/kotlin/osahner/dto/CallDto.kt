package osahner.dto

import osahner.domain.Medico
import java.time.LocalDateTime

open class CallDto(var phone: String, var extensionPhone: String, var dateTime: LocalDateTime, var url: String?, var medico: MedicoCustomDto?) {

    override fun toString() = "Ramal $extensionPhone date $dateTime"
}