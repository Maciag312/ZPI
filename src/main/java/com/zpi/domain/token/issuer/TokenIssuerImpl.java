package com.zpi.domain.token.issuer;

import com.zpi.domain.authCode.consentRequest.AuthCode;
import com.zpi.domain.authCode.consentRequest.authCodePersister.AuthCodeRepository;
import com.zpi.domain.common.AuthCodeGenerator;
import com.zpi.domain.token.RefreshRequest;
import com.zpi.domain.token.Token;
import com.zpi.domain.token.TokenRepository;
import com.zpi.domain.token.TokenRequest;
import com.zpi.domain.token.issuer.config.TokenIssuerConfigProvider;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class TokenIssuerImpl implements TokenIssuer {
    private final TokenIssuerConfigProvider configProvider;
    private final AuthCodeRepository authCodeRepository;
    private final TokenRepository tokenRepository;
    private final AuthCodeGenerator generator;

    private static final String tokenType = "Bearer";

    @Override
    public Token issue(TokenRequest tokenRequest) throws NoSuchElementException {
        var code = retrieveCode(tokenRequest);
        var claims = new UserClaims(code);

        return issueToken(claims);
    }

    @Override
    public Token refresh(RefreshRequest request) throws NoSuchElementException {
        var data = retrieveTokenData(request);
        var claims = new UserClaims(data);

        return issueToken(claims);
    }

    private Token issueToken(UserClaims claims) {
        var accessToken = buildAccessToken(claims);
        String refreshToken = generator.generate();
        String expiresIn = expiresInSeconds();

        tokenRepository.save(refreshToken, new TokenData(refreshToken, claims));

        return new Token(accessToken, tokenType, refreshToken, expiresIn);
    }

    private AuthCode retrieveCode(TokenRequest tokenRequest) throws NoSuchElementException {
        var code = authCodeRepository.findByKey(tokenRequest.getCode());
        authCodeRepository.remove(tokenRequest.getCode());

        return code.orElseThrow();
    }

    private TokenData retrieveTokenData(RefreshRequest request) throws NoSuchElementException {
        var data = tokenRepository.findByKey(request.getRefreshToken());
        tokenRepository.remove(request.getRefreshToken());

        return data.orElseThrow();
    }

    private String buildAccessToken(UserClaims userClaims) {
        var config = configProvider.getConfig();
        var claims = config.getClaims();

        var jwtClaims = Jwts.claims()
                .setIssuedAt(claims.getIssuedAt())
                .setExpiration(claims.getExpirationTime());

        jwtClaims.putAll(userClaims.getClaims());

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(config.getKey())
                .compact();
    }

    private String expiresInSeconds() {
        return String.valueOf((int) configProvider.getConfig().getValidityInMilliseconds() / 1000);
    }
}
