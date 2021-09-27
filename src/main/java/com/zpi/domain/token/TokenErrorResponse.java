package com.zpi.domain.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenErrorResponse {
    private final String error;
    private final String errorDescription;
}
