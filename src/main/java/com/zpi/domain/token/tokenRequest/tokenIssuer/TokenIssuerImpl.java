package com.zpi.domain.token.tokenRequest.tokenIssuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.token.tokenRequest.Token;
import com.zpi.domain.token.tokenRequest.TokenRequest;
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfigProvider;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuerImpl implements TokenIssuer {
    private final TokenIssuerConfigProvider configProvider;
    private final AuthCodeRepository authCodeRepository;

    @Override
    public Token issue(TokenRequest tokenRequest) throws TokenIssuerFailedException {
        var code = retrieveCode(tokenRequest);
        var accessToken = buildAccessToken(code);

        final String tokenType = "Bearer";
        final String refreshToken = createRefreshToken();
        final String expiresIn = expiresInSeconds();
        return new Token(accessToken, tokenType, refreshToken, expiresIn);
    }

    private AuthCode retrieveCode(TokenRequest tokenRequest) throws TokenIssuerFailedException {
        var code = authCodeRepository.findByKey(tokenRequest.getCode());
        if (code.isEmpty()) {
            var error = RequestError.<TokenIssuerErrorType>builder()
                    .error(TokenIssuerErrorType.UNRECOGNIZED_AUTH_CODE)
                    .errorDescription("Unrecognized auth_code: " + tokenRequest.getCode())
                    .build();

            throw new TokenIssuerFailedException(error);
        }

        return code.get();
    }

    private String buildAccessToken(AuthCode code) {
        final var config = configProvider.getConfig();
        final var claims = config.getClaims();

        var jwtClaims = Jwts.claims()
                .setIssuer(claims.getIssuer())
                .setSubject(claims.getSubject())
                .setAudience(claims.getAudience())
                .setIssuedAt(claims.getIssuedAt())
                .setExpiration(claims.getExpirationTime());

        jwtClaims.put("scope", code.getScope());

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(config.getKey())
                .compact();
    }

    private String createRefreshToken() {
        return "asdf";
    }

    private String expiresInSeconds() {
        return String.valueOf((int) configProvider.getConfig().getValidityInMilliseconds() / 1000);
    }
}
