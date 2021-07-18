package com.zpi.token.authorizationRequest


import com.zpi.token.api.authorizationRequest.ResponseDTO
import com.zpi.token.domain.TokenService
import com.zpi.token.domain.WebClientRepository
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class RequestUT extends Specification {
    def clientRepository = Mock(WebClientRepository)

    @Subject
    private TokenService tokenService = new TokenService(clientRepository)

    def "should return auth code when request is valid"() {
        given:
            def request = CommonFixtures.correctRequest()
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            response.getStatusCode() == HttpStatus.OK

        and:
            def responseBody = response.getBody() as ResponseDTO

            responseBody.getCode().length() != 0
            responseBody.getState() == CommonFixtures.defaultState
    }
}
