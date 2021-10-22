package com.zpi.authCode.ticketRequest

import com.github.tomakehurst.wiremock.WireMockServer
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.testUtils.CommonFixtures
import com.zpi.testUtils.MvcRequestHelpers
import com.zpi.testUtils.ResultHelpers
import com.zpi.testUtils.wiremock.ClientMocks
import com.zpi.testUtils.wiremock.UserMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class TicketRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private AmsClient ams

    @Autowired
    private WireMockServer mockServer

    @Autowired
    private MvcRequestHelpers commonHelpers

    private static final String baseUri = "/api/authenticate"

    def setup() {
        ClientMocks.setupMockClientDetailsResponse(mockServer)
        UserMocks.setupMockUserAuthenticateResponse(mockServer)
    }

    def "should return success on correct request"() {
        given:
            def request = CommonFixtures.requestDTO()
            def user = CommonFixtures.userDTO()

        when:
            def result = commonHelpers.postRequest(user, ResultHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isOk())
            ResultHelpers.attributeFromResult("state", result) == CommonFixtures.state
            ResultHelpers.attributeFromResult("ticket", result).length() != 0
    }

    def "should return failure on incorrect request"() {
        given:
            def request = TicketRequestDTO.builder()
                    .clientId("")
                    .redirectUri("")
                    .responseType("")
                    .scope("")
                    .state(CommonFixtures.state)
                    .build()

            def user = CommonFixtures.userDTO()
        when:
            def result = commonHelpers.postRequest(user, ResultHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isBadRequest())
            ResultHelpers.attributeFromResult("state", result) == CommonFixtures.state
            !ResultHelpers.attributeFromResult("error", result).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", result).isEmpty()
    }
}
