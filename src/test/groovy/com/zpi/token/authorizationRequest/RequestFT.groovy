package com.zpi.token.authorizationRequest

import com.fasterxml.jackson.databind.ObjectMapper
import com.zpi.CommonFixtures
import com.zpi.CommonHelpers
import com.zpi.client.domain.ClientRepository
import com.zpi.user.domain.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class RequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ClientRepository clientRepository

    @Autowired
    private UserService userService

    @Autowired
    private ObjectMapper mapper

    private CommonHelpers commonHelpers

    private static final String baseUri = "/api/token/authorize"

    def setup() {
        commonHelpers = new CommonHelpers(mapper, mockMvc)
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
            result.andExpect(status().isFound())
            CommonHelpers.attributeFromResult("state", result) == CommonFixtures.defaultState
            CommonHelpers.attributeFromResult("ticket", result).length() != 0
    }

    private void addClientWithRedirectUri() {
        def client = CommonFixtures.client()
        clientRepository.save(client.getId(), client)
    }

    private void addUser() {
        def user = CommonFixtures.userDTO()
        userService.createUser(user)
    }
}
