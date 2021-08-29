package com.zpi.token

import com.zpi.CommonFixtures
import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.authCodeIssuer.AuthCodeRepository
import com.zpi.domain.client.ClientRepository
import com.zpi.domain.token.tokenRequest.TokenRequest
import com.zpi.domain.token.tokenRequest.requestValidator.TokenRequestErrorType
import com.zpi.domain.token.tokenRequest.requestValidator.TokenRequestValidator
import com.zpi.domain.token.tokenRequest.requestValidator.TokenRequestValidatorImpl
import com.zpi.domain.token.tokenRequest.requestValidator.ValidationFailedException
import spock.lang.Specification
import spock.lang.Subject

class TokenRequestValidatorUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def authCodeRepository = Mock(AuthCodeRepository)

    @Subject
    private TokenRequestValidator validator = new TokenRequestValidatorImpl(clientRepository, authCodeRepository)

    def "should not throw on correct data"() {
        given:
            def request = Fixtures.correctRequest()
            def client = CommonFixtures.client()

        and:
            clientRepository.getByKey(request.getClientId()) >> Optional.of(client)
            authCodeRepository.getByKey(request.getCode()) >> Optional.of(new AuthCode())

        when:
            validator.validate(request)

        then:
            noExceptionThrown()
    }

    def "should throw on incorrect grant_type"() {
        given:
            def request = TokenRequest.builder()
                    .grantType(grantType)
                    .code(Fixtures.correctRequest().getCode())
                    .build()

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.INVALID_GRANT_TYPE
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            grantType      || expectedDescription
            "unrecognized" || "Unrecognized grant_type 'unrecognized'. Expected 'authorization_code'."
            ""             || "Unrecognized grant_type ''. Expected 'authorization_code'."
            null           || "Unrecognized grant_type 'null'. Expected 'authorization_code'."
    }

    def "should throw on incorrect code"() {
        given:
            def request = TokenRequest.builder()
                    .grantType(CommonFixtures.grantType)
                    .code(code)
                    .build()
        and:
            authCodeRepository.getByKey(request.getCode()) >> Optional.empty()

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.INVALID_CODE
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            code           || expectedDescription
            "unrecognized" || "Unrecognized code 'unrecognized'"
            ""             || "Unrecognized code ''"
            null           || "Unrecognized code 'null'"
    }

    def "should throw on non matching redirect_uri"() {
        given:
            def client = CommonFixtures.client()

            def request = TokenRequest.builder()
                    .grantType(CommonFixtures.grantType)
                    .code(Fixtures.correctRequest().getCode())
                    .redirectUri(redirectUri)
                    .clientId(client.getId())
                    .build()
        and:
            clientRepository.getByKey(client.getId()) >> Optional.of(client)
            authCodeRepository.getByKey(request.getCode()) >> Optional.of(new AuthCode())

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.UNRECOGNIZED_REDIRECT_URI
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            redirectUri    || expectedDescription
            "unrecognized" || "Unrecognized redirect_uri 'unrecognized'"
            ""             || "Unrecognized redirect_uri ''"
            null           || "Unrecognized redirect_uri 'null'"
    }

    def "should throw on unrecognized client_id"() {
        given:
            def request = TokenRequest.builder()
                    .grantType(CommonFixtures.grantType)
                    .code(Fixtures.correctRequest().getCode())
                    .clientId(clientId)
                    .build()

        and:
            clientRepository.getByKey(clientId) >> Optional.empty()
            authCodeRepository.getByKey(request.getCode()) >> Optional.of(new AuthCode())

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.UNRECOGNIZED_CLIENT_ID
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            clientId       || expectedDescription
            "unrecognized" || "Unrecognized client_id 'unrecognized'"
            ""             || "Unrecognized client_id ''"
            null           || "Unrecognized client_id 'null'"
    }

    private class Fixtures {
        final static String code = "asdfasdf"

        static TokenRequest correctRequest() {
            return TokenRequest.builder()
                    .grantType(CommonFixtures.grantType)
                    .code(code)
                    .redirectUri(CommonFixtures.redirectUri)
                    .clientId(CommonFixtures.clientId)
                    .build()
        }
    }
}
