package com.zpi.infrastructure.rest.ams;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class TokenConfigurationDTO {
    private long expirationTime;
    private String secretKey;
}
