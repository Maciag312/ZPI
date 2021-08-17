package com.zpi.domain.token.consentRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsentRequest {
    private final String ticket;
    private final String state;
}
