package com.zpi.token.authorizationRequest

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.zpi.token.api.authorizationRequest.RequestDTO
import com.zpi.token.domain.WebClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class RequestFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private WebClientRepository clientRepository

    @Autowired
    private ObjectMapper mapper

    def "should return success on correct request"() {
        given:
            def request = CommonFixtures.correctRequest()
            addClientWithRedirectUri()

        when:
            def result = makeAuthRequest(request)

        then:
            result.andExpect(status().isOk())
            attributeFromResult("state", result) == CommonFixtures.defaultState
            attributeFromResult("code", result).length() != 0
    }

    private void addClientWithRedirectUri() {
        def client = CommonFixtures.defaultClient()
        clientRepository.save(client.getId(), client)
    }

    private ResultActions makeAuthRequest(RequestDTO request) {
        return mockMvc.perform(
                post("/api/token/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
    }

    private static String attributeFromResult(String attribute, ResultActions result) {
        def response = result.andReturn().getResponse()
        def value = JsonPath.read(response.getContentAsString(), String.format("\$.%s", attribute))
        return value.toString()
    }
}
