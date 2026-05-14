package co.books.api.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

/** 주문 생성 요청 DTO. */
@Getter
public class CreateOrderRequest {

    @NotBlank
    private String receiver;

    private String phone;

    @NotBlank
    private String shippingAddress;

    private String shippingDetailAddress;

    @Min(0)
    private Integer usedPoints = 0;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;
}