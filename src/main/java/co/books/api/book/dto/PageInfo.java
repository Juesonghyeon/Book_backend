package co.books.api.book.dto;

/** 페이징 정보 DTO */
public record PageInfo(
        int nowPageNum,
        long totalRows
) {}