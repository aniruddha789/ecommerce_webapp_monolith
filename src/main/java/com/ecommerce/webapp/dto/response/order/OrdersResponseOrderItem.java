package com.ecommerce.webapp.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersResponseOrderItem {

    private int id;
    private Integer productId;
    private String name;
    private String size;
    private String color;
    private int quantity;
}
