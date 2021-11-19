package com.zpi.authCode.ticketRequest

import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequestErrorType
import com.zpi.domain.authCode.authorizationRequest.*
import com.zpi.domain.authCode.consentRequest.TicketData
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.common.CodeGenerator
import com.zpi.domain.common.RequestError
import com.zpi.domain.rest.ams.AmsService
import com.zpi.domain.rest.analysis.AnalysisResponse
import com.zpi.domain.rest.analysis.AnalysisService
import com.zpi.domain.rest.analysis.lockout.LoginAction
import com.zpi.domain.rest.analysis.lockout.LoginFailedResponse
import com.zpi.domain.rest.analysis.twoFactor.TwoFactorResponse
import com.zpi.domain.twoFactorAuth.TwoFactorData
import com.zpi.domain.twoFactorAuth.TwoFactorRepository
import com.zpi.testUtils.CommonFixtures
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class TicketServiceUT extends Specification {
    def ticketRepository = Mock(TicketRepository)
    def generator = Mock(CodeGenerator)
    def twoFactorRepository = Mock(TwoFactorRepository)
    def mail = Mock(MailService)
    def ams = Mock(AmsService)
    def analysis = Mock(AnalysisService)

    @Subject
    private TicketService service = new TicketServiceImpl(ticketRepository, generator, analysis, twoFactorRepository, mail, ams)

    def "should return ticket when 2fa is not required"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()
            def request = CommonFixtures.request()
            def ticket = "a"

        and:
            def analysisResponse = new AnalysisResponse(new LoginFailedResponse(LoginAction.ALLOW, LocalDateTime.now()), new TwoFactorResponse(false))

            ams.isAuthenticated(user) >> true
            analysis.analyse(analysisRequest) >> analysisResponse
            generator.ticketCode() >> ticket

        when:
            def result = service.createTicket(user, request, analysisRequest)

        then:
            result.getTicket() == ticket
            result.getTicketType() == TicketType.TICKET

        and:
            1 * ticketRepository.save(ticket, _ as TicketData)
            0 * twoFactorRepository.save(_ as String, _ as TwoFactorData)
    }

    def "should return ticket when 2fa is required"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()
            def request = CommonFixtures.request()
            def ticket = "a"
            def twoFactorCode = "b"
            def twoFactorKey = "c"

        and:
            def analysisResponse = new AnalysisResponse(new LoginFailedResponse(LoginAction.ALLOW, LocalDateTime.now()), new TwoFactorResponse(true))

            ams.isAuthenticated(user) >> true
            analysis.analyse(analysisRequest) >> analysisResponse
            generator.ticketCode() >>> [ticket, twoFactorKey]
            generator.twoFactorCode() >> twoFactorCode
            mail.send(twoFactorCode, user) >> null

        when:
            def result = service.createTicket(user, request, analysisRequest)

        then:
            result.getTicket() == twoFactorKey
            result.getTicketType() == TicketType.TICKET_2FA

        and:
            1 * ticketRepository.save(ticket, _ as TicketData)
            1 * twoFactorRepository.save(twoFactorKey, new TwoFactorData(ticket, twoFactorCode))
    }

    def "should throw when user not authenticated"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()
            def analysisResponse = new AnalysisResponse(new LoginFailedResponse(LoginAction.ALLOW, LocalDateTime.now()), new TwoFactorResponse(false))

            ams.isAuthenticated(user) >> false
            analysis.analyse(analysisRequest) >> analysisResponse

                    when :
            service.createTicket(user, request, analysisRequest)

        then:
            def error = new UserValidationFailedException(Fixtures.userAuthenticationFailedError())
            def thrownException = thrown(UserValidationFailedException)

            thrownException.error.getError() == error.error.getError()
            thrownException.error.getErrorDescription() == error.error.getErrorDescription()
    }

    def "should return error with delay till time when analysis returns block"() {
        given:
            def request = CommonFixtures.request()
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()

        and:
            def lockoutResponse = new LoginFailedResponse(LoginAction.BLOCK, LocalDateTime.now())
            def analysisResponse = new AnalysisResponse(lockoutResponse, new TwoFactorResponse(true))

            ams.isAuthenticated(user) >> true
            analysis.analyse(analysisRequest) >> analysisResponse
            analysis.analyse(analysisRequest) >> analysisResponse

        when:
            service.createTicket(user, request, analysisRequest)

        then:
            def error = new LoginLockoutException(Fixtures.lockoutError(lockoutResponse.getDelayTill()))
            def thrownException = thrown(LoginLockoutException)

            thrownException.error.getError() == error.getError().getError()
            thrownException.error.getErrorDescription() == error.getError().getErrorDescription()
    }

    private class Fixtures {
        static RequestError userAuthenticationFailedError() {
            final AuthenticationRequestErrorType errorType = AuthenticationRequestErrorType.USER_AUTH_FAILED
            final String description = "User authentication failed"

            return RequestError.builder()
                    .error(errorType)
                    .errorDescription(description)
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
