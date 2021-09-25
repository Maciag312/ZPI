package com.zpi.token

import com.zpi.domain.common.RequestError
import com.zpi.domain.token.*
import com.zpi.domain.token.issuer.TokenIssuer
import com.zpi.domain.token.validator.TokenRequestErrorType
import com.zpi.domain.token.validator.TokenRequestValidator
import com.zpi.domain.token.validator.ValidationFailedException
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
                    .error(TokenRequestErrorType.INVALID_CLIENT)
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

    def "should refresh token on validation success and issuer success"() {
        given:
            def request = new RefreshRequest("", "", "", "")
            def expectedToken = null

        and:
            validator.validate(request) >> null
            issuer.refresh(request) >> expectedToken

        when:
            def actualToken = service.refreshToken(request)

        then:
            noExceptionThrown()

        and:
            actualToken == expectedToken
    }

    def "should throw exception on refresh validation failure"() {
        given:
            def request = new RefreshRequest("", "", "" ,"")

            def expectedError = RequestError.builder()
                    .error(TokenRequestErrorType.INVALID_CLIENT)
                    .errorDescription(_ as String)
                    .build()

        and:
            validator.validate(request) >> {
                throw new ValidationFailedException(expectedError)
            }

            issuer.refresh(request) >> null

        when:
            service.refreshToken(request)

        then:
            def thrownException = thrown(TokenErrorResponseException)

        and:
            def response = thrownException.getResponse()

            response.getError() == expectedError.getError().toString()
            response.getErrorDescription() == expectedError.getErrorDescription()
    }
}