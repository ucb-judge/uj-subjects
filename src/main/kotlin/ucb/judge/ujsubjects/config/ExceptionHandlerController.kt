package ucb.judge.ujsubjects.config

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.ws.rs.*
import org.slf4j.LoggerFactory
import ucb.judge.ujsubjects.dto.ResponseDto
import ucb.judge.ujsubjects.exception.SubjectsException


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
}