package com.petshop.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends ApiException {
    public TooManyRequestsException(String message) {
        super("RATE_LIMITED", message);
    }
}
