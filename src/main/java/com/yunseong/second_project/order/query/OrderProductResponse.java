package com.yunseong.second_project.order.query;

import com.yunseong.second_project.product.commend.domain.Product;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrderProductResponse {

    private Long id;
    private String productName;
    private int price;

    public OrderProductResponse(Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.price = product.getValue();
    }
}
