package com.zpi.authCode.ticketRequest

import com.github.tomakehurst.wiremock.WireMockServer
import com.zpi.api.authCode.authenticationRequest.AuthenticationRequestDTO
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.infrastructure.rest.analysis.AnalysisClient
import com.zpi.testUtils.CommonFixtures
import com.zpi.testUtils.MvcRequestHelpers
import com.zpi.testUtils.ResultHelpers
import com.zpi.testUtils.wiremock.AnalysisMocks
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
    private AnalysisClient analysis

    @Autowired
    private WireMockServer mockServer

    @Autowired
    private MvcRequestHelpers commonHelpers

    private static final String baseUri = "/api/authenticate"

    def setup() {
        ClientMocks.clientDetails(mockServer)
        UserMocks.userAuthenticate(mockServer)
        AnalysisMocks.reportLoginFailure(mockServer)
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

            def requestBody = new AuthenticationRequestDTO(CommonFixtures.userDTO(), CommonFixtures.auditMetadataDTO())
        when:
            def result = commonHelpers.postRequest(requestBody, ResultHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isBadRequest())
            ResultHelpers.attributeFromResult("state", result) == CommonFixtures.state
            !ResultHelpers.attributeFromResult("error", result).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", result).isEmpty()
    }

    def "should return success on correct request when 2fa is not required"() {
        given:
            def request = CommonFixtures.requestDTO()
            def requestBody = new AuthenticationRequestDTO(CommonFixtures.userDTO(), CommonFixtures.auditMetadataDTO())

        and:
            AnalysisMocks.allowLogin2faNotRequired(mockServer)

        when:
            def result = commonHelpers.postRequest(requestBody, ResultHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isOk())
            ResultHelpers.attributeFromResult("state", result) == CommonFixtures.state
            ResultHelpers.attributeFromResult("ticket", result).length() != 0
            ResultHelpers.attributeFromResult("ticket_type", result) == "TICKET"
    }

    def "should return success on correct request when 2fa is required"() {
        given:
            def request = CommonFixtures.requestDTO()
            def requestBody = new AuthenticationRequestDTO(CommonFixtures.userDTO(), CommonFixtures.auditMetadataDTO())

        and:
            AnalysisMocks.allowLogin2faRequired(mockServer)

        when:
            def result = commonHelpers.postRequest(requestBody, ResultHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isOk())
            ResultHelpers.attributeFromResult("state", result) == CommonFixtures.state
            ResultHelpers.attributeFromResult("ticket", result).length() != 0
            ResultHelpers.attributeFromResult("ticket_type", result) == "TICKET_2FA"
    }

    def "should return failure on login lockout"() {
        given:
            def request = CommonFixtures.requestDTO()
            def requestBody = new AuthenticationRequestDTO(CommonFixtures.userDTO(), CommonFixtures.auditMetadataDTO())

        and:
            AnalysisMocks.blockLogin2faNotRequired(mockServer)

        when:
            def result = commonHelpers.postRequest(requestBody, ResultHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isTooManyRequests())
            ResultHelpers.attributeFromResult("state", result) == CommonFixtures.state
            !ResultHelpers.attributeFromResult("error", result).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", result).isEmpty()
    }
}
