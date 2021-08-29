package com.zpi.api.common.dto;

import com.zpi.domain.common.RequestError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseDTO<ErrorType> {
    private final ErrorType error;
    private final String error_description;
    private final String state;

    public ErrorResponseDTO(RequestError<ErrorType> error, String state) {
        this.error = error.getError();
        this.error_description = error.getErrorDescription();
        this.state = state;
    }
}
