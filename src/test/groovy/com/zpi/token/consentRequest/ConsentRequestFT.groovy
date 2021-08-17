package com.zpi.token.consentRequest

import com.zpi.CommonFixtures
import com.zpi.CommonHelpers
import com.zpi.domain.token.consentRequest.TicketRepository
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
    private TicketRepository repository

    @Autowired
    private CommonHelpers commonHelpers

    private static final String baseUri = "/api/token/consent"

    def setup() {
        repository.clear()
    }

    def "should return authentication code when ticket is up to date"() {
        given:
            def request = CommonFixtures.consentRequestDTO()

        and:
            repository.save(request.getTicket(), null)

        when:
            def response = commonHelpers.postRequest(request, baseUri)

        then:
            response.andExpect(status().isOk())

        and:
            CommonHelpers.attributeFromResult("code", response).length() != 0
            CommonHelpers.attributeFromResult("state", response) == request.getState()

        and:
            repository.getByKey(request.getTicket()).isEmpty()
    }

    def "should return error when ticket is outdated"() {
        given:
            def request = CommonFixtures.consentRequestDTO()

        when:
            def response = commonHelpers.postRequest(request, baseUri)

        then:
            response.andExpect(status().isBadRequest())

        and:
            CommonHelpers.attributeFromResult("error", response) == "TICKET_EXPIRED"
            CommonHelpers.attributeFromResult("errorDescription", response) == "Ticket expired"
            CommonHelpers.attributeFromResult("state", response) == request.getState()
    }
}
