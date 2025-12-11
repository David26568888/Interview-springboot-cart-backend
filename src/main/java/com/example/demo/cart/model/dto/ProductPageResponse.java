package com.example.demo.cart.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用來包裝「商品分頁結果」
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductPageResponse {

    private List<ProductDTO> products; // 當頁商品
    private int page;                  // 第幾頁（從 0 開始）
    private int size;                  // 每頁大小
    private long totalElements;        // 總筆數
    private int totalPages;            // 總頁數
    private boolean last;              // 是否為最後一頁
}
