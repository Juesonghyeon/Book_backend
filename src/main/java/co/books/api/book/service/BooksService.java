package co.books.api.book.service;

import co.books.api.book.dto.BookListItem;
import co.books.api.book.dto.BooksDetailData;
import co.books.api.book.dto.BooksListApiResponse;
import co.books.api.book.dto.BooksMainData;
import co.books.api.book.dto.BooksReviewItem;
import co.books.api.book.dto.BooksTopNItem;
import co.books.api.book.dto.PageInfo;
import co.books.api.book.entity.BookEntity;
import co.books.api.book.repo.BookRepository;
import co.books.api.review.repo.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BooksService {

    private static final int TOP_N = 5;
    private static final int BASIC_CATEGORY_ID = 1;

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 메인 페이지용 TopN 데이터 반환.
     * 베스트셀러 · 신간 · 기본서 각 5건 (출간일 최신순, 동일 시 제목순).
     */
    public BooksMainData getTopN() {
        List<BooksTopNItem> bestTopN = bookRepository
                .findTop5ByIsBestsellerOrderByPublishDateDescTitleAsc("Y")
                .stream().map(this::toTopNItem).toList();

        List<BooksTopNItem> newTopN = bookRepository
                .findTop5ByIsNewOrderByPublishDateDescTitleAsc("Y")
                .stream().map(this::toTopNItem).toList();

        List<BooksTopNItem> basicTopN = bookRepository
                .findByCategoryId(BASIC_CATEGORY_ID, PageRequest.of(0, TOP_N))
                .stream().map(this::toTopNItem).toList();

        return new BooksMainData(bestTopN, newTopN, basicTopN);
    }

    /**
     * 도서 상세 페이지 데이터 반환.
     * 존재하지 않는 bookId 이면 404 를 반환한다.
     */
    public BooksDetailData getBookDetail(String bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 도서입니다: " + bookId));

        List<BooksReviewItem> reviewList = reviewRepository
                .findByBookIdWithUser(bookId)
                .stream()
                .map(r -> new BooksReviewItem(
                        r.getReviewId(),
                        r.getUser().getUserId(),
                        r.getUser().getName(),
                        r.getRating(),
                        r.getContent(),
                        r.getCreatedAt()
                ))
                .toList();

        return new BooksDetailData(
                book.getBookId(),
                book.getTitle(),
                book.getSubtitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishDate(),
                book.getOriginalPrice(),
                book.getSalePrice(),
                book.getDescription(),
                book.getImageUrl(),
                book.getContents(),
                book.getStock(),
                reviewList
        );
    }

    /**
     * 도서 리스트 페이징 조회.
     * category 값에 따라 신간/베스트셀러/카테고리 slug 로 필터링하며,
     * keyword 가 있으면 제목·저자 부분 일치 검색을 추가로 적용한다.
     */
    public BooksListApiResponse<BookListItem> getBookList(String category, String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1));
        String kw = (keyword != null && !keyword.isBlank()) ? keyword : "";

        Page<BookEntity> result;
        if ("new".equals(category)) {
            result = bookRepository.findNewBooksWithFilter(kw, pageable);
        } else if ("bestseller".equals(category)) {
            result = bookRepository.findBestsellerBooksWithFilter(kw, pageable);
        } else {
            result = bookRepository.findBySlugWithFilter(category, kw, pageable);
        }

        List<BookListItem> items = result.getContent().stream()
                .map(b -> new BookListItem(
                        b.getBookId(),
                        b.getTitle(),
                        b.getSubtitle(),
                        b.getAuthor(),
                        b.getSalePrice(),
                        b.getPublishDate(),
                        b.getImageUrl()
                ))
                .toList();

        return BooksListApiResponse.ok(items, new PageInfo(page, result.getTotalElements()));
    }

    private BooksTopNItem toTopNItem(BookEntity book) {
        return new BooksTopNItem(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getSalePrice(),
                book.getImageUrl()
        );
    }
}