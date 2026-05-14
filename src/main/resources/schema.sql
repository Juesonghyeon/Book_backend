-- =====================================================================
-- 온라인 도서판매 사이트 - 데이터베이스 DDL
-- =====================================================================

-- 기존 객체 삭제 (재실행 대응)
DROP TABLE IF EXISTS reviews             CASCADE;
DROP TABLE IF EXISTS cart_items          CASCADE;
DROP TABLE IF EXISTS order_items         CASCADE;
DROP TABLE IF EXISTS payments            CASCADE;
DROP TABLE IF EXISTS orders              CASCADE;
DROP TABLE IF EXISTS book_categories     CASCADE;
DROP TABLE IF EXISTS categories          CASCADE;
DROP TABLE IF EXISTS books               CASCADE;
DROP TABLE IF EXISTS users               CASCADE;
DROP TABLE IF EXISTS students            CASCADE;

DROP TYPE IF EXISTS order_status         CASCADE;

-- 도서 테이블
CREATE TABLE books (
    book_id         VARCHAR(100)    PRIMARY KEY,
    title           VARCHAR(200)    NOT NULL,
    subtitle        VARCHAR(200),
    author          VARCHAR(100)    NOT NULL,
    publisher       VARCHAR(100),
    isbn            VARCHAR(20)     UNIQUE,
    description     TEXT,
    image_url       VARCHAR(500),
    contents        VARCHAR(100),
    original_price  INTEGER         NOT NULL CHECK (original_price >= 0),
    sale_price      INTEGER         NOT NULL CHECK (sale_price >= 0),
    stock           INTEGER         NOT NULL DEFAULT 0 CHECK (stock >= 0),
    page_count      INTEGER         CHECK (page_count >= 0),
    publish_date    DATE,
    is_bestseller   VARCHAR(1)      NOT NULL DEFAULT 'N',
    is_new          VARCHAR(1)      NOT NULL DEFAULT 'N',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_books_bestseller   ON books(is_bestseller) WHERE is_bestseller = 'Y';
CREATE INDEX idx_books_new          ON books(is_new)        WHERE is_new = 'Y';
CREATE INDEX idx_books_publish_date ON books(publish_date DESC);
CREATE INDEX idx_books_title        ON books(title);

-- 카테고리 테이블
CREATE TABLE categories (
    category_id     SERIAL          PRIMARY KEY,
    name            VARCHAR(50)     NOT NULL UNIQUE,
    slug            VARCHAR(50)     NOT NULL UNIQUE,
    display_order   INTEGER         NOT NULL DEFAULT 0
);

-- 도서-카테고리 조인 테이블 (N:M)
CREATE TABLE book_categories (
    book_id         VARCHAR(100)    NOT NULL,
    category_id     INTEGER         NOT NULL,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id)     REFERENCES books(book_id)           ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)  ON DELETE CASCADE
);

CREATE INDEX idx_book_categories_category ON book_categories(category_id);

-- 회원 테이블
CREATE TABLE users (
    user_id         VARCHAR(100)    PRIMARY KEY,
    email           VARCHAR(100)    NOT NULL UNIQUE,
    passwd          VARCHAR(255)    NOT NULL,
    name            VARCHAR(50)     NOT NULL,
    phone           VARCHAR(20),
    gender          VARCHAR(20),
    birth_date      DATE            CHECK (birth_date <= CURRENT_DATE),
    postal_code     VARCHAR(10),
    address         VARCHAR(255),
    address_detail  VARCHAR(255),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 주문 테이블 (order_id: 앱에서 생성하는 문자열 PK, status: VARCHAR)
CREATE TABLE orders (
    order_id                VARCHAR(100)    PRIMARY KEY,
    user_id                 VARCHAR(100)    NOT NULL,
    receiver                VARCHAR(100)    NOT NULL,
    phone                   VARCHAR(20),
    shipping_address        VARCHAR(255)    NOT NULL,
    shipping_detail_address VARCHAR(255),
    total_amount            INTEGER         NOT NULL CHECK (total_amount >= 0),
    used_points             INTEGER         NOT NULL DEFAULT 0,
    order_name              VARCHAR(200)    NOT NULL,
    status                  VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    ordered_at              TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX idx_orders_user   ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

-- 주문 상세 테이블
CREATE TABLE order_items (
    order_item_id     BIGSERIAL       PRIMARY KEY,
    order_id          VARCHAR(100)    NOT NULL,
    book_id           VARCHAR(100)    NOT NULL,
    quantity          INTEGER         NOT NULL CHECK (quantity > 0),
    price_at_purchase INTEGER         NOT NULL CHECK (price_at_purchase >= 0),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id)  REFERENCES books(book_id)   ON DELETE RESTRICT
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_book  ON order_items(book_id);

-- 장바구니 테이블
CREATE TABLE cart_items (
    cart_item_id    BIGSERIAL       PRIMARY KEY,
    user_id         VARCHAR(100)    NOT NULL,
    book_id         VARCHAR(100)    NOT NULL,
    quantity        INTEGER         NOT NULL DEFAULT 1 CHECK (quantity > 0),
    added_at        TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE (user_id, book_id)
);

CREATE INDEX idx_cart_items_user ON cart_items(user_id);

-- 리뷰 테이블
CREATE TABLE reviews (
    review_id   BIGSERIAL       PRIMARY KEY,
    user_id     VARCHAR(100)    NOT NULL,
    book_id     VARCHAR(100)    NOT NULL,
    rating      SMALLINT        NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content     TEXT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE (user_id, book_id)
);

CREATE INDEX idx_reviews_book ON reviews(book_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);

-- 결제 테이블 (status/pay_method: VARCHAR, raw_response: TEXT)
CREATE TABLE payments (
    payment_id    VARCHAR(100)    PRIMARY KEY,
    order_id      VARCHAR(100)    NOT NULL REFERENCES orders(order_id),
    user_id       VARCHAR(100)    NOT NULL REFERENCES users(user_id),
    tx_id         VARCHAR(100),
    channel_key   VARCHAR(100)    NOT NULL,
    pg_provider   VARCHAR(50),
    status        VARCHAR(30)     NOT NULL,
    pay_method    VARCHAR(30),
    total_amount  INTEGER         NOT NULL CHECK (total_amount >= 0),
    currency      VARCHAR(10)     NOT NULL DEFAULT 'KRW',
    order_name    VARCHAR(200)    NOT NULL,
    fail_reason   TEXT,
    fail_code     VARCHAR(50),
    raw_response  TEXT,
    paid_at       TIMESTAMPTZ,
    created_at    TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_user  ON payments(user_id);

-- 학생 테이블 (연습용)
CREATE TABLE students (
    std_id      VARCHAR(255) PRIMARY KEY,
    std_name    VARCHAR(255),
    std_age     INTEGER,
    std_phone   VARCHAR(255),
    std_email   VARCHAR(255),
    description VARCHAR(255)
);