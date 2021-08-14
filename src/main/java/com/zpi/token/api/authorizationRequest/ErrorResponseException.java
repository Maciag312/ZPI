package com.zpi.token.api.authorizationRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponseException extends Exception {
    private final ErrorResponseDTO errorResponse;
}
