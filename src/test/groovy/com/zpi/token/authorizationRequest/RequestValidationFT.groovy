package com.zpi.token.authorizationRequest

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.zpi.token.api.authorizationRequest.RequestDTO
import com.zpi.token.domain.WebClient
import com.zpi.token.domain.WebClientRepository
import com.zpi.token.domain.authorizationRequest.RequestErrorType
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
    private MockMvc mockMvc;

    @Autowired
    private WebClientRepository clientRepository;

    @Autowired
    private ObjectMapper mapper;

    def "should return appropriate error on unknown client"() {
        given:
            def request = Fixtures.correctRequest()

        when:
            def result = validateAuthorizationRequest(request)

        then:
            result.andExpect(status().isBadRequest())

        and:
            errorFromResult(result) == RequestErrorType.UNAUTHORIZED_CLIENT.toString()
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
            errorFromResult(result) == RequestErrorType.UNRECOGNIZED_REDIRECT_URI.toString()
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
            errorFromResult(result) == RequestErrorType.UNSUPPORTED_RESPONSE_TYPE.toString()
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
            errorFromResult(result) == RequestErrorType.INVALID_SCOPE.toString()
    }

    def "should return success on correct request"() {
        given:

            def request = Fixtures.correctRequest()
            addClientWithRedirectUri()

        when:
            def result = validateAuthorizationRequest(request)

        then:
            result.andExpect(status().isOk())
    }

    private ResultActions validateAuthorizationRequest(RequestDTO request) {
        return mockMvc.perform(
                post("/api/token/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
    }

    private static String errorFromResult(ResultActions result) {
        def response = result.andReturn().getResponse()
        def error = JsonPath.read(response.getContentAsString(), "\$.error")

        return error.toString()
    }
    private void addClientWithRedirectUri() {
        def client = Fixtures.defaultClient()
        clientRepository.save(client.getId(), client)
    }

    private class Fixtures {
        private static final String defaultUri = "uri"
        private static final String defaultClientId = "client_id"

        private static RequestDTO correctRequest() {
            return RequestDTO.builder()
                    .clientId(defaultClientId)
                    .redirectUri(defaultUri)
                    .responseType("code")
                    .scope("openid")
                    .state("state")
                    .build()
        }

        private static RequestDTO unrecognizedRedirectUri() {
            return RequestDTO.builder()
                    .clientId(defaultClientId)
                    .redirectUri("unrecognized")
                    .responseType("code")
                    .scope("openid")
                    .state("state")
                    .build()
        }

        private static RequestDTO invalidResponseType() {
            return RequestDTO.builder()
                    .clientId(defaultClientId)
                    .redirectUri(defaultUri)
                    .responseType("NotCode")
                    .scope("openid")
                    .state("state")
                    .build()
        }

        private static RequestDTO invalidScope() {
            return RequestDTO.builder()
                    .clientId(defaultClientId)
                    .redirectUri(defaultUri)
                    .responseType("code")
                    .scope("invalid scope")
                    .state("state")
                    .build()

        }

        private static WebClient defaultClient() {
            def client = WebClient.builder()
                    .id(defaultClientId)
                    .availableRedirectUri(new HashSet<String>())
                    .build()
            client.addRedirectUri(defaultUri)

            return client
        }
    }
}
