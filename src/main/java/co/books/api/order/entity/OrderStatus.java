package co.books.api.order.entity;

/** 주문 상태. DB에는 VARCHAR(30) 로 저장된다. */
public enum OrderStatus {
    PENDING, PAID, SHIPPED, DELIVERED, CANCELLED, FAILED
}