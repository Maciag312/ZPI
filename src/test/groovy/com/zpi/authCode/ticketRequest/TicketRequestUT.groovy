package com.zpi.authCode.ticketRequest

import com.zpi.CommonFixtures
import com.zpi.api.common.dto.ErrorResponseDTO
import com.zpi.api.common.exception.ErrorResponseException
import com.zpi.domain.authCode.AuthCodeServiceImpl
import com.zpi.domain.authCode.authenticationRequest.Request
import com.zpi.domain.authCode.authenticationRequest.RequestErrorType
import com.zpi.domain.authCode.authenticationRequest.RequestValidator
import com.zpi.domain.authCode.authenticationRequest.ValidationFailedException
import com.zpi.domain.authCode.authorizationRequest.AuthorizationResponse
import com.zpi.domain.authCode.authorizationRequest.AuthorizationService
import com.zpi.domain.authCode.consentRequest.ConsentServiceImpl
import com.zpi.domain.client.ClientRepository
import com.zpi.domain.common.RequestError
import com.zpi.domain.user.UserAuthenticator
import spock.lang.Specification
import spock.lang.Subject

class TicketRequestUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def requestValidator = Mock(RequestValidator)
    def authenticator = Mock(UserAuthenticator)
    def consentService = Mock(ConsentServiceImpl)
    def authorizationService = Mock(AuthorizationService)

    @Subject
    private AuthCodeServiceImpl tokenService = new AuthCodeServiceImpl(requestValidator, authenticator, consentService, authorizationService)

    def "should return auth ticket when request is valid"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toHashedDomain()
            def client = CommonFixtures.client()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidator.validateAndFillMissingFields(_ as Request) >> null
            authenticator.isAuthenticated(user) >> true
            authorizationService.createTicket(request) >> new AuthorizationResponse(CommonFixtures.ticket, CommonFixtures.state)

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
            requestValidator.validateAndFillMissingFields(_ as Request) >> {
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
            requestValidator.validateAndFillMissingFields(_ as Request) >> null
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
