package osahner.controller.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import osahner.dto.ErrorResponseDto


@RestControllerAdvice
class GlobalErrorHandler {
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ErrorResponseDto> {
        val response = ErrorResponseDto(ex.message ?: "Ocorreu um erro desconhecido ao processar a sua requisição", System.currentTimeMillis())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDto> {
        val response = ErrorResponseDto("Erros de validação. Preencha todos os campos corretamente", System.currentTimeMillis(), ex.bindingResult.fieldErrors.map { it.defaultMessage })
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }
}
