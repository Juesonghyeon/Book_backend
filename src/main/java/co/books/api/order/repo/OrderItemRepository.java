package co.books.api.order.repo;

import co.books.api.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 주문 상세 레포지토리. */
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}