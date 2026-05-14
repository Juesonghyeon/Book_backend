package co.books.api.cart.service;

import co.books.api.cart.dto.CartListItem;
import co.books.api.cart.dto.CartModifyRequest;
import co.books.api.cart.dto.CartRequest;
import co.books.api.cart.entity.CartItemEntity;
import co.books.api.cart.repo.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 장바구니 서비스. */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;

    /** 로그인한 사용자의 장바구니 목록을 반환한다. */
    @Transactional(readOnly = true)
    public List<CartListItem> getCartList(String userId) {
        return cartItemRepository.findCartListByUserId(userId);
    }

    /**
     * 장바구니에 담긴 도서 수량을 변경한다.
     * 해당 항목이 없으면 예외를 던진다.
     */
    @Transactional
    public void updateCartItem(String userId, CartModifyRequest request) {
        CartItemEntity entity = cartItemRepository
                .findByUserIdAndBookId(userId, request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 없는 도서입니다: " + request.getBookId()));
        entity.setQuantity(request.getQuantity());
    }

    /**
     * 장바구니에서 도서를 삭제한다.
     * 해당 항목이 없으면 예외를 던진다.
     */
    @Transactional
    public void deleteCartItem(String userId, String bookId) {
        CartItemEntity entity = cartItemRepository
                .findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 없는 도서입니다: " + bookId));
        cartItemRepository.delete(entity);
    }

    /**
     * 로그인한 사용자의 장바구니에 도서를 등록한다.
     * 이미 담긴 도서이면 수량만 증가시킨다.
     */
    @Transactional
    public void addCartItem(String userId, CartRequest request) {
        cartItemRepository.findByUserIdAndBookId(userId, request.getBookId())
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + request.getQuantity()),
                        () -> {
                            CartItemEntity entity = new CartItemEntity();
                            entity.setUserId(userId);
                            entity.setBookId(request.getBookId());
                            entity.setQuantity(request.getQuantity());
                            cartItemRepository.save(entity);
                        }
                );
    }
}