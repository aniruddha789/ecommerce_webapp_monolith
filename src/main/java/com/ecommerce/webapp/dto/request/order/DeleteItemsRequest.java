package com.ecommerce.webapp.dto.request.order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteItemsRequest {

    private String username;
    private List<Integer> itemIds;
}
