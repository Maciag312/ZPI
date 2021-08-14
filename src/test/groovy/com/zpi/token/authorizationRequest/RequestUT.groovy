package com.zpi.token.authorizationRequest

import com.zpi.CommonFixtures
import com.zpi.client.domain.Client
import com.zpi.client.domain.ClientRepository
import com.zpi.token.api.authorizationRequest.ErrorResponseDTO
import com.zpi.token.api.authorizationRequest.ErrorResponseException
import com.zpi.token.domain.TokenService
import com.zpi.token.domain.authorizationRequest.request.*
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
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toHashedDomain()
            def client = CommonFixtures.client()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> null
            userService.isAuthenticated(user) >> true

        when:
            def response = tokenService.authorizationRequest(user, request)

        then:
            response.getTicket().length() != 0
            response.getState() == CommonFixtures.defaultState
    }

    def "should return error on wrong request"() {
        given:
            def request = CommonFixtures.request()
            def client = CommonFixtures.client()
            def user = CommonFixtures.userDTO().toHashedDomain()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> {
                throw Fixtures.sampleException()
            }

        when:
            tokenService.authorizationRequest(user, request)

        then:
            def error = new ErrorResponseDTO(Fixtures.unauthorizedClientError(), request.getState())
            def thrownException = thrown(ErrorResponseException)

            thrownException.errorResponse == error
    }

    def "should return error when user not authenticated"() {
        given:
            def request = CommonFixtures.request()
            def client = CommonFixtures.client()
            def user = CommonFixtures.userDTO().toHashedDomain()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> null
            userService.isAuthenticated(user) >> false

        when:
            tokenService.authorizationRequest(user, request)

        then:
            def error = new ErrorResponseDTO(Fixtures.userAuthenticationFailedError(), request.getState())
            def thrownException = thrown(ErrorResponseException)

            thrownException.errorResponse == error
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
