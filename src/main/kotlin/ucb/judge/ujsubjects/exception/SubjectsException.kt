package ucb.judge.ujsubjects.exception

import org.springframework.http.HttpStatus


class SubjectsException(var httpStatus: HttpStatus, message: String) : Exception(message)