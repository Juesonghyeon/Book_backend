package co.books.api.review.repo;

import co.books.api.review.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 리뷰 리포지터리.
 * reviews 테이블에 대한 기본 CRUD 와 도서별 리뷰 조회 메서드를 제공한다.
 */
@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    /**
     * 도서 ID 로 리뷰 목록 조회 (최신순).
     * user 를 JOIN FETCH 해서 N+1 문제를 방지한다.
     */
    @Query("SELECT r FROM ReviewEntity r JOIN FETCH r.user " +
           "WHERE r.book.bookId = :bookId " +
           "ORDER BY r.createdAt DESC")
    List<ReviewEntity> findByBookIdWithUser(@Param("bookId") String bookId);
}