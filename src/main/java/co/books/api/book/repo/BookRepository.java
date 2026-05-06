package co.books.api.book.repo;

import co.books.api.book.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 도서 리포지토리.
 * books 테이블에 대한 기본 CRUD 와 자주 쓰이는 조회 메서드를 제공한다.
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {

    /** ISBN 으로 도서 단건 조회 */
    Optional<BookEntity> findByIsbn(String isbn);

    /** 베스트셀러 여부로 도서 목록 조회 ('Y' / 'N') */
    List<BookEntity> findByIsBestseller(String isBestseller);

    /** 신간 여부로 도서 목록 조회 ('Y' / 'N') */
    List<BookEntity> findByIsNew(String isNew);

    /** 제목에 키워드가 포함된 도서 검색 */
    List<BookEntity> findByTitleContaining(String keyword);

    /** 베스트셀러 상위 N 건 (출간일 최신순, 동일 시 제목순) */
    List<BookEntity> findTop5ByIsBestsellerOrderByPublishDateDescTitleAsc(String isBestseller);

    /** 신간 상위 N 건 (출간일 최신순, 동일 시 제목순) */
    List<BookEntity> findTop5ByIsNewOrderByPublishDateDescTitleAsc(String isNew);

    /** 특정 카테고리의 도서 목록 (출간일 최신순 NULLS LAST, 동일 시 제목순) */
    @Query("SELECT b FROM BookEntity b JOIN b.bookCategories bc " +
           "WHERE bc.category.categoryId = :categoryId " +
           "ORDER BY b.publishDate DESC NULLS LAST, b.title ASC")
    List<BookEntity> findByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);

    /**
     * 카테고리 slug 기반 도서 목록 페이징 조회 (제목·저자 검색 포함).
     * keyword 가 빈 문자열이면 전체 조회, 값이 있으면 제목·저자 부분 일치 필터 적용.
     */
    @Query(value = "SELECT b FROM BookEntity b JOIN b.bookCategories bc " +
                   "WHERE bc.category.slug = :slug " +
                   "AND (:keyword = '' OR b.title LIKE CONCAT('%', :keyword, '%') " +
                   "     OR b.author LIKE CONCAT('%', :keyword, '%')) " +
                   "ORDER BY b.publishDate DESC NULLS LAST, b.title ASC",
           countQuery = "SELECT COUNT(b) FROM BookEntity b JOIN b.bookCategories bc " +
                        "WHERE bc.category.slug = :slug " +
                        "AND (:keyword = '' OR b.title LIKE CONCAT('%', :keyword, '%') " +
                        "     OR b.author LIKE CONCAT('%', :keyword, '%'))")
    Page<BookEntity> findBySlugWithFilter(
            @Param("slug") String slug,
            @Param("keyword") String keyword,
            Pageable pageable);

    /** 신간(is_new = Y) 도서 목록 페이징 조회 (제목·저자 검색 포함) */
    @Query(value = "SELECT b FROM BookEntity b " +
                   "WHERE b.isNew = 'Y' " +
                   "AND (:keyword = '' OR b.title LIKE CONCAT('%', :keyword, '%') " +
                   "     OR b.author LIKE CONCAT('%', :keyword, '%')) " +
                   "ORDER BY b.publishDate DESC NULLS LAST, b.title ASC",
           countQuery = "SELECT COUNT(b) FROM BookEntity b " +
                        "WHERE b.isNew = 'Y' " +
                        "AND (:keyword = '' OR b.title LIKE CONCAT('%', :keyword, '%') " +
                        "     OR b.author LIKE CONCAT('%', :keyword, '%'))")
    Page<BookEntity> findNewBooksWithFilter(
            @Param("keyword") String keyword,
            Pageable pageable);

    /** 베스트셀러(is_bestseller = Y) 도서 목록 페이징 조회 (제목·저자 검색 포함) */
    @Query(value = "SELECT b FROM BookEntity b " +
                   "WHERE b.isBestseller = 'Y' " +
                   "AND (:keyword = '' OR b.title LIKE CONCAT('%', :keyword, '%') " +
                   "     OR b.author LIKE CONCAT('%', :keyword, '%')) " +
                   "ORDER BY b.publishDate DESC NULLS LAST, b.title ASC",
           countQuery = "SELECT COUNT(b) FROM BookEntity b " +
                        "WHERE b.isBestseller = 'Y' " +
                        "AND (:keyword = '' OR b.title LIKE CONCAT('%', :keyword, '%') " +
                        "     OR b.author LIKE CONCAT('%', :keyword, '%'))")
    Page<BookEntity> findBestsellerBooksWithFilter(
            @Param("keyword") String keyword,
            Pageable pageable);
}
