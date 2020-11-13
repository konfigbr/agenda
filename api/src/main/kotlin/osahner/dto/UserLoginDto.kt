package osahner.dto

import osahner.domain.TermoDeUso

data class UserLoginDto(
        var userId: Long? = null,
        var accessType: String? = null,
        var token: String? = null,
        var passwordRecovered: Boolean? = null,
        var idProfile: Long? = null,
        var accountLocked: Boolean? = null,
        var specialty: Boolean? = null
)
