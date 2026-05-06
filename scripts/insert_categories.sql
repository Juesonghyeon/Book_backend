-- =====================================================
-- categories (카테고리) INSERT
-- category_id 는 SERIAL 이지만 명시적으로 지정해
-- insert_book_categories.sql 의 FK 값과 일치시킨다.
-- =====================================================

INSERT INTO categories (category_id, name, slug, display_order) VALUES
(1, '기본서',       'fundamentals', 1),
(2, '모바일',       'mobile',       2),
(3, '웹프로그래밍', 'web',          3);

-- SERIAL 시퀀스를 실제 삽입된 최댓값에 맞게 보정
SELECT setval('categories_category_id_seq', (SELECT MAX(category_id) FROM categories));