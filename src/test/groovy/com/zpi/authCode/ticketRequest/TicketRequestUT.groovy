package com.zpi.authCode.ticketRequest

import com.zpi.api.common.dto.ErrorResponseDTO
import com.zpi.api.common.exception.ErrorResponseException
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
import com.zpi.domain.rest.ams.AmsService
import com.zpi.domain.rest.analysis.AnalysisService
import com.zpi.domain.rest.analysis.request.AnalysisRequest
import com.zpi.domain.rest.analysis.response.AnalysisResponse
import com.zpi.testUtils.CommonFixtures
import spock.lang.Specification
import spock.lang.Subject

class TicketRequestUT extends Specification {
    def requestValidator = Mock(RequestValidator)
    def ams = Mock(AmsService)
    def consentService = Mock(ConsentServiceImpl)
    def authorizationService = Mock(AuthorizationService)
    def analysisService = Mock(AnalysisService)

    @Subject
    private AuthCodeService tokenService = new AuthCodeServiceImpl(requestValidator, ams, consentService, authorizationService, analysisService)

    def "should return auth ticket when request is valid"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toHashedDomain()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ams.isAuthenticated(user) >> true
            authorizationService.createTicket(user, request) >> new AuthorizationResponse(CommonFixtures.ticket, CommonFixtures.state)
            analysisService.isAdditionalLayerRequired(_ as AnalysisRequest) >> Fixtures.stubAnalysisResponse()

        when:
            def response = tokenService.authenticationTicket(user, request, CommonFixtures.analysisRequest())

        then:
            !response.getTicket().isEmpty()
            response.getState() == CommonFixtures.state
    }

    def "should return error on wrong request"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toHashedDomain()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> {
                throw Fixtures.sampleException()
            }
            analysisService.isAdditionalLayerRequired(_ as AnalysisRequest) >> Fixtures.stubAnalysisResponse()

        when:
            tokenService.authenticationTicket(user, request, CommonFixtures.analysisRequest())

        then:
            def error = new ErrorResponseDTO(Fixtures.unauthorizedClientError(), request.getState())
            def thrownException = thrown(ErrorResponseException)

            thrownException.errorResponse == error
    }

    def "should return error when user not authenticated"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toHashedDomain()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ams.isAuthenticated(user) >> false
            analysisService.isAdditionalLayerRequired(_ as AnalysisRequest) >> Fixtures.stubAnalysisResponse()

        when:
            tokenService.authenticationTicket(user, request, CommonFixtures.analysisRequest())

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

        static AnalysisResponse stubAnalysisResponse() {
            return new AnalysisResponse(false)
        }
    }
}
