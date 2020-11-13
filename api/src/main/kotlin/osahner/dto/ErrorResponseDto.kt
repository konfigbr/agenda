package osahner.dto

class ErrorResponseDto(
        val message: String,
        val timestamp: Long,
        val errors: List<String> = emptyList())
