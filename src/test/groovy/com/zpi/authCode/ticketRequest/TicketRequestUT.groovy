package com.zpi.authCode.ticketRequest

import com.zpi.CommonFixtures
import com.zpi.api.common.dto.ErrorResponseDTO
import com.zpi.api.common.exception.ErrorResponseException
import com.zpi.domain.audit.AuditMetadata
import com.zpi.domain.audit.AuditService
import com.zpi.domain.authCode.AuthCodeService
import com.zpi.domain.authCode.AuthCodeServiceImpl
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequestErrorType
import com.zpi.domain.authCode.authenticationRequest.RequestValidator
import com.zpi.domain.authCode.authenticationRequest.ValidationFailedException
import com.zpi.domain.authCode.authorizationRequest.AuthorizationResponse
import com.zpi.domain.authCode.authorizationRequest.AuthorizationService
import com.zpi.domain.authCode.consentRequest.ConsentServiceImpl
import com.zpi.domain.common.RequestError
import com.zpi.domain.organization.client.ClientRepository
import com.zpi.domain.user.User
import com.zpi.domain.user.UserAuthenticator
import spock.lang.Specification
import spock.lang.Subject

class TicketRequestUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def requestValidator = Mock(RequestValidator)
    def authenticator = Mock(UserAuthenticator)
    def consentService = Mock(ConsentServiceImpl)
    def authorizationService = Mock(AuthorizationService)
    def auditService = Mock(AuditService)

    @Subject
    private AuthCodeService tokenService = new AuthCodeServiceImpl(requestValidator, authenticator, consentService, authorizationService, auditService)

    def "should return auth ticket when request is valid"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toHashedDomain()
            def client = CommonFixtures.client()

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            authenticator.isAuthenticated(user) >> true
            authorizationService.createTicket(user, request) >> new AuthorizationResponse(CommonFixtures.ticket, CommonFixtures.state)
            auditService.audit(_ as User, _ as AuthenticationRequest, new AuditMetadata("", "")) >> null

        when:
            def response = tokenService.authenticationTicket(user, request, new AuditMetadata("", ""))

        then:
            !response.getTicket().isEmpty()
            response.getState() == CommonFixtures.state
    }

    def "should return error on wrong request"() {
        given:
            def request = CommonFixtures.request()
            def client = CommonFixtures.client()
            def user = CommonFixtures.userDTO().toHashedDomain()

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> {
                throw Fixtures.sampleException()
            }
            auditService.audit(_ as User, _ as AuthenticationRequest, new AuditMetadata("", "")) >> null

        when:
            tokenService.authenticationTicket(user, request, new AuditMetadata("", ""))

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

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            authenticator.isAuthenticated(user) >> false
            auditService.audit(_ as User, _ as AuthenticationRequest, new AuditMetadata("", "")) >> null

        when:
            tokenService.authenticationTicket(user, request, new AuditMetadata("", ""))

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
            final AuthenticationRequestErrorType errorType = AuthenticationRequestErrorType.UNAUTHORIZED_CLIENT
            final String description = "Unauthorized client id"

            return RequestError.builder()
                    .error(errorType)
                    .errorDescription(description)
                    .build()
        }

        static RequestError userAuthenticationFailedError() {
            final AuthenticationRequestErrorType errorType = AuthenticationRequestErrorType.USER_AUTH_FAILED
            final String description = "User authentication failed"

            return RequestError.builder()
                    .error(errorType)
                    .errorDescription(description)
                    .build()
        }
    }
}
