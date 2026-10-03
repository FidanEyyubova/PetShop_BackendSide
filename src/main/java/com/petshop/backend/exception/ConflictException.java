package com.petshop.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 409: duplicate resource or a state that conflicts with the request (duplicate review, stock race ...). */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super("CONFLICT", message);
    }

    public ConflictException(String code, String message) {
        super(code, message);
    }
}
