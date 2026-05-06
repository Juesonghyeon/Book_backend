package co.books.api.book.contrl;

import co.books.api.book.dto.BookListItem;
import co.books.api.book.dto.BooksApiResponse;
import co.books.api.book.dto.BooksDetailData;
import co.books.api.book.dto.BooksListApiResponse;
import co.books.api.book.dto.BooksMainData;
import co.books.api.book.service.BooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;

    /**
     * 도서 리스트 조회.
     * category: web·mobile·basic → slug 일치, new → 신간, bestseller → 베스트셀러
     * keyword: 제목·저자 부분 일치 검색 (생략 시 전체)
     * page: 페이지 번호 (1부터, 기본 1, 한 페이지 10건)
     */
    @GetMapping
    public ResponseEntity<BooksListApiResponse<BookListItem>> getBookList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(booksService.getBookList(category, keyword, page, size));
    }

    /**
     * 메인 페이지 TopN 조회.
     * 베스트셀러 · 신간 · 기본서 각 5건을 반환한다.
     */
    @GetMapping("/topn")
    public ResponseEntity<BooksApiResponse<BooksMainData>> getTopN() {
        return ResponseEntity.ok(BooksApiResponse.ok(booksService.getTopN()));
    }

    /**
     * 도서 상세 페이지 조회.
     * 존재하지 않는 bookId 이면 404 를 반환한다.
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BooksApiResponse<BooksDetailData>> getBookDetail(
            @PathVariable String bookId) {
        return ResponseEntity.ok(BooksApiResponse.ok(booksService.getBookDetail(bookId)));
    }
}