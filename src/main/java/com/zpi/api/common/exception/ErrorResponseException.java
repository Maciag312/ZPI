package com.zpi.api.common.exception;

import com.zpi.api.common.dto.ErrorResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponseException extends Exception {
    private final ErrorResponseDTO errorResponse;
}
