package ucb.judge.ujsubjects.config

import feign.FeignException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.ws.rs.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import ucb.judge.ujsubjects.dto.ResponseDto
import ucb.judge.ujsubjects.exception.SubjectsException
import javax.servlet.http.HttpServletResponse


@ControllerAdvice
class ExceptionHandlerController {
    companion object {
        private val logger = LoggerFactory.getLogger(ExceptionHandlerController::class.java.name)
    }

    @ExceptionHandler(SubjectsException::class)
    fun handleUJUsersException(ex: SubjectsException): ResponseEntity<ResponseDto<Nothing>> {
        logger.error("Error message: ${ex.message}")
        return ResponseEntity(ResponseDto(null, ex.message!!, false), ex.httpStatus)
    }

    @ExceptionHandler(FeignException::class)
    fun handleFeignException(ex: FeignException): ResponseEntity<ResponseDto<Nothing>> {
        logger.error("Error message: ${ex.message}")
        val http = mapOf(
                400 to HttpStatus.BAD_REQUEST,
                401 to HttpStatus.UNAUTHORIZED,
                403 to HttpStatus.FORBIDDEN,
                404 to HttpStatus.NOT_FOUND,
                500 to HttpStatus.INTERNAL_SERVER_ERROR,
        )

        return ResponseEntity(ResponseDto(null, ex.message!!, false),http[ex.status()]!!)
    }
}