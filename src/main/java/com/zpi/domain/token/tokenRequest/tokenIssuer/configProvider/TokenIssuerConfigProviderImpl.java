package com.zpi.domain.token.tokenRequest.tokenIssuer.configProvider;

import org.springframework.stereotype.Component;

@Component
public class TokenIssuerConfigProviderImpl implements TokenIssuerConfigProvider {
    @Override
    public TokenIssuerConfig getConfig() {
        return new TokenIssuerConfig();
    }
}
