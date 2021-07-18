package com.zpi.token.authorizationRequest

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.zpi.token.api.authorizationRequest.RequestDTO
import com.zpi.token.domain.WebClientRepository
import com.zpi.token.domain.authorizationRequest.request.RequestErrorType
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
class RequestValidationFT extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private WebClientRepository clientRepository

    @Autowired
    private ObjectMapper mapper

    def "should return appropriate error on unknown client"() {
        given:
            def request = Fixtures.unknownClientRequest()

        when:
            def result = validateAuthorizationRequest(request)

        then:
            result.andExpect(status().isBadRequest())

        and:
            attributeFromResult("error", result) == RequestErrorType.UNAUTHORIZED_CLIENT.toString()
            attributeFromResult("state", result) == CommonFixtures.defaultState
    }

    def "should return appropriate error on unrecognized redirect_uri"() {
        given:
            def request = Fixtures.unrecognizedRedirectUri()
            addClientWithRedirectUri()

        when:
            def result = validateAuthorizationRequest(request)

        then:
            result.andExpect(status().isBadRequest())

        and:
            attributeFromResult("error", result) == RequestErrorType.UNRECOGNIZED_REDIRECT_URI.toString()
            attributeFromResult("state", result) == CommonFixtures.defaultState
    }

    def "should return appropriate error on invalid response_type"() {
        given:

            def request = Fixtures.invalidResponseType()
            addClientWithRedirectUri()

        when:
            def result = validateAuthorizationRequest(request)

        then:
            result.andExpect(status().isBadRequest())

        and:
            attributeFromResult("error", result) == RequestErrorType.UNSUPPORTED_RESPONSE_TYPE.toString()
            attributeFromResult("state", result) == CommonFixtures.defaultState
    }

    def "should return appropriate error on invalid scope"() {
        given:
            def request = Fixtures.invalidScope()
            addClientWithRedirectUri()
        when:
            def result = validateAuthorizationRequest(request)

        then:
            result.andExpect(status().isBadRequest())

        and:
            attributeFromResult("error", result) == RequestErrorType.INVALID_SCOPE.toString()
            attributeFromResult("state", result) == CommonFixtures.defaultState
    }

    private ResultActions validateAuthorizationRequest(RequestDTO request) {
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

    private void addClientWithRedirectUri() {
        def client = CommonFixtures.defaultClient()
        clientRepository.save(client.getId(), client)
    }

    private class Fixtures {
        private static RequestDTO unknownClientRequest() {
            var clientId = UUID.randomUUID().toString()
            return RequestDTO.builder()
                    .clientId(clientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("code")
                    .scope("openid")
                    .state(CommonFixtures.defaultState)
                    .build()
        }

        private static RequestDTO unrecognizedRedirectUri() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri("unrecognized")
                    .responseType("code")
                    .scope("openid")
                    .state(CommonFixtures.defaultState)
                    .build()
        }

        private static RequestDTO invalidResponseType() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("NotCode")
                    .scope("openid")
                    .state(CommonFixtures.defaultState)
                    .build()
        }

        private static RequestDTO invalidScope() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("code")
                    .scope("invalid scope")
                    .state(CommonFixtures.defaultState)
                    .build()

        }
    }
}
