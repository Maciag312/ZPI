package com.zpi.authCode.ticketRequest

import com.zpi.api.common.dto.ErrorResponseDTO
import com.zpi.api.common.exception.ErrorResponseException
import com.zpi.domain.authCode.AuthCodeService
import com.zpi.domain.authCode.AuthCodeServiceImpl
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequest
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequestErrorType
import com.zpi.domain.authCode.authenticationRequest.RequestValidator
import com.zpi.domain.authCode.authenticationRequest.ValidationFailedException
import com.zpi.domain.authCode.authorizationRequest.TicketResponse
import com.zpi.domain.authCode.authorizationRequest.TicketService
import com.zpi.domain.authCode.authorizationRequest.TicketType
import com.zpi.domain.authCode.authorizationRequest.UserValidationFailedException
import com.zpi.domain.authCode.consentRequest.ConsentServiceImpl
import com.zpi.domain.common.RequestError
import com.zpi.testUtils.CommonFixtures
import spock.lang.Specification
import spock.lang.Subject

class TicketRequestUT extends Specification {
    def requestValidator = Mock(RequestValidator)
    def consentService = Mock(ConsentServiceImpl)
    def ticketService = Mock(TicketService)

    @Subject
    private AuthCodeService tokenService = new AuthCodeServiceImpl(requestValidator, consentService, ticketService)

    def "should return auth ticket when request is valid"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ticketService.createTicket(user, request, analysisRequest) >> new TicketResponse(CommonFixtures.ticket, TicketType.TICKET, CommonFixtures.state)

        when:
            def response = tokenService.authenticationTicket(user, request, analysisRequest)

        then:
            !response.getTicket().isEmpty()
            response.getState() == CommonFixtures.state
    }

    def "should return error on wrong request"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> {
                throw Fixtures.sampleException()
            }

        when:
            tokenService.authenticationTicket(user, request, CommonFixtures.analysisRequest())

        then:
            def error = new ErrorResponseDTO(Fixtures.unauthorizedClientError(), request.getState())
            def thrownException = thrown(ErrorResponseException)

            thrownException.errorResponse == error
    }

    def "should return error on ticket service failure"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ticketService.createTicket(user, request, analysisRequest) >> {
                throw new UserValidationFailedException(Fixtures.unauthorizedClientError())
            }

        when:
            tokenService.authenticationTicket(user, request, analysisRequest)

        then:
            def error = new ErrorResponseDTO(Fixtures.unauthorizedClientError(), request.getState())
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
    }
}
