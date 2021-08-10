package com.zpi.token.authorizationRequest

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.zpi.common.api.UserDTO
import com.zpi.token.api.authorizationRequest.RequestDTO
import com.zpi.token.domain.ClientRepository
import com.zpi.user.domain.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

    private static final String baseUri = "/api/token/authorize"

    def "should return success on correct request"() {
        given:
            def request = CommonFixtures.correctRequest()
            def user = Fixtures.sampleUser()
            addClientWithRedirectUri()
            addUser()

        when:
            def result = makeAuthRequest(request, user)

        then:
            result.andExpect(status().isFound())
            attributeFromResult("state", result) == CommonFixtures.defaultState
            attributeFromResult("ticket", result).length() != 0
    }

    private void addClientWithRedirectUri() {
        def client = CommonFixtures.defaultClient()
        clientRepository.save(client.getId(), client)
    }

    private void addUser() {
        def user = CommonFixtures.defaultUser()
        userService.createUser(user)
    }

    private ResultActions makeAuthRequest(RequestDTO request, UserDTO user) {
        return mockMvc.perform(
                post(parametersToUrl(request, baseUri))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
        )
    }

    private static String parametersToUrl(RequestDTO request, String uri) {
        return UriComponentsBuilder.fromUriString(uri)
                .queryParam("client_id", request.clientId)
                .queryParam("redirect_uri", request.redirectUri)
                .queryParam("response_type", request.responseType)
                .queryParam("scope", request.scope)
                .queryParam("state", request.state)
                .toUriString()
    }

    private static String attributeFromResult(String attribute, ResultActions result) {
        def response = result.andReturn().getResponse()
        def value = JsonPath.read(response.getContentAsString(), String.format("\$.%s", attribute))
        return value.toString()
    }

    private class Fixtures {
        static sampleUser() {
            return UserDTO.builder()
                    .login("Login")
                    .password("Password")
                    .build()
        }
    }
}
