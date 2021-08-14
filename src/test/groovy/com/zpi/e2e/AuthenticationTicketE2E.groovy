package com.zpi.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import com.zpi.CommonFixtures
import com.zpi.CommonHelpers
import com.zpi.client.domain.ClientRepository
import com.zpi.user.domain.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTicketE2E extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper

    private CommonHelpers commonHelpers

    private static final String clientRegisterUrl = "/api/client/register"
    private static final String userRegisterUrl = "/api/user/register"
    private static final String authRequestUrl = "/api/token/authorize"

    def setup() {
        commonHelpers = new CommonHelpers(mapper, mockMvc)
    }

    def "should get authentication ticket for newly registered user and client"() {
        given:
            def client = CommonFixtures.clientDTO()
            def user = CommonFixtures.userDTO()
            def request = CommonFixtures.requestDTO()

        when:
            commonHelpers.postRequest(client, clientRegisterUrl)
            commonHelpers.postRequest(user, userRegisterUrl)

        and:
            def response = commonHelpers.postRequest(user, CommonHelpers.authParametersToUrl(request, authRequestUrl))

        then:
            response.andExpect(status().isFound())

        and:
            CommonHelpers.attributeFromResult("ticket", response).length() != 0
            CommonHelpers.attributeFromResult("state", response) == request.getState()
    }
}
