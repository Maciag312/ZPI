package com.zpi.token.authorizationRequest

import com.zpi.CommonFixtures
import com.zpi.api.token.ticketRequest.RequestDTO
import com.zpi.domain.client.Client
import com.zpi.domain.common.RequestError
import com.zpi.domain.token.ticketRequest.request.ValidationFailedException
import com.zpi.domain.token.ticketRequest.request.RequestErrorType
import com.zpi.domain.token.ticketRequest.request.RequestValidator
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class RequestValidatorUT extends Specification {
    @Subject
    private RequestValidator requestValidation = new RequestValidator()

    def "should not throw when all parameters correct"() {
        given:
            def request = CommonFixtures.request()
            def client = CommonFixtures.client()
        when:
            requestValidation.validate(request, client)

        then:
            noExceptionThrown()
    }

    def "should throw unauthorized_client on non existing client"() {
        given:
            def request = Fixtures.correctRequest().toDomain()

        when:
            requestValidation.validate(request, null)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(RequestErrorType.UNAUTHORIZED_CLIENT)
                    .errorDescription("Unauthorized client id")
                    .build()

            exception.error == expected
    }

    def "should throw when incorrect redirect_uri"() {
        given:
            def request = Fixtures.requestWithCustomUri("UnrecognizedUri").toDomain()
            def client = CommonFixtures.client()

        when:
            requestValidation.validate(request, client)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(RequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .build()

            exception.error == expected
    }

    def "should return error message when client has no registered redirect uris"() {
        given:
            def request = Fixtures.requestWithCustomUri("UnrecognizedUri").toDomain()
            def client = Fixtures.clientWithNullRedirectUri()

        when:
            requestValidation.validate(request, client)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(RequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .build()

            exception.error == expected
    }

    def "should throw invalid_request on missing required parameters"() {
        given:
            def client = CommonFixtures.client()

        when:
            requestValidation.validate(request.toDomain(), client)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(RequestErrorType.INVALID_REQUEST)
                    .errorDescription("Missing: " + errorDescription)
                    .build()

            exception.error == expected

        where:
            request                 | _ || errorDescription
            Fixtures.nullClientId() | _ || "client_id"
            Fixtures.nullState()    | _ || "state"
    }

    def "should throw unsupported_response_type on wrong responseType"() {
        given:
            def client = CommonFixtures.client()

        when:
            requestValidation.validate(request.toDomain(), client)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(RequestErrorType.UNSUPPORTED_RESPONSE_TYPE)
                    .errorDescription(errorDescription)
                    .build()

            exception.error == expected

        where:
            request                        | _ || errorDescription
            Fixtures.invalidResponseType() | _ || "Unrecognized response type: invalid"
    }

    def "should throw invalid_scope on invalid scope"() {
        given:
            def client = CommonFixtures.client()

        when:
            requestValidation.validate(request.toDomain(), client)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(RequestErrorType.INVALID_SCOPE)
                    .errorDescription("Invalid scope")
                    .build()

            exception.error == expected

        where:
            request                       | _
            Fixtures.emptyScope()         | _
            Fixtures.nullScope()          | _
            Fixtures.scopeWithoutOpenId() | _
    }

    private class Fixtures {
        static RequestDTO correctRequest() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope("openid%20phone%20photos%20asdf_asdf_asdf")
                    .state(CommonFixtures.state)
                    .build()
        }

        static RequestDTO requestWithCustomUri(String uri) {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(uri)
                    .responseType(CommonFixtures.responseType)
                    .scope("openid")
                    .state(CommonFixtures.state)
                    .build()
        }

        static RequestDTO nullClientId() {
            return RequestDTO.builder()
                    .clientId(null)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope("openid")
                    .state(CommonFixtures.state)
                    .build()
        }

        static RequestDTO nullState() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope(CommonFixtures.scope)
                    .state(null)
                    .build()
        }

        static RequestDTO invalidResponseType() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType("invalid")
                    .scope(CommonFixtures.scope)
                    .state(CommonFixtures.state)
                    .build()
        }

        static RequestDTO emptyScope() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope("")
                    .state(CommonFixtures.clientId)
                    .build()
        }

        static RequestDTO nullScope() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope(null)
                    .state(CommonFixtures.clientId)
                    .build()
        }

        static RequestDTO scopeWithoutOpenId() {
            return RequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope("profile phone unknown_value other_unknown")
                    .state(CommonFixtures.state)
                    .build()
        }

        static Client clientWithNullRedirectUri() {
            return Client.builder()
                    .id(CommonFixtures.clientId)
                    .availableRedirectUri(null)
                    .build()
        }
    }
}
