package com.zpi.token.authorizationRequest

import com.zpi.CommonFixtures
import com.zpi.api.common.dto.ErrorResponseDTO
import com.zpi.api.common.exception.ErrorResponseException
import com.zpi.domain.client.Client
import com.zpi.domain.client.ClientRepository
import com.zpi.domain.common.RequestError
import com.zpi.domain.token.TokenService
import com.zpi.domain.token.consentRequest.ConsentService
import com.zpi.domain.token.ticketRequest.request.ValidationFailedException
import com.zpi.domain.token.ticketRequest.request.Request

import com.zpi.domain.token.ticketRequest.request.RequestErrorType
import com.zpi.domain.token.ticketRequest.request.RequestValidator
import com.zpi.domain.user.UserAuthenticator
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class RequestUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def requestValidator = Mock(RequestValidator)
    def authenticator = Mock(UserAuthenticator)
    def consentService = Mock(ConsentService)

    @Subject
    private TokenService tokenService = new TokenService(clientRepository, requestValidator, authenticator, consentService)

    def "should return auth ticket when request is valid"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toHashedDomain()
            def client = CommonFixtures.client()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidator.validate(_ as Request, _ as Client) >> null
            authenticator.isAuthenticated(user) >> true

        when:
            def response = tokenService.authenticationTicket(user, request)

        then:
            response.getTicket().length() != 0
            response.getState() == CommonFixtures.state
    }

    def "should return error on wrong request"() {
        given:
            def request = CommonFixtures.request()
            def client = CommonFixtures.client()
            def user = CommonFixtures.userDTO().toHashedDomain()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidator.validate(_ as Request, _ as Client) >> {
                throw Fixtures.sampleException()
            }

        when:
            tokenService.authenticationTicket(user, request)

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
            requestValidator.validate(_ as Request, _ as Client) >> null
            authenticator.isAuthenticated(user) >> false

        when:
            tokenService.authenticationTicket(user, request)

        then:
            def error = new ErrorResponseDTO(Fixtures.userAuthenticationFailedError(), request.getState())
            def thrownException = thrown(ErrorResponseException)

            thrownException.errorResponse == error
    }

    private class Fixtures {
        static ValidationFailedException sampleException() {
            return new ValidationFailedException(unauthorizedClientError())
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
