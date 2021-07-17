package com.zpi.token.api.authorizationRequest;

import com.zpi.token.domain.authorizationRequest.RequestError;
import com.zpi.token.domain.authorizationRequest.RequestErrorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class RequestErrorDTO {
    private final RequestErrorType error;
    private final String error_description;

    public RequestErrorDTO(RequestError error) {
        this.error = error.getError();
        this.error_description = error.getErrorDescription();
    }
}
