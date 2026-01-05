package com.franchise.seti.api;

import com.franchise.seti.api.dto.ErrorResponse;
import com.franchise.seti.model.exception.CustomExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class ApiErrorFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                .onErrorResume(CustomExceptions.ValidationException.class, e ->
                        jsonError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", e.getMessage())
                )
                .onErrorResume(CustomExceptions.NotFoundException.class, e ->
                        jsonError(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage())
                )
                .onErrorResume(CustomExceptions.ConflictException.class, e ->
                        jsonError(HttpStatus.CONFLICT, "CONFLICT", e.getMessage())
                )
                .onErrorResume(Throwable.class, e ->
                        jsonError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error")
                );
    }

    private Mono<ServerResponse> jsonError(HttpStatus status, String error, String message) {
        return status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ErrorResponse(error, message));
    }
}
