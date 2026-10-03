package com.petshop.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 403: authenticated but not allowed (wrong role, not the owner, account pending approval). */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedCustomException extends ApiException {
    public AccessDeniedCustomException(String message) {
        super("FORBIDDEN", message);
    }

    public AccessDeniedCustomException(String code, String message) {
        super(code, message);
    }
}
