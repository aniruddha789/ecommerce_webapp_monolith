package com.ecommerce.webapp.dto.request;

public class AddInventoryDTO {

    int productID;
    String size;
    int quantity;

    public AddInventoryDTO(int productID, String size, int quantity) {
        this.productID = productID;
        this.size = size;
        this.quantity = quantity;
    }

    public AddInventoryDTO() {
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
