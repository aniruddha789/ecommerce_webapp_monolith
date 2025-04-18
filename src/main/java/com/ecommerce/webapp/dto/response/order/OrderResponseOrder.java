package com.ecommerce.webapp.dto.response.order;

import com.ecommerce.webapp.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseOrder {
    private Integer id;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private List<OrdersResponseOrderItem> orderItems;
}
