package co.books.api.payment.entity;

/** 결제 수단. DB에는 VARCHAR(30) 로 저장된다. */
public enum PayMethod {
    CARD, VIRTUAL_ACCOUNT, TRANSFER, MOBILE, EASY_PAY
}