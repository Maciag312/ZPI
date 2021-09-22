package com.zpi.token

import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository
import com.zpi.domain.token.tokenRequest.TokenRequest
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuer
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerErrorType
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerFailedException
import com.zpi.domain.token.tokenRequest.tokenIssuer.TokenIssuerImpl
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenClaims
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfig
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfigProvider
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject

import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import java.security.Key

class TokenIssuerUT extends Specification {
    def configProvider = Mock(TokenIssuerConfigProvider)
    def authCodeRepository = Mock(AuthCodeRepository)

    @Subject
    private TokenIssuer issuer = new TokenIssuerImpl(configProvider, authCodeRepository)

    private
    def "should return token when data correct"() {
        given:
            def request = TokenRequest.builder().code(Fixtures.authCode.getValue()).build()

            def config = new TokenIssuerConfig(Fixtures.secretKey)
            ReflectionTestUtils.setField(config, "claims", Fixtures.claims())
        and:
            configProvider.getConfig() >> config
            authCodeRepository.findByKey(Fixtures.authCode.getValue()) >> Optional.of(Fixtures.authCode)

        when:
            def result = issuer.issue(request)

        then:
            !result.getAccessToken().isEmpty()

        and:
            def parsed = Fixtures.parseToken(result.getAccessToken())

            parsed.getHeader().getAlgorithm() == Fixtures.algorithm.getValue()

        and:
            var body = parsed.getBody()
            body.getIssuer() == Fixtures.claims().getIssuer()
            body.getSubject() == Fixtures.claims().getSubject()
            body.getAudience() == Fixtures.claims().getAudience()
            Fixtures.areDatesQuiteEqual(body.getIssuedAt(), Fixtures.claims().getIssuedAt())
            Fixtures.areDatesQuiteEqual(body.getExpiration(), Fixtures.claims().getExpirationTime())
            parsed.getBody().get("scope") == Fixtures.authCode.getScope()
    }

    def "should throw exception on non existing auth code"() {
        given:
            def request = TokenRequest.builder().code(Fixtures.authCode.getValue()).build()

        and:
            authCodeRepository.findByKey(Fixtures.authCode.getValue()) >> Optional.empty()

        when:
            issuer.issue(request)

        then:
            def exception = thrown(TokenIssuerFailedException)

            exception.getError().getError() == TokenIssuerErrorType.UNRECOGNIZED_AUTH_CODE
            !exception.getError().getErrorDescription().isEmpty()
    }

    private class Fixtures {
        static final String secretKey = "DJASFDNUS812DAMNXMANSDHQHW83183JD18JJ1HFG8JXJ12JSH1XCHBUJ28X2JH12J182XJH1F3H1JS81G7RESHD13H71"
        static final SignatureAlgorithm algorithm = SignatureAlgorithm.HS512
        static final Key key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(Fixtures.secretKey), algorithm.getJcaName())
        static final AuthCode authCode = new AuthCode("asdf", "photos profile")
        static final long validity = 123123L

        static Jws<Claims> parseToken(String token) {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        }

        static TokenClaims claims() {
            final String issuer = "asdf"
            final String subject = "asdf"
            final String audience = "asdf"
            final Date issuedAt = new Date()
            final Date expirationTime = new Date(issuedAt.getTime() + validity)
            return new TokenClaims(issuer, subject, audience, issuedAt, expirationTime)
        }

        static boolean areDatesQuiteEqual(Date one, Date two) {
            final int timeError = 5000
            return Math.abs(one.getTime() - two.getTime()) < timeError
        }
    }
}
