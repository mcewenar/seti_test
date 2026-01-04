package com.franchise.seti.model.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomExceptions extends RuntimeException {

    protected CustomExceptions(String message) {
        super(message);
    }


    public static class ValidationException extends CustomExceptions {
        public ValidationException(String msg) {
            super(msg);
        }
    }

    public static class NotFoundException extends CustomExceptions {
        public NotFoundException(String msg) {
            super(msg);
        }
    }


    public static class ConflictException extends CustomExceptions {
        public ConflictException(String msg) {
            super(msg);
        }
    }
}

