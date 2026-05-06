package co.books.api.book.dto;

import java.time.OffsetDateTime;

/**
 * 도서 상세 페이지의 리뷰 항목 DTO.
 */
public record BooksReviewItem(
        Long reviewId,
        String userId,
        String userName,
        Short rating,
        String content,
        OffsetDateTime createdAt
) {}