package com.ucbjudge.ujsubjects.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.ws.rs.*
import com.fasterxml.jackson.module.kotlin.readValue
import com.ucbjudge.ujsubjects.dto.ResponseDto
import com.ucbjudge.ujsubjects.exception.SubjectsException
import org.slf4j.LoggerFactory


@ControllerAdvice
class ExceptionHandlerController {
    companion object {
        private val logger = LoggerFactory.getLogger(ExceptionHandlerController::class.java.name)
    }

    val objectMapper = jacksonObjectMapper()

    @ExceptionHandler(SubjectsException::class)
    fun handleUJUsersException(ex: SubjectsException): ResponseEntity<ResponseDto<Nothing>> {
        logger.error("Error message: ${ex.message}")
        return ResponseEntity(ResponseDto(null, ex.message!!, false), ex.httpStatus)
    }
}