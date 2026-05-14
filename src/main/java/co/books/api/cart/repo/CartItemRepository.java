package co.books.api.cart.repo;

import co.books.api.cart.dto.CartListItem;
import co.books.api.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** 장바구니 항목 리포지토리. */
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    Optional<CartItemEntity> findByUserIdAndBookId(String userId, String bookId);

    @Query("""
            SELECT new co.books.api.cart.dto.CartListItem(c.bookId, b.title, b.author, c.quantity, b.originalPrice, b.salePrice, b.imageUrl)
            FROM CartItemEntity c
            JOIN BookEntity b ON c.bookId = b.bookId
            WHERE c.userId = :userId
            ORDER BY c.addedAt DESC
            """)
    List<CartListItem> findCartListByUserId(@Param("userId") String userId);
}