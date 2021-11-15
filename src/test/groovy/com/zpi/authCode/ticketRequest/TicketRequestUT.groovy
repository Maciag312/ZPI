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
import com.zpi.domain.authCode.authorizationRequest.TicketType
import com.zpi.domain.authCode.consentRequest.ConsentServiceImpl
import com.zpi.domain.common.RequestError
import com.zpi.domain.rest.ams.AmsService
import com.zpi.domain.rest.analysis.AnalysisService
import com.zpi.domain.rest.analysis.failedLogin.LockoutResponse
import com.zpi.domain.rest.analysis.failedLogin.LoginAction
import com.zpi.testUtils.CommonFixtures
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class TicketRequestUT extends Specification {
    def requestValidator = Mock(RequestValidator)
    def ams = Mock(AmsService)
    def consentService = Mock(ConsentServiceImpl)
    def authorizationService = Mock(AuthorizationService)
    def analysis = Mock(AnalysisService)

    @Subject
    private AuthCodeService tokenService = new AuthCodeServiceImpl(requestValidator, ams, consentService, authorizationService, analysis)

    def "should return auth ticket when request is valid"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ams.isAuthenticated(user) >> true
            analysis.failedLoginLockout(analysisRequest) >> new LockoutResponse(LoginAction.ALLOW, LocalDateTime.now())
            authorizationService.createTicket(user, request, analysisRequest) >> new AuthorizationResponse(CommonFixtures.ticket, TicketType.TICKET, CommonFixtures.state)

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

    def "should return error when user not authenticated"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ams.isAuthenticated(user) >> false

        when:
            tokenService.authenticationTicket(user, request, CommonFixtures.analysisRequest())

        then:
            def error = new ErrorResponseDTO(Fixtures.userAuthenticationFailedError(), request.getState())
            def thrownException = thrown(ErrorResponseException)

            thrownException.errorResponse == error
    }

    def "should return error when analysis is not available"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ams.isAuthenticated(user) >> true
            analysis.failedLoginLockout(analysisRequest) >> null

        when:
            tokenService.authenticationTicket(user, request, CommonFixtures.analysisRequest())

        then:
            def error = new ErrorResponseDTO(Fixtures.analysisUnavailableError(), request.getState())
            def thrownException = thrown(ErrorResponseException)

            thrownException.errorResponse == error
    }

    def "should return error with delay till time when analysis returns block"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()
            def lockoutResponse = new LockoutResponse(LoginAction.BLOCK, LocalDateTime.now())

            requestValidator.validateAndFillMissingFields(_ as AuthenticationRequest) >> null
            ams.isAuthenticated(user) >> true
            analysis.failedLoginLockout(analysisRequest) >> lockoutResponse

        when:
            tokenService.authenticationTicket(user, request, analysisRequest)

        then:
            def error = new ErrorResponseDTO(Fixtures.lockoutError(lockoutResponse.getDelayTill()), request.getState())
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

        static RequestError analysisUnavailableError() {
            return RequestError.builder()
                    .error(AuthenticationRequestErrorType.ANALYSIS_NOT_AVAILABLE)
                    .errorDescription("Cannot connect to analysis service")
                    .build()
        }

        static RequestError lockoutError(LocalDateTime time) {
            return RequestError.builder()
                    .error(AuthenticationRequestErrorType.LOGIN_LOCKOUT)
                    .errorDescription(time.toString())
                    .build()
        }
    }
}
