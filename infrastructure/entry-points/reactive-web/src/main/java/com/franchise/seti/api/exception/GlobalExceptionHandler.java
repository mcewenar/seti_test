package com.franchise.seti.api.exception;

import com.franchise.seti.model.exception.CustomExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomExceptions.ValidationException.class)
    public Mono<ProblemDetail> handleValidation(CustomExceptions.ValidationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Validation error");
        return Mono.just(pd);
    }

    @ExceptionHandler(CustomExceptions.NotFoundException.class)
    public Mono<ProblemDetail> handleNotFound(CustomExceptions.NotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Not found");
        return Mono.just(pd);
    }

    @ExceptionHandler(CustomExceptions.ConflictException.class)
    public Mono<ProblemDetail> handleConflict(CustomExceptions.ConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflict");
        return Mono.just(pd);
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ProblemDetail> handleGeneric(Throwable ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        pd.setTitle("Internal error");
        return Mono.just(pd);
    }
}

