package com.zpi.token.refreshRequest

import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.common.CodeGenerator
import com.zpi.domain.rest.ams.AuthConfiguration
import com.zpi.domain.token.RefreshRequest
import com.zpi.domain.token.TokenRepository
import com.zpi.domain.token.issuer.TokenData
import com.zpi.domain.token.issuer.TokenIssuer
import com.zpi.domain.token.issuer.TokenIssuerImpl
import com.zpi.domain.token.issuer.config.TokenIssuerConfig
import com.zpi.domain.token.issuer.config.TokenIssuerConfigProvider
import com.zpi.testUtils.CommonFixtures
import com.zpi.token.TokenCommonFixtures
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject

class TokenIssuerRefreshUT extends Specification {
    def configProvider = Mock(TokenIssuerConfigProvider)
    def authCodeRepository = Mock(AuthCodeRepository)
    def tokenRepository = Mock(TokenRepository)
    def generator = Mock(CodeGenerator)

    @Subject
    private TokenIssuer issuer = new TokenIssuerImpl(configProvider, authCodeRepository, tokenRepository, generator)

    def "should refresh token if data correct"() {
        given:
            def refreshToken = "asdf"
            def request = new RefreshRequest(CommonFixtures.clientId, CommonFixtures.grantType, refreshToken, CommonFixtures.scope)
            def config = new TokenIssuerConfig(new AuthConfiguration(TokenCommonFixtures.secretKey, 1000L))

            ReflectionTestUtils.setField(config, "claims", TokenCommonFixtures.claims())
        and:
            generator.ticketCode() >> "fdsafdsa"
            configProvider.getConfig() >> config
            authCodeRepository.findByKey(TokenCommonFixtures.authCode.getValue()) >> Optional.of(TokenCommonFixtures.authCode)
            tokenRepository.findByKey(refreshToken) >> Optional.of(new TokenData(refreshToken, CommonFixtures.scope, CommonFixtures.userDTO().email))

        when:
            def result = issuer.refresh(request)

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
            body.get("username") == TokenCommonFixtures.authCode.getUserData().getUsername()
    }
}
