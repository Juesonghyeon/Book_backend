package co.books.api.common.exception;

import lombok.Getter;

import java.time.OffsetDateTime;

/** 에러 응답 공통 DTO. */
@Getter
public class ErrorResponse {

    private final int code;
    private final String message;
    private final OffsetDateTime timestamp;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }
}