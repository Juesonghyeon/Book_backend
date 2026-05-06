package co.books.api.book.dto;

import java.time.LocalDate;

/** 도서 리스트 항목 DTO */
public record BookListItem(
        String id,
        String title,
        String subtitle,
        String author,
        Integer price,
        LocalDate publishDate,
        String imageUrl
) {}