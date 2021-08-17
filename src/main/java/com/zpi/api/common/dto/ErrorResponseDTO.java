package com.zpi.api.common.dto;

import com.zpi.domain.common.RequestError;
import com.zpi.domain.token.ticketRequest.request.RequestErrorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseDTO {
    private final RequestErrorType error;
    private final String error_description;
    private final String state;

    public ErrorResponseDTO(RequestError<RequestErrorType> error, String state) {
        this.error = error.getError();
        this.error_description = error.getErrorDescription();
        this.state = state;
    }
}
