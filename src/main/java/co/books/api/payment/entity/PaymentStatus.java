package co.books.api.payment.entity;

/** 결제 상태. DB에는 VARCHAR(30) 로 저장된다. */
public enum PaymentStatus {
    PAID, FAILED, VIRTUAL_ACCOUNT_ISSUED
}