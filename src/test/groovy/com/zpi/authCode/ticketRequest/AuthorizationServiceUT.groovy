package com.zpi.authCode.ticketRequest

import com.zpi.domain.authCode.authorizationRequest.AuthorizationService
import com.zpi.domain.authCode.authorizationRequest.AuthorizationServiceImpl
import com.zpi.domain.authCode.authorizationRequest.TicketType
import com.zpi.domain.authCode.authorizationRequest.MailService
import com.zpi.domain.authCode.consentRequest.TicketData
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.common.CodeGenerator
import com.zpi.domain.rest.ams.User
import com.zpi.domain.rest.analysis.AnalysisService
import com.zpi.domain.twoFactorAuth.TwoFactorData
import com.zpi.domain.twoFactorAuth.TwoFactorRepository
import com.zpi.testUtils.CommonFixtures
import spock.lang.Specification
import spock.lang.Subject

class AuthorizationServiceUT extends Specification {
    def ticketRepository = Mock(TicketRepository)
    def generator = Mock(CodeGenerator)
    def analysisService = Mock(AnalysisService)
    def twoFactorRepository = Mock(TwoFactorRepository)
    def mail = Mock(MailService)

    @Subject
    private AuthorizationService service = new AuthorizationServiceImpl(ticketRepository, generator, analysisService, twoFactorRepository, mail)

    def "should return ticket when 2fa is not required"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()
            def request = CommonFixtures.request()
            def ticket = "a"

        and:
            analysisService.isAdditionalLayerRequired(analysisRequest) >> false
            generator.ticketCode() >> ticket

        when:
            def result = service.createTicket(user, request, analysisRequest)

        then:
            result.getTicket() == ticket
            result.getTicketType() == TicketType.TICKET

        and:
            1 * ticketRepository.save(ticket, _ as TicketData)
            0 * twoFactorRepository.save(_ as String, _ as TwoFactorData);
    }

    def "should return ticket when 2fa is required"() {
        given:
            def user = CommonFixtures.userDTO().toDomain()
            def analysisRequest = CommonFixtures.analysisRequest()
            def request = CommonFixtures.request()
            def ticket = "a"
            def twoFactorCode = "b"
            def twoFactorTicket = "c"

        and:
            analysisService.isAdditionalLayerRequired(analysisRequest) >> true
            generator.ticketCode() >>> [ticket, twoFactorTicket]
            generator.twoFactorCode() >> twoFactorCode
            mail.send(_ as String, _ as User) >> null

        when:
            def result = service.createTicket(user, request, analysisRequest)

        then:
            result.getTicket() == twoFactorTicket
            result.getTicketType() == TicketType.TICKET_2FA

        and:
            1 * ticketRepository.save(ticket, _ as TicketData)
            1 * twoFactorRepository.save(twoFactorTicket, new TwoFactorData(ticket, twoFactorCode))
    }
}
