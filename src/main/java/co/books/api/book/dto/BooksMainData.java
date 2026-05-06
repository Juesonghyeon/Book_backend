package co.books.api.book.dto;

import java.util.List;

/**
 * 메인 페이지 TopN API 의 data 영역 DTO.
 */
public record BooksMainData(
        List<BooksTopNItem> bestTopN,
        List<BooksTopNItem> newTopN,
        List<BooksTopNItem> basicTopN
) {}