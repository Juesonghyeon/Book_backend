package co.books.api.book.dto;

/**
 * 메인 페이지 TopN 목록의 도서 항목 DTO.
 */
public record BooksTopNItem(
        String id,
        String title,
        String author,
        Integer price,
        String imageUrl
) {}