package com.zpi.token.tokenRequest


import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.common.AuthCodeGenerator
import com.zpi.domain.token.TokenRepository
import com.zpi.domain.token.TokenRequest
import com.zpi.domain.token.issuer.TokenIssuer
import com.zpi.domain.token.issuer.TokenIssuerImpl
import com.zpi.domain.token.issuer.config.TokenIssuerConfig
import com.zpi.domain.token.issuer.config.TokenIssuerConfigProvider
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

            body.getIssuer() == ""
            TokenCommonFixtures.areDatesQuiteEqual(body.getIssuedAt(), TokenCommonFixtures.claims().getIssuedAt())
            TokenCommonFixtures.areDatesQuiteEqual(body.getExpiration(), TokenCommonFixtures.claims().getExpirationTime())
            body.get("scope") == TokenCommonFixtures.authCode.getUserData().getScope()
            body.get("username_hash") == TokenCommonFixtures.authCode.getUserData().getUsername()
    }
}
