package ucb.judge.ujsubjects.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import ucb.judge.ujsubjects.dto.KeycloakTokenReqDto
import ucb.judge.ujsubjects.dto.KeycloakTokenResDto
import javax.ws.rs.core.MediaType

@FeignClient(name = "keycloak", url = "\${keycloak.auth-server-url}/realms/\${keycloak.realm}")
interface KeycloakService {
    @PostMapping("/protocol/openid-connect/token", consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED))
    fun getToken(tokenReqDto: KeycloakTokenReqDto) : KeycloakTokenResDto
}