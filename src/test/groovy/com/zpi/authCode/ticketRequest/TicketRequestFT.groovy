package com.zpi.authCode.ticketRequest


import com.zpi.CommonFixtures
import com.zpi.CommonHelpers
import com.zpi.domain.client.ClientRepository
import com.zpi.domain.user.UserManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class TicketRequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ClientRepository clientRepository

    @Autowired
    private UserManager userManager

    @Autowired
    private CommonHelpers commonHelpers

    private static final String baseUri = "/api/authenticate"

    def setup() {
        clientRepository.clear()
    }

    def "should return success on correct request"() {
        given:
            def request = CommonFixtures.requestDTO()
            def user = CommonFixtures.userDTO()
            addClientWithRedirectUri()
            addUser()

        when:
            def result = commonHelpers.postRequest(user, CommonHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isOk())
            CommonHelpers.attributeFromResult("state", result) == CommonFixtures.state
            CommonHelpers.attributeFromResult("ticket", result).length() != 0
    }

    private void addClientWithRedirectUri() {
        def client = CommonFixtures.client()
        clientRepository.save(client.getId(), client)
    }

    private void addUser() {
        def user = CommonFixtures.userDTO().toHashedDomain()
        userManager.createUser(user)
    }
}
