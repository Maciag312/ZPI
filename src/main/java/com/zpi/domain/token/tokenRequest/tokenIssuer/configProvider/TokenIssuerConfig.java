package com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Getter
@AllArgsConstructor
public class TokenIssuerConfig {
    private final TokenClaims claims;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final long validityInMilliseconds = 3600000L;

    private Key key;

    public TokenIssuerConfig() {
        final String issuer = "asdf";
        final String subject = "asdf";
        final String audience = "asdf";
        final Date issuedAt = new Date();
        final Date expirationTime = new Date(issuedAt.getTime() + validityInMilliseconds);

        claims = new TokenClaims(issuer, subject, audience, issuedAt, expirationTime);
    }

    public TokenIssuerConfig(String secretKey, Key key, TokenClaims claims) {
        this.secretKey = secretKey;
        this.key = key;
        this.claims = claims;
    }

    @PostConstruct
    protected void init() {
        key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secretKey), SignatureAlgorithm.HS384.getJcaName());
    }
}
