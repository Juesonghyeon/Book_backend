package co.books.api.payment.repo;

import co.books.api.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 결제 레포지토리. */
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
}