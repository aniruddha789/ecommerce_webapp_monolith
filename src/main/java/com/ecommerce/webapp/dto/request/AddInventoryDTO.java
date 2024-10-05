package com.ecommerce.webapp.dto.request;

import lombok.Data;

@Data
public class AddInventoryDTO {

    int productID;
    String size;

    String color;
    int quantity;
    String image;


    public AddInventoryDTO(int productID, String size, String color, int quantity, String image) {
        this.productID = productID;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.image = image;
    }

    public AddInventoryDTO() {
    }

}
