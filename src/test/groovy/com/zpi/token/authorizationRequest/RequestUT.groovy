package com.zpi.token.authorizationRequest

import com.zpi.token.api.authorizationRequest.ErrorResponseDTO
import com.zpi.token.api.authorizationRequest.ResponseDTO
import com.zpi.token.domain.Client
import com.zpi.token.domain.ClientRepository
import com.zpi.token.domain.TokenService
import com.zpi.token.domain.authorizationRequest.request.*
import com.zpi.common.api.UserDTO
import com.zpi.user.domain.UserService
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class RequestUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def requestValidation = Mock(RequestValidation)
    def userService = Mock(UserService)

    @Subject
    private TokenService tokenService = new TokenService(clientRepository, requestValidation, userService)

    def "should return auth ticket when request is valid"() {
        given:
            def request = CommonFixtures.correctRequest()
            def user = CommonFixtures.defaultUser()
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> null
            userService.isAuthenticated(user as UserDTO) >> true

        when:
            def response = tokenService.authorizationRequest(user, request)

        then:
            response.getStatusCode() == HttpStatus.FOUND

        and:
            def responseBody = response.getBody() as ResponseDTO

            responseBody.getTicket().length() != 0
            responseBody.getState() == CommonFixtures.defaultState
    }

    def "should return error on wrong request"() {
        given:
            def request = CommonFixtures.correctRequest()
            def client = CommonFixtures.defaultClient()
            def user = CommonFixtures.defaultUser()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> {
                throw Fixtures.sampleException()
            }

        when:
            def response = tokenService.authorizationRequest(user, request)

        then:
            response.getStatusCodeValue() == HttpStatus.FOUND.value()

        and:
            def expected = new ErrorResponseDTO(Fixtures.unauthorizedClientError(), request.getState())
            def responseBody = response.getBody() as ErrorResponseDTO

            responseBody == expected
    }

    def "should return error when user not authenticated"() {
        given:
            def request = CommonFixtures.correctRequest()
            def client = CommonFixtures.defaultClient()
            def user = CommonFixtures.defaultUser()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> null
            userService.isAuthenticated(user as UserDTO) >> false

        when:
            def response = tokenService.authorizationRequest(user, request)

        then:
            response.getStatusCodeValue() == HttpStatus.FOUND.value()

        and:
            def expected = new ErrorResponseDTO(Fixtures.userAuthenticationFailedError(), request.getState())
            def responseBody = response.getBody() as ErrorResponseDTO

            responseBody == expected
    }

    private class Fixtures {
        private static final HttpStatus defaultErrorHttpStatus = HttpStatus.FOUND

        static InvalidRequestException sampleException() {
            return new InvalidRequestException(defaultErrorHttpStatus, unauthorizedClientError())
        }

        static RequestError unauthorizedClientError() {
            final RequestErrorType errorType = RequestErrorType.UNAUTHORIZED_CLIENT
            final String description = "Unauthorized client id"

            return RequestError.builder()
                    .error(errorType)
                    .errorDescription(description)
                    .build()
        }

        static RequestError userAuthenticationFailedError() {
            final RequestErrorType errorType = RequestErrorType.USER_AUTH_FAILED
            final String description = "User authentication failed"

            return RequestError.builder()
                    .error(errorType)
                    .errorDescription(description)
                    .build()
        }
    }
}
