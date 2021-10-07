package com.zpi.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.zpi.CommonHelpers
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.api.common.dto.UserDTO
import com.zpi.domain.audit.AuditLog
import com.zpi.domain.audit.AuditMetadata
import com.zpi.domain.audit.AuditRepository
import com.zpi.domain.organization.Organization
import com.zpi.domain.organization.OrganizationRepository
import com.zpi.domain.organization.client.Client
import com.zpi.domain.organization.client.ClientRepository
import com.zpi.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuditInterceptorFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper mapper

    @Autowired
    private ClientRepository clientRepository

    @Autowired
    private UserRepository userRepository

    @Autowired
    private OrganizationRepository organizationRepository

    @Autowired
    private AuditRepository auditRepository

    private static final String baseUri = "/api/authenticate"

    def setup() {
        clientRepository.clear()
        userRepository.clear()
        organizationRepository.clear()
        auditRepository.clear()
    }

    def "should add request headers from authenticate endpoint to audit repository when request is correct"() {
        given:
            def organization = new Organization("afgasdf")
            def client = new Client("asdfadsf")
            client.setOrganizationName(organization.getName())
            def redirectUri = "sasdfdg"
            client.addRedirectUri(redirectUri)
            def request = new TicketRequestDTO(client.getId(), redirectUri, "code", "profile", "agasdf")
            def host = "192.168.0.1"
            def userAgent = "agent"
            def user = new UserDTO("login", "password")
            def hashedUser = user.toHashedDomain()

        and:
            organizationRepository.save(organization)
            clientRepository.save(client.getId(), client)
            userRepository.save(hashedUser.getLogin(), hashedUser)

        when:
            mockMvc.perform(
                    post(CommonHelpers.authParametersToUrl(request, baseUri))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(user))
                            .header("host", host)
                            .header("user-agent", userAgent)
            )

        then:
            def result = auditRepository.findByOrganization(organization)
            def expected = new AuditLog(new Date(), new AuditMetadata(host, userAgent), organization.getName(), hashedUser.getLogin())

        and:
            result.size() == 1

        and:
            result.first().getOrganizationName() == expected.getOrganizationName()
            result.first().getMetadata().getHost() == expected.getMetadata().getHost()
            result.first().getMetadata().getUserAgent() == expected.getMetadata().getUserAgent()
            result.first().getUsername() == expected.getUsername()
    }

    def "should add entry to incident repository when incorrect request is provided"() {
        given:
            organizationRepository.save(organization)
            clientRepository.save(client.getId(), client)
            userRepository.save(user.toHashedDomain().getLogin(), user.toHashedDomain())

        when:
            mockMvc.perform(
                    post(CommonHelpers.authParametersToUrl(request, baseUri))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(user))
                            .header("host", host)
                            .header("user-agent", userAgent)
            )

        then:
            def result = auditRepository.findByOrganization(organization)

        and:
            result.size() == 0

        where:
            organization         | client         | user                | host | userAgent | request                 || expected
            new Organization("") | new Client("") | new UserDTO("", "") | ""   | ""        | Fixtures.emptyRequest() || null
    }

    private class Fixtures {
        static TicketRequestDTO emptyRequest() {
            return new TicketRequestDTO("", "", "", "", "")
        }
    }
}
