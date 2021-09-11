package com.zpi.e2e


import com.zpi.CommonFixtures
import com.zpi.CommonHelpers
import com.zpi.domain.authCode.consentRequest.TicketRepository
import com.zpi.domain.organization.client.ClientRepository
import com.zpi.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTicketE2E extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ClientRepository clientRepository

    @Autowired
    private UserRepository userRepository

    @Autowired
    private TicketRepository ticketRepository

    @Autowired
    private CommonHelpers commonHelpers

    private static final String accessCode = "funny-code"
    private static final String organizationURI = '/api/organization/';
    private static final String clientRegisterUrl(String organizationName){
        return organizationURI + organizationName + '/client/register';
    }
    private static final String userRegisterUrl(String organizationName){
        return organizationURI + organizationName + '/user/register'
    }
    private static final String authorizeRequestUrl = "/api/authorize"
    private static final String authenticateRequestUrl = "/api/authenticate"
    private static final String consentRequestUrl = "/api/consent"

    def setup() {
        clientRepository.clear()
        userRepository.clear()
        ticketRepository.clear()
    }

    def "should get authentication ticket for newly registered user and client"() {
        given:
            def organizationName = "pizzaHouse"
            def client = CommonFixtures.clientDTO()
            def user = CommonFixtures.userDTO()
            def request = CommonFixtures.requestDTO()

        when:
            commonHelpers.postRequest( '/api/organization/register/' + organizationName + '?code=' + accessCode)
            commonHelpers.postRequest(client, clientRegisterUrl(organizationName))
            commonHelpers.postRequest(user, userRegisterUrl(organizationName))

        and:
            def authorizeResponse = commonHelpers.getRequest(CommonHelpers.authParametersToUrl(request, authorizeRequestUrl))

        then:
            authorizeResponse.andExpect(status().isFound())
            authorizeResponse.andExpect(header().exists("Location"))

        when:
            def authenticateResponse = commonHelpers.postRequest(user, CommonHelpers.authParametersToUrl(request, authenticateRequestUrl))

        then:
            authenticateResponse.andExpect(status().isOk())
            def ticket = CommonHelpers.attributeFromResult("ticket", authenticateResponse)

        when:
            def consentRequest = CommonFixtures.consentRequestDTO(ticket)
            def consentResponse = commonHelpers.postRequest(consentRequest, CommonHelpers.authParametersToUrl(request, consentRequestUrl))

        then:
            consentResponse.andExpect(status().isFound())
        and:
            def uri = consentResponse.andReturn().getResponse().getHeader("Location")
            def path = UriComponentsBuilder.fromUriString(uri).build().getPath()
            path == CommonFixtures.redirectUri
    }
}
