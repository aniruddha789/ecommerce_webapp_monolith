package com.ecommerce.webapp.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubmitOrderItem {

    private int id; /** Order Item ID*/
    private int quantity;
    private String size;
    private String color;

}
