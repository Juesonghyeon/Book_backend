package co.books.api.common.exception;

/** 본인 리소스가 아닌 경우 발생하는 예외 (HTTP 403). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}