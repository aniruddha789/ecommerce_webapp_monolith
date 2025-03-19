package com.ecommerce.webapp.dto.request.order;

import com.ecommerce.webapp.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubmitOrderRequest {

    private String username;;
    private List<SubmitOrderItem> items;

}
