package co.books.api.order.service;

import co.books.api.book.entity.BookEntity;
import co.books.api.book.repo.BookRepository;
import co.books.api.common.exception.NotFoundException;
import co.books.api.order.dto.CreateOrderRequest;
import co.books.api.order.dto.CreateOrderResponse;
import co.books.api.order.dto.OrderItemRequest;
import co.books.api.order.entity.OrderEntity;
import co.books.api.order.entity.OrderItemEntity;
import co.books.api.order.entity.OrderStatus;
import co.books.api.order.repo.OrderItemRepository;
import co.books.api.order.repo.OrderRepository;
import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** 주문 서비스. */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 주문을 생성한다.
     * 금액은 books 테이블 기준으로 서버에서 재계산하며, 포인트 차감을 트랜잭션으로 묶는다.
     */
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        int usedPoints = request.getUsedPoints() != null ? request.getUsedPoints() : 0;
        if (user.getPoints() < usedPoints) {
            throw new IllegalArgumentException("포인트 잔액이 부족합니다.");
        }

        List<String> bookIds = request.getItems().stream()
                .map(OrderItemRequest::getBookId)
                .toList();

        List<BookEntity> books = bookRepository.findAllById(bookIds);
        if (books.size() != bookIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 도서가 포함되어 있습니다.");
        }

        Map<String, BookEntity> bookMap = books.stream()
                .collect(Collectors.toMap(BookEntity::getBookId, b -> b));

        int productTotal = request.getItems().stream()
                .mapToInt(item -> item.getQuantity() * bookMap.get(item.getBookId()).getSalePrice())
                .sum();
        int totalAmount = productTotal - usedPoints;

        String orderId = generateOrderId();
        String orderName = buildOrderName(request.getItems(), bookMap);

        OrderEntity order = new OrderEntity();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setReceiver(request.getReceiver());
        order.setPhone(request.getPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingDetailAddress(request.getShippingDetailAddress());
        order.setTotalAmount(totalAmount);
        order.setUsedPoints(usedPoints);
        order.setOrderName(orderName);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderedAt(java.time.OffsetDateTime.now());
        orderRepository.save(order);

        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(orderId);
            item.setBookId(itemReq.getBookId());
            item.setQuantity(itemReq.getQuantity());
            item.setPriceAtPurchase(bookMap.get(itemReq.getBookId()).getSalePrice());
            orderItemRepository.save(item);
        }

        if (usedPoints > 0) {
            user.setPoints(user.getPoints() - usedPoints);
        }

        return new CreateOrderResponse(orderId, totalAmount, orderName);
    }

    private String generateOrderId() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ord_" + date + "_" + uuid;
    }

    private String buildOrderName(List<OrderItemRequest> items, Map<String, BookEntity> bookMap) {
        String firstTitle = bookMap.get(items.get(0).getBookId()).getTitle();
        if (items.size() == 1) {
            return firstTitle;
        }
        return firstTitle + " 외 " + (items.size() - 1) + "건";
    }
}