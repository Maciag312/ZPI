package com.zpi.authCode.consentRequest

import com.zpi.CommonFixtures
import com.zpi.domain.authCode.consentRequest.*
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodePersister
import com.zpi.domain.common.RequestError
import spock.lang.Specification
import spock.lang.Subject

class ConsentRequestUT extends Specification {
    def ticketRepository = Mock(TicketRepository)
    def authCodePersister = Mock(AuthCodePersister)

    @Subject
    private ConsentService service = new ConsentServiceImpl(ticketRepository, authCodePersister)

    def "should return authentication code when ticket is in database"() {
        given:
            def request = CommonFixtures.consentRequest()
            def authData = new TicketData(CommonFixtures.redirectUri, CommonFixtures.scope, CommonFixtures.userDTO().login)

            def ticket = request.getTicket()
            def scope = "bbbb"
            def redirectUri = "cccc"
            def username = "dddd"
            def authCode = new AuthCode("aaaa", new AuthUserData(scope, redirectUri, username))

        and:
            ticketRepository.findByKey(ticket) >> Optional.of(authData)
            ticketRepository.remove(ticket) >> null
            authCodePersister.persist(authData) >> authCode
        when:
            def response = service.consent(request)

        then:
            1 * ticketRepository.remove(ticket)
        and:
            !response.getRedirectUri().isEmpty()

            response.getCode().getValue() == authCode.getValue()
            response.getState() == CommonFixtures.state
    }

    def "should return error when ticket was used before"() {
        given:
            def request = CommonFixtures.consentRequest()

            ticketRepository.findByKey(request.getTicket()) >> Optional.empty()
            authCodePersister.persist(new TicketData("", "", "")) >> null

        when:
            service.consent(request)

        then:
            def thrownException = thrown(ErrorConsentResponseException)
            thrownException.getError() == Fixtures.error(request.getState())
    }

    private class Fixtures {
        static RequestError error(String state) {
            final ConsentErrorType errorType = ConsentErrorType.TICKET_EXPIRED
            final String description = "Ticket expired"

            return RequestError.builder()
                    .error(errorType)
                    .errorDescription(description)
                    .state(state)
                    .build()
        }
    }
}
