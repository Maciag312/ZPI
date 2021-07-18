package com.zpi.token.authorizationRequest

import com.zpi.token.api.authorizationRequest.ErrorResponseDTO
import com.zpi.token.api.authorizationRequest.ResponseDTO
import com.zpi.token.domain.TokenService
import com.zpi.token.domain.WebClientRepository
import com.zpi.token.domain.authorizationRequest.request.RequestErrorType
import com.zpi.user.api.UserDTO
import com.zpi.user.domain.EndUserService
import com.zpi.utils.BasicAuth
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class RequestUT extends Specification {
    def clientRepository = Mock(WebClientRepository)
    def userService = Mock(EndUserService)

    @Subject
    private TokenService tokenService = new TokenService(clientRepository, userService)

    def "should return auth code when request is valid"() {
        given:
            def request = CommonFixtures.correctRequest()

            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            userService.isUserAuthorized(_ as UserDTO) >> true
        when:
            def response = tokenService.validateAuthorizationRequest(request, CommonFixtures.defaultAuth())

        then:
            response.getStatusCode() == HttpStatus.OK

        and:
            def responseBody = response.getBody() as ResponseDTO

            responseBody.getCode().length() != 0
            responseBody.getState() == CommonFixtures.defaultState
    }

    def "should return error when credentials incorrect"() {
        given:
            def request = CommonFixtures.correctRequest()

            def client = CommonFixtures.defaultClient()
            def basicAuth = Fixtures.unauthorizedAuth()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            userService.isUserAuthorized(_ as UserDTO) >> false
        when:
            def response = tokenService.validateAuthorizationRequest(request, basicAuth)

        then:
            response.getStatusCode() == HttpStatus.BAD_REQUEST

        and:
            def error = response.getBody() as ErrorResponseDTO
            error.getError() == RequestErrorType.ACCESS_DENIED
    }

    private class Fixtures {
        static BasicAuth unauthorizedAuth() {
            var login = UUID.randomUUID().toString()
            var password = UUID.randomUUID().toString()
            var str = login + ":" + password
            return new BasicAuth(Base64.getEncoder().encodeToString(str.getBytes()))
        }
    }

}
