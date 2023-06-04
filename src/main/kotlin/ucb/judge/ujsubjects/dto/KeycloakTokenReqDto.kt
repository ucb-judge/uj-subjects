package ucb.judge.ujsubjects.dto

import feign.form.FormProperty

class KeycloakTokenReqDto {
    @FormProperty("grant_type")
    var grantType: String = "password"

    @FormProperty("client_id")
    var clientId: String = "ujsubjects"

    @FormProperty("client_secret")
    var secret: String = "mysecret"

    constructor(grantType: String, clientId: String, secret: String) {
        this.grantType = grantType
        this.clientId = clientId
        this.secret = secret
    }
}