package com.zpi.token.authorizationRequest

import com.zpi.token.api.AuthRequestDTO
import com.zpi.token.domain.TokenService
import com.zpi.token.domain.WebClient
import com.zpi.token.domain.WebClientRepository
import com.zpi.token.domain.authorizationRequest.RequestError
import com.zpi.token.domain.authorizationRequest.RequestErrorType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

class RequestValidationUT extends Specification {
    def clientRepository = Mock(WebClientRepository)

    @Subject
    private TokenService tokenService = new TokenService(clientRepository)

    def "should return ok on all parameters correct"() {
        given:
            def request = Fixtures.correctRequest()

            def client = Fixtures.clientWithDefaultUri()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            response == new ResponseEntity<>(HttpStatus.OK)
    }

    def "should return unauthorized_client on non existing client"() {
        given:
            def request = Fixtures.correctRequest()

            clientRepository.getByKey(request.getClientId()) >> Optional.empty()
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def requestError = RequestError.builder()
                    .error(RequestErrorType.UNAUTHORIZED_CLIENT)
                    .error_description("Unauthorized client id")
                    .build()

            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)
    }

    def "should return error message when incorrect redirect_uri"() {
        given:
            def request = Fixtures.requestWithCustomUri("UnrecognizedUri")

            def client = Fixtures.clientWithDefaultUri()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def requestError = RequestError.builder()
                    .error(RequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .error_description("Unrecognized redirect uri")
                    .build()
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)
    }

    def "should return invalid_request on missing required parameters"() {
        given:
            def client = Fixtures.clientWithDefaultUri()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def requestError = RequestError.builder()
                    .error(RequestErrorType.INVALID_REQUEST)
                    .error_description("Missing: " + errorDescription)
                    .build()
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)

        where:
            request                 | _ || errorDescription
            Fixtures.nullClientId() | _ || "client_id"
            Fixtures.nullState()    | _ || "state"
    }

    def "should return unsupported_response_type on wrong responseType"() {
        given:
            def client = Fixtures.clientWithDefaultUri()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def requestError = RequestError.builder()
                    .error(RequestErrorType.UNSUPPORTED_RESPONSE_TYPE)
                    .error_description(errorDescription)
                    .build()
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)

        where:
            request                        | _ || errorDescription
            Fixtures.invalidResponseType() | _ || "Unrecognized response type: invalid"
    }

    def "should return invalid_scope on invalid scope"() {
        given:
            def client = Fixtures.clientWithDefaultUri()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def requestError = RequestError.builder()
                    .error(RequestErrorType.INVALID_SCOPE)
                    .error_description(errorDescription)
                    .build()
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)

        where:
            request                       | _ || errorDescription
            Fixtures.emptyScope()         | _ || "Invalid scope"
            Fixtures.scopeWithoutOpenId() | _ || "Invalid scope"
    }

    private class Fixtures {
        private static final String defaultUri = "uri"

        static AuthRequestDTO correctRequest() {
            return AuthRequestDTO.builder()
                    .clientId("client_id")
                    .redirectUri(defaultUri)
                    .responseType("code")
                    .scope("openid")
                    .state("state")
                    .build()
        }

        static AuthRequestDTO requestWithCustomUri(String uri) {
            return AuthRequestDTO.builder()
                    .clientId("client_id")
                    .redirectUri(uri)
                    .responseType("code")
                    .scope("openid")
                    .state("state")
                    .build()
        }

        static AuthRequestDTO nullClientId() {
            return AuthRequestDTO.builder()
                    .clientId(null)
                    .redirectUri(defaultUri)
                    .responseType("code")
                    .scope("openid")
                    .state("state")
                    .build()
        }

        static AuthRequestDTO nullState() {
            return AuthRequestDTO.builder()
                    .clientId("client_id")
                    .redirectUri(defaultUri)
                    .responseType("code")
                    .scope("openid")
                    .state(null)
                    .build()
        }

        static AuthRequestDTO invalidResponseType() {
            return AuthRequestDTO.builder()
                    .clientId("client_id")
                    .redirectUri(defaultUri)
                    .responseType("invalid")
                    .scope("openid")
                    .state("state")
                    .build()
        }

        static AuthRequestDTO emptyScope() {
            return AuthRequestDTO.builder()
                    .clientId("client_id")
                    .redirectUri(defaultUri)
                    .responseType("code")
                    .scope("")
                    .state("state")
                    .build()
        }

        static AuthRequestDTO scopeWithoutOpenId() {
            return AuthRequestDTO.builder()
                    .clientId("client_id")
                    .redirectUri(defaultUri)
                    .responseType("code")
                    .scope("profile phone unknown_value other_unknown")
                    .state("state")
                    .build()
        }

        static WebClient clientWithDefaultUri() {
            def client = WebClient.builder().id("id").availableRedirectUri(null).build()
            client.addRedirectUri(defaultUri)

            return client
        }
    }
}
