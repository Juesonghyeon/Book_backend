package co.books.api.book.dto;

/**
 * 선택된 도서 정보 응답 DTO.
 * 책 제목, 원가격, 할인가격, 이미지 URL 만 포함한다.
 */
public record SelectBookItem(
        String bookId,
        String title,
        Integer originalPrice,
        Integer salePrice,
        String imageUrl
) {}