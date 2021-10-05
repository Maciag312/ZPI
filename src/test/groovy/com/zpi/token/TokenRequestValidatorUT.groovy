package com.zpi.token

import com.zpi.CommonFixtures
import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.AuthUserData
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.organization.client.Client
import com.zpi.domain.organization.client.ClientRepository
import com.zpi.domain.token.TokenRepository
import com.zpi.domain.token.RefreshRequest
import com.zpi.domain.token.TokenRequest
import com.zpi.domain.token.issuer.TokenData
import com.zpi.domain.token.validator.TokenRequestErrorType
import com.zpi.domain.token.validator.TokenRequestValidator
import com.zpi.domain.token.validator.TokenRequestValidatorImpl
import com.zpi.domain.token.validator.ValidationFailedException
import spock.lang.Specification
import spock.lang.Subject

class TokenRequestValidatorUT extends Specification {
    def clientRepository = Mock(ClientRepository)
    def authCodeRepository = Mock(AuthCodeRepository)
    def tokenRepository = Mock(TokenRepository)

    @Subject
    private TokenRequestValidator validator = new TokenRequestValidatorImpl(clientRepository, authCodeRepository, tokenRepository)

    def "should not throw on correct data"() {
        given:
            def request = Fixtures.correctRequest()
            def client = CommonFixtures.client()

        and:
            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            authCodeRepository.findByKey(request.getCode()) >> Optional.of(TokenCommonFixtures.authCode)

        when:
            validator.validate(request)

        then:
            noExceptionThrown()
    }

    def "should throw invalid_client on invalid client_id"() {
        given:
            def request = TokenRequest.builder()
                    .grantType(CommonFixtures.grantType)
                    .code(Fixtures.correctRequest().getCode())
                    .clientId(clientId)
                    .build()

        and:
            clientRepository.findByKey(clientId) >> Optional.empty()
            authCodeRepository.findByKey(request.getCode()) >> Optional.of(new AuthCode("", new AuthUserData("", "", "")))

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.INVALID_CLIENT
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            clientId       || expectedDescription
            "unrecognized" || "Unrecognized client_id 'unrecognized'"
            ""             || "Unrecognized client_id ''"
            null           || "Unrecognized client_id 'null'"
    }

    def "should throw invalid_grant on incorrect code"() {
        given:
            def request = TokenRequest.builder()
                    .grantType(CommonFixtures.grantType)
                    .clientId(CommonFixtures.clientId)
                    .code(code)
                    .build()

            def client = new Client(CommonFixtures.clientId)

        and:
            authCodeRepository.findByKey(request.getCode()) >> Optional.empty()
            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.INVALID_GRANT
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            code           || expectedDescription
            "unrecognized" || "Unrecognized code 'unrecognized'"
            ""             || "Unrecognized code ''"
            null           || "Unrecognized code 'null'"
    }

    def "should throw invalid_grant on incorrect refresh_token"() {
        given:
            def request = new RefreshRequest(CommonFixtures.clientId, CommonFixtures.grantType, refreshToken, "profile")
            def client = new Client(CommonFixtures.clientId)

        and:
            tokenRepository.findByKey(request.getRefreshToken()) >> Optional.empty()
            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.INVALID_GRANT
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            refreshToken   || expectedDescription
            "unrecognized" || "Unrecognized refresh_token 'unrecognized'"
            ""             || "Unrecognized refresh_token ''"
            null           || "Unrecognized refresh_token 'null'"
    }

    def "should throw unauthorized_client when client is not authorized to use provided grant_type"() {
        given:
            def request = Fixtures.correctRequest()
            def client = new Client("asdf")
            client.setAvailableGrantTypes(Set.of(""))

        and:
            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            authCodeRepository.findByKey(request.getCode()) >> Optional.of(new AuthCode("", new AuthUserData("", "", "")))

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.UNAUTHORIZED_CLIENT
            thrown.getError().getErrorDescription() == "Client is not authorized to use provided grant_type"
    }

    def "should throw unsupported_grant_type on incorrect grant_type"() {
        given:
            def request = TokenRequest.builder()
                    .grantType(grantType)
                    .clientId(CommonFixtures.clientId)
                    .code(Fixtures.correctRequest().getCode())
                    .build()

            def client = new Client(CommonFixtures.clientId)

        and:
            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
            authCodeRepository.findByKey(request.getCode()) >> Optional.of(new AuthCode("", new AuthUserData("", "", "")))

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.UNSUPPORTED_GRANT_TYPE
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            grantType      || expectedDescription
            "unrecognized" || "Unrecognized grant_type 'unrecognized'. Expected 'authorization_code'."
            ""             || "Unrecognized grant_type ''. Expected 'authorization_code'."
            null           || "Unrecognized grant_type 'null'. Expected 'authorization_code'."
    }

    def "for refresh request should throw invalid_scope on incorrect scope"() {
        given:
            def tokenData = new TokenData("asdfdf", "", "")
            def request = new RefreshRequest(CommonFixtures.clientId, CommonFixtures.grantType, tokenData.getValue(), scope)

            def client = new Client(CommonFixtures.clientId)
            client.getAvailableRedirectUri().add(CommonFixtures.redirectUri)

        and:
            clientRepository.findByKey(request.getClientId()) >> Optional.of(client)
        tokenRepository.findByKey(request.getRefreshToken()) >> Optional.of(tokenData)

        when:
            validator.validate(request)

        then:
            def thrown = thrown(ValidationFailedException)

        and:
            thrown.getError().error == TokenRequestErrorType.INVALID_SCOPE
            thrown.getError().getErrorDescription() == expectedDescription

        where:
            scope                   || expectedDescription
            ""                      || "Unrecognized scope ''"
            null                    || "Unrecognized scope 'null'"
            "photos videos profile" || "Scope exceeds the scope granted by the resource owner"
    }

    private class Fixtures {
        final static String code = "asdfasdf"

        static TokenRequest correctRequest() {
            return TokenRequest.builder()
                    .grantType(CommonFixtures.grantType)
                    .code(code)
                    .clientId(CommonFixtures.clientId)
                    .redirectUri(CommonFixtures.redirectUri)
                    .build()
        }
    }
}
