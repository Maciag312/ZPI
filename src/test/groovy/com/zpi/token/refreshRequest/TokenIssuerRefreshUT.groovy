package com.zpi.token.refreshRequest

import com.zpi.CommonFixtures
import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.common.AuthCodeGenerator
import com.zpi.domain.token.refreshRequest.RefreshRequest
import com.zpi.domain.token.refreshRequest.TokenData
import com.zpi.domain.token.refreshRequest.TokenRepository
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuer
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerImpl
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfig
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfigProvider
import com.zpi.token.TokenCommonFixtures
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject

class TokenIssuerRefreshUT extends Specification {
    def configProvider = Mock(TokenIssuerConfigProvider)
    def authCodeRepository = Mock(AuthCodeRepository)
    def tokenRepository = Mock(TokenRepository)
    def generator = Mock(AuthCodeGenerator)

    @Subject
    private TokenIssuer issuer = new TokenIssuerImpl(configProvider, authCodeRepository, tokenRepository, generator)

    def "should refresh token if data correct"() {
        given:
            def refreshToken = "asdf"
            def request = new RefreshRequest(CommonFixtures.clientId, CommonFixtures.grantType, refreshToken, CommonFixtures.scope)

            def config = new TokenIssuerConfig(TokenCommonFixtures.secretKey)
            ReflectionTestUtils.setField(config, "claims", TokenCommonFixtures.claims())
        and:
            generator.generate() >> "fdsafdsa"
            configProvider.getConfig() >> config
            authCodeRepository.findByKey(TokenCommonFixtures.authCode.getValue()) >> Optional.of(TokenCommonFixtures.authCode)
            tokenRepository.findByKey(refreshToken) >> Optional.of(new TokenData(refreshToken, CommonFixtures.scope, CommonFixtures.userDTO().login))

        when:
            def result = issuer.refresh(request)

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

    def "should throw on non existing refresh_token"() {

    }
}
