package com.zpi.twoFactorAuth

import com.zpi.api.twoFactorAuth.TwoFactorRequestDTO
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.twoFactorAuth.TwoFactorData
import com.zpi.domain.twoFactorAuth.TwoFactorRepository
import com.zpi.testUtils.MvcRequestHelpers
import com.zpi.testUtils.ResultHelpers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class TwoFactorRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private MvcRequestHelpers mvcRequestHelpers

    @Autowired
    private TwoFactorRepository twoFactorRepository

    @Autowired
    private TicketRepository ticketRepository

    private static final String baseUrl = "/api/2fa"

    def setup() {
        twoFactorRepository.clear()
        ticketRepository.clear()
    }

    def "should return ticket when 2fa code is correct"() {
        given:
            def twoFactorTicket = "a"
            def ticket = "b"
            def twoFactorCode = "c"
            def request = new TwoFactorRequestDTO(twoFactorTicket, twoFactorCode);
            def twoFactorData = new TwoFactorData(ticket, twoFactorCode)

        and:
            twoFactorRepository.save(twoFactorTicket, twoFactorData)

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            ResultHelpers.attributeFromResult("ticket", response) == ticket
    }

    def "should return failure on incorrect 2fa code"() {
        given:
            def request = new TwoFactorRequestDTO("", "");

        when:
            def response = mvcRequestHelpers.postRequest(request, baseUrl)

        then:
            !ResultHelpers.attributeFromResult("error", response).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", response).isEmpty()
    }
}
