package co.books.api.book.dto;

import lombok.Getter;

import java.util.List;

/**
 * 도서 리스트 API 응답 래퍼.
 * { "code": 200, "data": [...], "pageInfo": {...} } 형태로 직렬화된다.
 */
@Getter
public class BooksListApiResponse<T> {

    private final int code;
    private final List<T> data;
    private final PageInfo pageInfo;

    private BooksListApiResponse(int code, List<T> data, PageInfo pageInfo) {
        this.code = code;
        this.data = data;
        this.pageInfo = pageInfo;
    }

    public static <T> BooksListApiResponse<T> ok(List<T> data, PageInfo pageInfo) {
        return new BooksListApiResponse<>(200, data, pageInfo);
    }
}