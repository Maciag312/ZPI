package com.zpi.authCode.consentRequest

import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.testUtils.CommonFixtures
import com.zpi.testUtils.CommonHelpers
import com.zpi.testUtils.MvcRequestHelpers
import com.zpi.testUtils.UriParamsResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

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
            def request = CommonFixtures.consentRequestDTO("")

        and:
            ticketRepository.save(request.getTicket(), CommonFixtures.ticketData())

        when:
            def response = mvcHelpers.postRequest(request, baseUrl)

        then:
            def uri = UriComponentsBuilder
                    .fromUriString(
                            response.andReturn().getResponse().getContentAsString()
                    ).build()

            CommonHelpers.attributeFromRedirectedUrlInBody("state", response) == request.getState()
            uri.getPath() == CommonFixtures.redirectUri

        and:
            ticketRepository.findByKey(request.getTicket()).isEmpty()

        and:
            def code = CommonHelpers.attributeFromRedirectedUrlInBody("code", response)
            authCodeRepository.findByKey(code).isPresent()
    }

    def "should return error when ticket is outdated"() {
        given:
            def request = CommonFixtures.consentRequestDTO("")

        when:
            def response = mvcHelpers.postRequest(request, baseUrl)

        then:
            def actual = new UriParamsResult(response)
            actual.getParam("error") == "TICKET_EXPIRED"
            actual.getParam("error_description").replace("%20", " ") == "Ticket expired"
            actual.getParam("state") == request.getState()
            actual.getPath() == CommonFixtures.authPageUrl
    }
}
