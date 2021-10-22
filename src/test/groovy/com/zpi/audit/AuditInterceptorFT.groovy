package com.zpi.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.audit.AuditLog
import com.zpi.domain.audit.AuditMetadata
import com.zpi.domain.audit.AuditRepository
import com.zpi.domain.rest.ams.Client
import com.zpi.infrastructure.rest.ams.AmsClient
import com.zpi.testUtils.CommonFixtures
import com.zpi.testUtils.CommonHelpers
import com.zpi.testUtils.wiremock.ClientMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class AuditInterceptorFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private AmsClient ams;

    @Autowired
    private WireMockServer mockServer

    @Autowired
    private ObjectMapper mapper

    @Autowired
    private AuditRepository auditRepository

    private static final String baseUri = "/api/authenticate"

    def setup() {
        ClientMocks.setupMockClientDetailsResponse(mockServer)
        auditRepository.clear()
    }

    def "should add request headers from authenticate endpoint to audit repository when request is correct"() {
        given:
            def redirectUri = CommonFixtures.redirectUri
            def client = new Client(List.of(redirectUri), "asdfadsf")
            def request = new TicketRequestDTO(client.getId(), redirectUri, "code", "profile", "agasdf")
            def host = "192.168.0.1"
            def userAgent = "agent"
            def user = new UserDTO("login", "password")
            def hashedUser = user.toHashedDomain()

        when:
            mockMvc.perform(
                    post(CommonHelpers.authParametersToUrl(request, baseUri))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(user))
                            .header("host", host)
                            .header("user-agent", userAgent)
            )

        then:
            def result = auditRepository.findByUsername(hashedUser.getLogin())
            def expected = new AuditLog(new Date(), new AuditMetadata(host, userAgent), hashedUser.getLogin())

        and:
            result.size() == 1

        and:
            result.first().getMetadata().getHost() == expected.getMetadata().getHost()
            result.first().getMetadata().getUserAgent() == expected.getMetadata().getUserAgent()
            result.first().getUsername() == expected.getUsername()
    }

    def "should add entry to incident repository when incorrect request is provided"() {
        when:
            mockMvc.perform(
                    post(CommonHelpers.authParametersToUrl(request, baseUri))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(user))
                            .header("host", host)
                            .header("user-agent", userAgent)
            )

        then:
            def result = auditRepository.findByUsername(user.getLogin())

        and:
            result.size() == 0

        where:
             user                | host | userAgent | request                                  || expected
             new UserDTO("", "") | ""   | ""        | new TicketRequestDTO("", "", "", "", "") || null
    }
}
