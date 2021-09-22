package com.zpi.authCode.ticketRequest


import com.zpi.CommonFixtures
import com.zpi.MvcRequestHelpers
import com.zpi.ResultHelpers
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.domain.organization.client.ClientRepository
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
    private ClientRepository repository

    @Autowired
    private UserManager userManager

    @Autowired
    private MvcRequestHelpers commonHelpers

    private static final String baseUri = "/api/authenticate"

    def cleanup() {
        repository.clear()
    }

    def "should return success on correct request"() {
        given:
            def request = CommonFixtures.requestDTO()
            def user = CommonFixtures.userDTO()
            addClientWithRedirectUri()
            addUser()

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
            addClientWithRedirectUri()
            addUser()

        when:
            def result = commonHelpers.postRequest(user, ResultHelpers.authParametersToUrl(request, baseUri))

        then:
            result.andExpect(status().isBadRequest())
            ResultHelpers.attributeFromResult("state", result) == CommonFixtures.state
            !ResultHelpers.attributeFromResult("error", result).isEmpty()
            !ResultHelpers.attributeFromResult("error_description", result).isEmpty()
    }

    private void addClientWithRedirectUri() {
        def client = CommonFixtures.client()
        repository.save(client.getId(), client)
    }

    private void addUser() {
        def user = CommonFixtures.userDTO().toHashedDomain()
        userManager.createUser(user)
    }
}
