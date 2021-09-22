package com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenIssuerConfigProviderImpl implements TokenIssuerConfigProvider {
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Override
    public TokenIssuerConfig getConfig() {
        return new TokenIssuerConfig(secretKey);
    }
}
