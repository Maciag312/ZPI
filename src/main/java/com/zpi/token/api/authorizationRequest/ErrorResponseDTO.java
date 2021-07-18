package com.zpi.token.api.authorizationRequest;

import com.zpi.token.domain.authorizationRequest.request.RequestError;
import com.zpi.token.domain.authorizationRequest.request.RequestErrorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseDTO {
    private final RequestErrorType error;
    private final String error_description;

    public ErrorResponseDTO(RequestError error) {
        this.error = error.getError();
        this.error_description = error.getErrorDescription();
    }
}
