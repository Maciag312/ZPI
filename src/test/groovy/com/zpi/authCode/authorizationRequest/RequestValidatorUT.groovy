package com.zpi.authCode.authorizationRequest

import com.zpi.CommonFixtures
import com.zpi.api.authCode.ticketRequest.TicketRequestDTO
import com.zpi.domain.authCode.authenticationRequest.OptionalParamsFiller
import com.zpi.domain.authCode.authenticationRequest.AuthenticationRequestErrorType
import com.zpi.domain.authCode.authenticationRequest.RequestValidator
import com.zpi.domain.authCode.authenticationRequest.ValidationFailedException
import com.zpi.domain.organization.client.Client
import com.zpi.domain.organization.client.ClientRepository
import com.zpi.domain.common.RequestError
import spock.lang.Specification
import spock.lang.Subject

class RequestValidatorUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def filler = Mock(OptionalParamsFiller)

    @Subject
    private RequestValidator requestValidation = new RequestValidator(clientRepository, filler)

    def "should not throw when all parameters correct"() {
        given:
            def request = CommonFixtures.request()
            def client = CommonFixtures.client()

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            filler.fill(request) >> request

        when:
            requestValidation.validateAndFillMissingFields(request)

        then:
            noExceptionThrown()
    }

    def "should throw unauthorized_client on non existing client"() {
        given:
            def request = Fixtures.correctRequest().toDomain()

            clientRepository.findByKey(request.getClientId()) >> Optional.empty()
            filler.fill(request) >> request

        when:
            requestValidation.validateAndFillMissingFields(request)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(AuthenticationRequestErrorType.UNAUTHORIZED_CLIENT)
                    .errorDescription("Unauthorized client id")
                    .state(request.getState())
                    .build()

            exception.error == expected
    }

    def "should throw when incorrect redirect_uri"() {
        given:
            def request = Fixtures.requestWithCustomUri("UnrecognizedUri").toDomain()
            def client = CommonFixtures.client()

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            filler.fill(request) >> request

        when:
            requestValidation.validateAndFillMissingFields(request)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(AuthenticationRequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .state(request.getState())
                    .build()

            exception.error == expected
    }

    def "should return error message when client has no registered redirect uris"() {
        given:
            def request = Fixtures.requestWithCustomUri("UnrecognizedUri").toDomain()
            def client = Fixtures.clientWithEmptyRedirectURIs()

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            filler.fill(request) >> request

        when:
            requestValidation.validateAndFillMissingFields(request)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(AuthenticationRequestErrorType.UNRECOGNIZED_REDIRECT_URI)
                    .errorDescription("Unrecognized redirect uri")
                    .state(request.getState())
                    .build()

            exception.error == expected
    }

    def "should throw invalid_request on missing required parameters"() {
        given:
            def client = CommonFixtures.client()

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            filler.fill(request) >> request

        when:
            requestValidation.validateAndFillMissingFields(request)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(AuthenticationRequestErrorType.INVALID_REQUEST)
                    .errorDescription("Missing: " + errorDescription)
                    .state(request.getState())
                    .build()

            exception.error == expected

        where:
            request                            | _ || errorDescription
            Fixtures.nullClientId().toDomain() | _ || "client_id"
            Fixtures.nullState().toDomain()    | _ || "state"
    }

    def "should throw unsupported_response_type on wrong responseType"() {
        given:
            def client = CommonFixtures.client()

            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            filler.fill(request) >> request

        when:
            requestValidation.validateAndFillMissingFields(request)

        then:
            def exception = thrown(ValidationFailedException)
            def expected = RequestError.builder()
                    .error(AuthenticationRequestErrorType.UNSUPPORTED_RESPONSE_TYPE)
                    .errorDescription(errorDescription)
                    .state(request.getState())
                    .build()

            exception.error == expected

        where:
            request                                   | _ || errorDescription
            Fixtures.invalidResponseType().toDomain() | _ || "Unrecognized response type: invalid"
    }

    private class Fixtures {
        static TicketRequestDTO correctRequest() {
            return TicketRequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope("openid%20phone%20photos%20asdf_asdf_asdf")
                    .state(CommonFixtures.state)
                    .build()
        }

        static TicketRequestDTO requestWithCustomUri(String uri) {
            return TicketRequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(uri)
                    .responseType(CommonFixtures.responseType)
                    .scope("openid")
                    .state(CommonFixtures.state)
                    .build()
        }

        static TicketRequestDTO nullClientId() {
            return TicketRequestDTO.builder()
                    .clientId(null)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope("openid")
                    .state(CommonFixtures.state)
                    .build()
        }

        static TicketRequestDTO nullState() {
            return TicketRequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope(CommonFixtures.scope)
                    .state(null)
                    .build()
        }

        static TicketRequestDTO invalidResponseType() {
            return TicketRequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType("invalid")
                    .scope(CommonFixtures.scope)
                    .state(CommonFixtures.state)
                    .build()
        }

        static TicketRequestDTO scopeWithoutOpenId() {
            return TicketRequestDTO.builder()
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .responseType(CommonFixtures.responseType)
                    .scope("profile phone unknown_value other_unknown")
                    .state(CommonFixtures.state)
                    .build()
        }

        static Client clientWithEmptyRedirectURIs() {
            return new Client(CommonFixtures.clientId);
        }
    }
}
