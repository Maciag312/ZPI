package com.zpi.token

import com.zpi.CommonFixtures
import com.zpi.domain.authCode.consentRequest.AuthCode
import com.zpi.domain.authCode.consentRequest.AuthUserData
import com.zpi.domain.token.issuer.config.TokenClaims
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import java.security.Key

class TokenCommonFixtures {
    static final String secretKey = "DJASFDNUS812DAMNXMANSDHQHW83183JD18JJ1HFG8JXJ12JSH1XCHBUJ28X2JH12J182XJH1F3H1JS81G7RESHD13H71"
    static final SignatureAlgorithm algorithm = SignatureAlgorithm.HS512
    static final Key key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), algorithm.getJcaName())
    static final AuthUserData userData = new AuthUserData(CommonFixtures.scope, CommonFixtures.redirectUri, CommonFixtures.login)
    static final AuthCode authCode = new AuthCode("asdf", userData)
    static final long validity = 123123L

    static Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
    }

    static TokenClaims claims() {
        final Date issuedAt = new Date()
        final Date expirationTime = new Date(issuedAt.getTime() + validity)
        return new TokenClaims(issuedAt, expirationTime)
    }

    static boolean areDatesQuiteEqual(Date one, Date two) {
        final int timeError = 5000
        return Math.abs(one.getTime() - two.getTime()) < timeError
    }
}
