package com.zpi.token.authorizationRequest

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.zpi.token.api.authorizationRequest.RequestDTO
import com.zpi.token.domain.WebClientRepository
import com.zpi.token.domain.authorizationRequest.request.RequestErrorType
import com.zpi.user.api.UserDTO
import com.zpi.utils.BasicAuth
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
            def request = CommonFixtures.correctRequest()
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
                        .header("Authorization", Fixtures.defaultBasicAuth())
        )
    }

    private static String errorFromResult(ResultActions result) {
        def response = result.andReturn().getResponse()
        def error = JsonPath.read(response.getContentAsString(), "\$.error")

        return error.toString()
    }

    private void addClientWithRedirectUri() {
        def client = CommonFixtures.defaultClient()
        clientRepository.save(client.getId(), client)
    }

    private class Fixtures {
        private static RequestDTO unknownClientRequest() {
            return RequestDTO.builder()
                    .clientId(UUID.randomUUID().toString())
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

        private static String defaultBasicAuth() {
            var user = UserDTO.builder()
                    .login(CommonFixtures.defaultLogin)
                    .password(CommonFixtures.defaultPassword)
                    .build()

            return BasicAuth.encodeFrom(user.toHashedDomain())
        }
    }
}
