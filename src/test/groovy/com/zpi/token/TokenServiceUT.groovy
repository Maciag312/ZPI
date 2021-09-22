package com.zpi.token


import com.zpi.domain.common.RequestError
import com.zpi.domain.token.TokenErrorResponseException
import com.zpi.domain.token.TokenService
import com.zpi.domain.token.TokenServiceImpl
import com.zpi.domain.token.tokenRequest.TokenRequest
import com.zpi.domain.token.tokenRequest.requestValidator.TokenRequestErrorType
import com.zpi.domain.token.tokenRequest.requestValidator.TokenRequestValidator
import com.zpi.domain.token.tokenRequest.requestValidator.ValidationFailedException
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuer
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerErrorType
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerFailedException
import spock.lang.Specification
import spock.lang.Subject

class TokenServiceUT extends Specification {
    def issuer = Mock(TokenIssuer)
    def validator = Mock(TokenRequestValidator)

    @Subject
    private TokenService service = new TokenServiceImpl(issuer, validator)

    def "should return token on validation success and issuer success"() {
        given:
            def request = TokenRequest.builder().build()
            def expectedToken = null

        and:
            validator.validate(request) >> null
            issuer.issue(request) >> expectedToken

        when:
            def actualToken = service.getToken(request)

        then:
            noExceptionThrown()

        and:
            actualToken == expectedToken
    }

    def "should throw exception on validation failure"() {
        given:
            def request = TokenRequest.builder()
                    .build()


            def expectedError = RequestError.builder()
                    .error(TokenRequestErrorType.UNRECOGNIZED_CLIENT_ID)
                    .errorDescription(_ as String)
                    .build()

        and:
            validator.validate(request) >> {
                throw new ValidationFailedException(expectedError)
            }

            issuer.issue(request) >> null

        when:
            service.getToken(request)

        then:
            def thrownException = thrown(TokenErrorResponseException)

        and:
            def response = thrownException.getResponse()

            response.getError() == expectedError.getError().toString()
            response.getErrorDescription() == expectedError.getErrorDescription()
    }

    def "should throw exception on issuer failure"() {
        given:
            def request = TokenRequest.builder()
                    .build()

            def expectedError = RequestError.builder()
                    .error(TokenIssuerErrorType.UNRECOGNIZED_AUTH_CODE)
                    .errorDescription("asdf")
                    .build()

        and:
            validator.validate(request) >> null
            issuer.issue(request) >> {
                throw new TokenIssuerFailedException(expectedError)
            }

        when:
            service.getToken(request)

        then:
            def thrownException = thrown(TokenErrorResponseException)

        and:
            def response = thrownException.getResponse()

            response.getError() == expectedError.getError().toString()
            response.getErrorDescription() == expectedError.getErrorDescription()
    }
}