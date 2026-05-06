# book 책 한권 상세정보 api

## 1. Book API
- 관련 클래스 만들 때 Books - 접두사 사용
- 기본 end-point : /api/v1/books

## 2. 기능 정의

### 2.1 메인 페이지 리스트
- method : GET
- end-point : /api/v1/books/{bookId}
- 매개변수 : bookId(PathVariable 로 처리)
- 데이터는 bookId에 해당하는 책의 정보
- 필요 데이터 : 아이디, 제목, 부제목, 저자, 출판사, 출간일, 원가격, 할인가격, 책설명, 리뷰리스트
- 결과 데이터 :
    ```
   {
      code : 200,
      data  : {
        "bookId" : "",
        "title" : "",
        "subtitle" : "",
        "author" : "",
        "publisher" "",
        "publisher"
          "publishDate" : "",
        "originalPrice",
        "salePrice" : "",
        "description" : "",
        "stock" : "",
        "reviewList" : []
  
      }
   }
    ```


## 3. 보안 설정
- 로그인 안해도 접근가능

