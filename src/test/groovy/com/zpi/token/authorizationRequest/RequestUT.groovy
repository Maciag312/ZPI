package com.zpi.token.authorizationRequest

import com.zpi.token.api.authorizationRequest.ErrorResponseDTO
import com.zpi.token.api.authorizationRequest.ResponseDTO
import com.zpi.token.domain.Client
import com.zpi.token.domain.ClientRepository
import com.zpi.token.domain.TokenService
import com.zpi.token.domain.authorizationRequest.request.*
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class RequestUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def requestValidation = Mock(RequestValidation)

    @Subject
    private TokenService tokenService = new TokenService(clientRepository, requestValidation)

    def "should return auth code when request is valid"() {
        given:
            def request = CommonFixtures.correctRequest()
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> null

        when:
            def response = tokenService.authorizationRequest(request)

        then:
            response.getStatusCode() == HttpStatus.OK

        and:
            def responseBody = response.getBody() as ResponseDTO

            responseBody.getCode().length() != 0
            responseBody.getState() == CommonFixtures.defaultState
    }

    def "should return error on wrong request"() {
        given:
            def request = CommonFixtures.correctRequest()
            def client = CommonFixtures.defaultClient()

            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            requestValidation.validate(_ as Request, _ as Client) >> {
                throw Fixtures.sampleException()
            }

        when:
            def response = tokenService.authorizationRequest(request)

        then:
            response.getStatusCodeValue() == Fixtures.defaultErrorHttpStatus.value()

        and:
            def expected = new ErrorResponseDTO(Fixtures.sampleRequestError(), request.getState())
            def responseBody = response.getBody() as ErrorResponseDTO

            responseBody == expected
    }

    private class Fixtures {
        private static final RequestErrorType defaultErrorType = RequestErrorType.UNAUTHORIZED_CLIENT
        private static final String defaultErrorDescription = "Unauthorized client id"
        private static final HttpStatus defaultErrorHttpStatus = HttpStatus.BAD_GATEWAY

        static InvalidRequestException sampleException() {
            return new InvalidRequestException(defaultErrorHttpStatus, sampleRequestError())
        }

        static RequestError sampleRequestError() {
            return RequestError.builder()
                    .error(defaultErrorType)
                    .errorDescription(defaultErrorDescription)
                    .build()
        }
    }
}
