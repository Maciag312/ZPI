package com.zpi.domain.token.consentRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsentResponse {
    private final AuthCode code;
    private final String state;
}
