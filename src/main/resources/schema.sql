-- =====================================================================
-- 온라인 도서판매 사이트 - 데이터베이스 DDL
-- =====================================================================

-- 기존 객체 삭제 (재실행 대응)
DROP TABLE IF EXISTS reviews         CASCADE;
DROP TABLE IF EXISTS cart_items      CASCADE;
DROP TABLE IF EXISTS order_items     CASCADE;
DROP TABLE IF EXISTS orders          CASCADE;
DROP TABLE IF EXISTS book_categories CASCADE;
DROP TABLE IF EXISTS categories      CASCADE;
DROP TABLE IF EXISTS books           CASCADE;
DROP TABLE IF EXISTS users           CASCADE;
DROP TABLE IF EXISTS students        CASCADE;

DROP TYPE IF EXISTS order_status CASCADE;

-- 주문 상태 ENUM
CREATE TYPE order_status AS ENUM (
    'pending',
    'paid',
    'shipped',
    'delivered',
    'cancelled'
);

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

-- 주문 테이블
CREATE TABLE orders (
    order_id         BIGSERIAL       PRIMARY KEY,
    user_id          VARCHAR(100)    NOT NULL,
    total_amount     INTEGER         NOT NULL CHECK (total_amount >= 0),
    status           order_status    NOT NULL DEFAULT 'pending',
    shipping_address VARCHAR(255)    NOT NULL,
    ordered_at       TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

-- 주문 상세 테이블
CREATE TABLE order_items (
    order_item_id     BIGSERIAL       PRIMARY KEY,
    order_id          BIGINT          NOT NULL,
    book_id           VARCHAR(100)    NOT NULL,
    quantity          INTEGER         NOT NULL CHECK (quantity > 0),
    price_at_purchase INTEGER         NOT NULL CHECK (price_at_purchase >= 0),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id)  REFERENCES books(book_id)   ON DELETE RESTRICT
);

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

-- 학생 테이블 (연습용)
CREATE TABLE students (
    std_id      VARCHAR(255) PRIMARY KEY,
    std_name    VARCHAR(255),
    std_age     INTEGER,
    std_phone   VARCHAR(255),
    std_email   VARCHAR(255),
    description VARCHAR(255)
);