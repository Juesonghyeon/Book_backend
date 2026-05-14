## 1. 기능
- 장바구니 등록 API 만들기

## 2. API 구성
- end-point : /api/v1.carts
- method : GET
- 파라미터 양식 : 없음
  - 파라미터 :
      ```
      {
          "code" : 200,
          "data" : [
              {
                  "bookId" : "BOOK001",
                  "title" : "클린코드",
                  "author" : "TOM",
                  "quantity" : 1,
                  "originalPrice" : 30000,
                  "salePrice" : 27000
              }
          ]
      }
      ```
## 3. 기능 설명
- 사용자가 등록된 장바구니 리스트 출력
- 비로그인 시 접근 금지

## 4. 기능
- 필요한 controller, service, entity, repository 생성
- 불필요한 코드 수정 금지
