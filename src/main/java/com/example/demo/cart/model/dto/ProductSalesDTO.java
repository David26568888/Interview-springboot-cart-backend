package com.example.demo.cart.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用於銷售統計：每個商品的總銷量與總金額
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO {

    private Long productId;
    private String productName;
    private Long totalQty;      // 總數量
    private Long totalAmount;   // 總金額(總數量 * 單價)
}
