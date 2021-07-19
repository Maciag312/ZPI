package com.zpi.token.domain.authorizationRequest.request;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends Exception {
    public HttpStatus status;
    public RequestError error;

    public InvalidRequestException(HttpStatus status, RequestError error) {
        this.status = status;
        this.error = error;
    }
}
