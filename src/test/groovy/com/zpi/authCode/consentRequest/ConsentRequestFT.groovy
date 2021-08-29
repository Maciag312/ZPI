package com.zpi.authCode.consentRequest

import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.UriParamsResult
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ConsentRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private MvcRequestHelpers mvcHelpers

    @Autowired
    private TicketRepository ticketRepository

    @Autowired
    private AuthCodeRepository authCodeRepository

    private static final String baseUrl = "/api/consent"

    def cleanup() {
        ticketRepository.clear()
        authCodeRepository.clear()
    }

    def "should return authentication code when ticket is up to date"() {
        given:
            def request = CommonFixtures.consentRequestDTO()

        and:
            ticketRepository.save(request.getTicket(), CommonFixtures.ticketData())

        when:
            def response = mvcHelpers.postRequest(request, baseUrl)

        then:
            response.andExpect(status().isFound())

        and:
            def actual = new UriParamsResult(response)

            actual.getParam("state") == request.getState()
            actual.getPath() == CommonFixtures.redirectUri

        and:
            ticketRepository.getByKey(request.getTicket()).isEmpty()

        and:
            def code = actual.getParam("code")
            authCodeRepository.getByKey(code).isPresent()
    }

    def "should return error when ticket is outdated"() {
        given:
            def request = CommonFixtures.consentRequestDTO()

        when:
            def response = mvcHelpers.postRequest(request, baseUrl)

        then:
            response.andExpect(status().isFound())

        and:
            def actual = new UriParamsResult(response)
            actual.getParam("error") == "TICKET_EXPIRED"
            actual.getParam("error_description").replace("%20", " ") == "Ticket expired"
            actual.getParam("state") == request.getState()
            actual.getPath() == CommonFixtures.authPageUrl
    }
}
