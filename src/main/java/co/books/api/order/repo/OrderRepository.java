package co.books.api.order.repo;

import co.books.api.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 주문 레포지토리. */
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}