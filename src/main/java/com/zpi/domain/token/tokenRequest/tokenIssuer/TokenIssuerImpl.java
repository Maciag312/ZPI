package com.zpi.domain.token.tokenRequest.tokenIssuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository;
import com.zpi.domain.common.AuthCodeGenerator;
import com.zpi.domain.common.RequestError;
import com.zpi.domain.token.refreshRequest.RefreshRequest;
import com.zpi.domain.token.refreshRequest.TokenData;
import com.zpi.domain.token.refreshRequest.TokenRepository;
import com.zpi.domain.token.tokenRequest.Token;
import com.zpi.domain.token.tokenRequest.TokenRequest;
import com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider.TokenIssuerConfigProvider;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenIssuerImpl implements TokenIssuer {
    private final TokenIssuerConfigProvider configProvider;
    private final AuthCodeRepository authCodeRepository;
    private final TokenRepository tokenRepository;
    private final AuthCodeGenerator generator;

    private static final String tokenType = "Bearer";

    @Override
    public Token issue(TokenRequest tokenRequest) throws TokenIssuerFailedException {
        var code = retrieveCode(tokenRequest);
        var claims = new UserClaims(code);
        var accessToken = buildAccessToken(claims);

        final String refreshToken = generator.generate();
        final String expiresIn = expiresInSeconds();
        tokenRepository.save(refreshToken, new TokenData(refreshToken, claims));
        return new Token(accessToken, tokenType, refreshToken, expiresIn);
    }

    @Override
    public Token refresh(RefreshRequest request) throws TokenIssuerFailedException {
        var data = retrieveTokenData(request);
        var claims = new UserClaims(data);
        var accessToken = buildAccessToken(claims);

        final String refreshToken = generator.generate();
        final String expiresIn = expiresInSeconds();
        tokenRepository.save(refreshToken, new TokenData(refreshToken, claims));
        return new Token(accessToken, tokenType, refreshToken, expiresIn);
    }

    private AuthCode retrieveCode(TokenRequest tokenRequest) throws TokenIssuerFailedException {
        var code = authCodeRepository.findByKey(tokenRequest.getCode());
        authCodeRepository.remove(tokenRequest.getCode());

        if (code.isEmpty()) {
            var error = RequestError.<TokenIssuerErrorType>builder()
                    .error(TokenIssuerErrorType.UNRECOGNIZED_AUTH_CODE)
                    .errorDescription("Unrecognized auth_code: " + tokenRequest.getCode())
                    .build();

            throw new TokenIssuerFailedException(error);
        }

        return code.get();
    }

    private TokenData retrieveTokenData(RefreshRequest request) throws TokenIssuerFailedException {
        var data = tokenRepository.findByKey(request.getRefreshToken());

        tokenRepository.remove(request.getRefreshToken());

        if (data.isEmpty()) {
            var error = RequestError.<TokenIssuerErrorType>builder()
                    .error(TokenIssuerErrorType.UNRECOGNIZED_REFRESH_TOKEN)
                    .errorDescription("Unrecognized refresh_token: " + request.getRefreshToken())
                    .build();

            throw new TokenIssuerFailedException(error);
        }

        return data.get();
    }

    private String buildAccessToken(UserClaims userClaims) {
        final var config = configProvider.getConfig();
        final var claims = config.getClaims();

        var jwtClaims = Jwts.claims()
                .setIssuer(claims.getIssuer())
                .setSubject(claims.getSubject())
                .setAudience(claims.getAudience())
                .setIssuedAt(claims.getIssuedAt())
                .setExpiration(claims.getExpirationTime());

        for (Map.Entry<String, String> claim : userClaims.getClaims().entrySet()) {
            jwtClaims.put(claim.getKey(), claim.getValue());
        }

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(config.getKey())
                .compact();
    }

    private String expiresInSeconds() {
        return String.valueOf((int) configProvider.getConfig().getValidityInMilliseconds() / 1000);
    }
}
