package com.zpi.domain.token.issuer.config;

import com.zpi.domain.rest.ams.AmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuerConfigProviderImpl implements TokenIssuerConfigProvider {
    private final AmsService ams;

    @Override
    public TokenIssuerConfig getConfig() {
        return new TokenIssuerConfig(ams.config());
    }
}
