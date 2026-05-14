package co.books.api.common.exception;

/** 포트원 REST API 호출 실패 시 발생하는 예외 (HTTP 502). */
public class PortOneApiException extends RuntimeException {
    public PortOneApiException(String message) {
        super(message);
    }
}