package ucb.judge.ujsubjects.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import ucb.judge.ujsubjects.dto.KeycloakUserDto
import ucb.judge.ujsubjects.dto.ResponseDto

@FeignClient(name = "uj-users")
interface UjUsersService {
    @GetMapping("/api/v1/users/professors")
    fun getProfessorByKcUuid(
        @RequestParam("kcUuid", required = true) kcUuid: String,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<Long>

    @GetMapping("/api/v1/users/students")
    fun getStudentByKcUuid(
        @RequestParam("kcUuid", required = true) kcUuid: String,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<Long>

    @GetMapping("/api/v1/users/profile/{userId}")
    fun getProfile(@PathVariable userId: String, @RequestHeader("Authorization") token: String): ResponseDto<KeycloakUserDto>
}