package com.zpi.domain.authCode.authenticationRequest;

public interface RequestValidator {
    AuthenticationRequest validateAndFillMissingFields(AuthenticationRequest request) throws ValidationFailedException;
}
