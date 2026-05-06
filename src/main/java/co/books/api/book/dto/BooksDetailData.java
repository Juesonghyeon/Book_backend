package co.books.api.book.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * 도서 상세 페이지 API 의 data 영역 DTO.
 */
public record BooksDetailData(
        String bookId,
        String title,
        String subtitle,
        String author,
        String publisher,
        LocalDate publishDate,
        Integer originalPrice,
        Integer salePrice,
        String description,
        String imageUrl,
        String contents,
        Integer stock,
        List<BooksReviewItem> reviewList
) {}