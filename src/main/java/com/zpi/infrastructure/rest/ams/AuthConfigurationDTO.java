package com.zpi.infrastructure.rest.ams;

import com.zpi.domain.rest.ams.AuthConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthConfigurationDTO {
    private TokenConfigurationDTO token;

    public AuthConfiguration toDomain() {
        return new AuthConfiguration(this.token.getSecretKey(), this.token.getExpirationTime());
    }
}
