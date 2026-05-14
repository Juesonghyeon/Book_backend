package co.books.api.cart.contrl;

import co.books.api.book.dto.BooksApiResponse;
import co.books.api.cart.dto.CartApiResponse;
import co.books.api.cart.dto.CartListItem;
import co.books.api.cart.dto.CartModifyRequest;
import co.books.api.cart.dto.CartRequest;
import co.books.api.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 장바구니 컨트롤러. */
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /** 장바구니 목록 조회. 로그인한 사용자의 장바구니 항목을 반환한다. */
    @GetMapping
    public ResponseEntity<BooksApiResponse<List<CartListItem>>> getCartList(Authentication authentication) {
        List<CartListItem> list = cartService.getCartList(authentication.getName());
        return ResponseEntity.ok(BooksApiResponse.ok(list));
    }

    /** 장바구니 수량 수정. 프론트에서 bookId 와 변경 수량을 받아 업데이트한다. */
    @PatchMapping
    public ResponseEntity<CartApiResponse> updateCart(
            @RequestBody CartModifyRequest request,
            Authentication authentication) {
        try {
            cartService.updateCartItem(authentication.getName(), request);
            return ResponseEntity.ok(CartApiResponse.updateOk());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(CartApiResponse.updateError());
        }
    }

    /** 장바구니 도서 삭제. */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<CartApiResponse> deleteCart(
            @PathVariable String bookId,
            Authentication authentication) {
        try {
            cartService.deleteCartItem(authentication.getName(), bookId);
            return ResponseEntity.ok(CartApiResponse.deleteOk());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(CartApiResponse.deleteError());
        }
    }

    /**
     * 장바구니 등록.
     * 로그인한 사용자만 접근 가능하며 (SecurityConfig anyRequest().authenticated()),
     * DB 저장 실패 시 500 응답을 반환한다.
     */
    @PostMapping
    public ResponseEntity<CartApiResponse> addCart(
            @RequestBody CartRequest request,
            Authentication authentication) {
        try {
            cartService.addCartItem(authentication.getName(), request);
            return ResponseEntity.ok(CartApiResponse.ok());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(CartApiResponse.error());
        }
    }
}