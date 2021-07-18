package com.zpi.token.authorizationRequest

import com.zpi.token.api.authorizationRequest.ErrorResponseDTO
import com.zpi.token.api.authorizationRequest.RequestDTO
import com.zpi.token.domain.TokenService
import com.zpi.token.domain.Client
import com.zpi.token.domain.ClientRepository
import com.zpi.token.domain.authorizationRequest.request.RequestError
import com.zpi.token.domain.authorizationRequest.request.RequestErrorType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

class RequestValidationUT extends Specification {
    def clientRepository = Mock(ClientRepository)

    @Subject
    private TokenService tokenService = new TokenService(clientRepository)

    def "should return ok on all parameters correct"() {
        given:
            def request = CommonFixtures.correctRequest()
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            response.getStatusCode() == HttpStatus.OK
    }

    def "should return unauthorized_client on non existing client"() {
        given:
            def request = CommonFixtures.correctRequest()

            clientRepository.getByKey(request.getClientId()) >> Optional.empty()
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def error = RequestError.builder()
                    .error(RequestErrorType.UNAUTHORIZED_CLIENT)
                    .errorDescription("Unauthorized client id")
                    .build()
            def requestError = new ErrorResponseDTO(error, request.getState())

            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)
    }

    def "should return error message when incorrect redirect_uri"() {
        given:
            def request = Fixtures.requestWithCustomUri("UnrecognizedUri")
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def error = RequestError.builder()
                    .error(RequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .build()
            def requestError = new ErrorResponseDTO(error, request.getState())
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)
    }

    def "should return error message when client has no registered redirect uris"() {
        given:
            def request = Fixtures.requestWithCustomUri("UnrecognizedUri")
            def client = Fixtures.clientWithNullRedirectUri()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def error = RequestError.builder()
                    .error(RequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .build()
            def requestError = new ErrorResponseDTO(error, request.getState())
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)
    }

    def "should return invalid_request on missing required parameters"() {
        given:
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def error = RequestError.builder()
                    .error(RequestErrorType.INVALID_REQUEST)
                    .errorDescription("Missing: " + errorDescription)
                    .build()

            def requestError = new ErrorResponseDTO(error, request.getState())
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)

        where:
            request                 | _ || errorDescription
            Fixtures.nullClientId() | _ || "client_id"
            Fixtures.nullState()    | _ || "state"
    }

    def "should return unsupported_response_type on wrong responseType"() {
        given:
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def error = RequestError.builder()
                    .error(RequestErrorType.UNSUPPORTED_RESPONSE_TYPE)
                    .errorDescription(errorDescription)
                    .build()
            def requestError = new ErrorResponseDTO(error, request.getState())
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)

        where:
            request                        | _ || errorDescription
            Fixtures.invalidResponseType() | _ || "Unrecognized response type: invalid"
    }

    def "should return invalid_scope on invalid scope"() {
        given:
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
        when:
            def response = tokenService.validateAuthorizationRequest(request)

        then:
            def requestError = new ErrorResponseDTO(
                    RequestError.builder()
                            .error(RequestErrorType.INVALID_SCOPE)
                            .errorDescription("Invalid scope")
                            .build()
                    ,
                    request.getState()
            )
            response == new ResponseEntity<>(requestError, HttpStatus.BAD_REQUEST)

        where:
            request                       | _
            Fixtures.emptyScope()         | _
            Fixtures.nullScope()          | _
            Fixtures.scopeWithoutOpenId() | _
    }

    private class Fixtures {
        static RequestDTO requestWithCustomUri(String uri) {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(uri)
                    .responseType("code")
                    .scope("openid")
                    .state(CommonFixtures.defaultState)
                    .build()
        }

        static RequestDTO nullClientId() {
            return RequestDTO.builder()
                    .clientId(null)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("code")
                    .scope("openid")
                    .state(CommonFixtures.defaultState)
                    .build()
        }

        static RequestDTO nullState() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("code")
                    .scope("openid")
                    .state(null)
                    .build()
        }

        static RequestDTO invalidResponseType() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("invalid")
                    .scope("openid")
                    .state(CommonFixtures.defaultState)
                    .build()
        }

        static RequestDTO emptyScope() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("code")
                    .scope("")
                    .state(CommonFixtures.defaultClientId)
                    .build()
        }

        static RequestDTO nullScope() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("code")
                    .scope(null)
                    .state(CommonFixtures.defaultClientId)
                    .build()
        }

        static RequestDTO scopeWithoutOpenId() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.defaultClientId)
                    .redirectUri(CommonFixtures.defaultUri)
                    .responseType("code")
                    .scope("profile phone unknown_value other_unknown")
                    .state(CommonFixtures.defaultState)
                    .build()
        }

        static Client clientWithNullRedirectUri() {
            return Client.builder()
                    .id(CommonFixtures.defaultClientId)
                    .availableRedirectUri(null)
                    .build()
        }
    }
}
