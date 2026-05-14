## 1. 기능
- 장바구니 등록 API 만들기

## 2. API 구성
- end-point : /api/v1.carts
- method : POST
- 파라미터 양식 : application/json
- 파라미터 : 
    ```
    {
        bookId : BOOK001,
        quantity : 1
    }
    ```
- 결과 :
  - 성공 시 : { "code" : 200, "message" : "장바구니 등록이 되었습니다."}
  - 실패 시 : { "code" : 500, "message" : "장바구니 등록이 실패되었습니다."}
## 3. 기능 설명
- 로그인한 사용자가 장바구니 등록을 요청하면 DB에 등록 후 결과 리턴
- 비 로그인 상태에서 접근 불가
- 이미 장바구니에 담긴 도서(동일 user_id + book_id)가 있을 경우 새로 삽입하지 않고 수량(quantity)만 증가

## 4. 기능
- 필요한 controller, service, entity, repository 생성
- 불필요한 코드 수정 금지
- script/books_schema.sql 파일을 참고