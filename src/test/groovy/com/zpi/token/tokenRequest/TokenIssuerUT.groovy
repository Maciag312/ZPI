package com.zpi.token.tokenRequest


import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.common.AuthCodeGenerator
import com.zpi.domain.token.refreshRequest.TokenRepository
import com.zpi.domain.token.tokenRequest.TokenRequest
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuer
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerErrorType
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerFailedException
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerImpl
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfig
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfigProvider
import com.zpi.token.TokenCommonFixtures
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject

class TokenIssuerUT extends Specification {
    def configProvider = Mock(TokenIssuerConfigProvider)
    def authCodeRepository = Mock(AuthCodeRepository)
    def tokenRepository = Mock(TokenRepository)
    def generator = Mock(AuthCodeGenerator)

    @Subject
    private TokenIssuer issuer = new TokenIssuerImpl(configProvider, authCodeRepository, tokenRepository, generator)

    def "should return token when data correct"() {
        given:
            def request = TokenRequest.builder().code(TokenCommonFixtures.authCode.getValue()).build()

            def config = new TokenIssuerConfig(TokenCommonFixtures.secretKey)
            ReflectionTestUtils.setField(config, "claims", TokenCommonFixtures.claims())
        and:
            generator.generate() >> "fdsafdsa"
            configProvider.getConfig() >> config
            authCodeRepository.findByKey(TokenCommonFixtures.authCode.getValue()) >> Optional.of(TokenCommonFixtures.authCode)

        when:
            def result = issuer.issue(request)

        then:
            !result.getAccessToken().isEmpty()

        and:
            def parsed = TokenCommonFixtures.parseToken(result.getAccessToken())
            parsed.getHeader().getAlgorithm() == TokenCommonFixtures.algorithm.getValue()

        and:
            def body = parsed.getBody()

            body.getIssuer() == TokenCommonFixtures.claims().getIssuer()
            body.getSubject() == TokenCommonFixtures.claims().getSubject()
            body.getAudience() == TokenCommonFixtures.claims().getAudience()
            TokenCommonFixtures.areDatesQuiteEqual(body.getIssuedAt(), TokenCommonFixtures.claims().getIssuedAt())
            TokenCommonFixtures.areDatesQuiteEqual(body.getExpiration(), TokenCommonFixtures.claims().getExpirationTime())
            body.get("scope") == TokenCommonFixtures.authCode.getUserData().getScope()
            body.get("username") == TokenCommonFixtures.authCode.getUserData().getUsername()
    }

    def "should throw exception on non existing auth code"() {
        given:
            def request = TokenRequest.builder().code(TokenCommonFixtures.authCode.getValue()).build()

        and:
            authCodeRepository.findByKey(TokenCommonFixtures.authCode.getValue()) >> Optional.empty()

        when:
            issuer.issue(request)

        then:
            def exception = thrown(TokenIssuerFailedException)

            exception.getError().getError() == TokenIssuerErrorType.UNRECOGNIZED_AUTH_CODE
            !exception.getError().getErrorDescription().isEmpty()
    }
}
