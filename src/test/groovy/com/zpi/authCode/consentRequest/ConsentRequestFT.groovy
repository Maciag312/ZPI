package com.zpi.authCode.consentRequest

import com.zpi.CommonFixtures
import com.zpi.CommonHelpers
import com.zpi.domain.authCode.consentRequest.TicketRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ConsentRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private TicketRepository repository

    @Autowired
    private CommonHelpers commonHelpers

    private static final String baseUri = "/api/consent"

    def setup() {
        repository.clear()
    }

    def "should return authentication code when ticket is up to date"() {
        given:
            def request = CommonFixtures.consentRequestDTO()

        and:
            repository.save(request.getTicket(), CommonFixtures.ticketData())

        when:
            def response = commonHelpers.postRequest(request, baseUri)

        then:
            response.andExpect(status().isFound())

        and:
            var uri = response.andReturn().getResponse().getHeader("Location")
            var uriComponents = UriComponentsBuilder.fromUriString(uri).build()
            var path = uriComponents.getPath()
            var params = uriComponents.getQueryParams()

            var code = (params.get("code") as String)
            var state = params.get("state") as String

            code.length() != 0
            state.contains(request.getState())
            path == CommonFixtures.redirectUri

        and:
            repository.getByKey(request.getTicket()).isEmpty()
    }

    def "should return error when ticket is outdated"() {
        given:
            def request = CommonFixtures.consentRequestDTO()

        when:
            def response = commonHelpers.postRequest(request, baseUri)

        then:
            response.andExpect(status().isFound())

        and:
            var uri = response.andReturn().getResponse().getHeader("Location")
            var uriComponents = UriComponentsBuilder.fromUriString(uri).build()
            var path = uriComponents.getPath()
            var params = uriComponents.getQueryParams()

            var error = params.get("error") as String
            var errorDescription = params.get("error_description") as String
            var state = params.get("state") as String

            error.contains("TICKET_EXPIRED")
            errorDescription.replace("%20", " ").contains("Ticket expired")
            state.contains(request.getState())
            path == CommonFixtures.authPageUrl
    }
}
