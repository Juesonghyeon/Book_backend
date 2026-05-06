package co.books.api.book.dto;

import lombok.Getter;

/**
 * Books API 공통 응답 래퍼.
 * { "code": 200, "data": ... } 형태로 직렬화된다.
 */
@Getter
public class BooksApiResponse<T> {

    private final int code;
    private final T data;

    private BooksApiResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> BooksApiResponse<T> ok(T data) {
        return new BooksApiResponse<>(200, data);
    }
}