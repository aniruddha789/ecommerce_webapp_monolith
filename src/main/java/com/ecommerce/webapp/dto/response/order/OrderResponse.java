package com.ecommerce.webapp.dto.response.order;

import com.ecommerce.webapp.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

   private Integer userId;
   private List<OrderResponseOrder> orders;

}
