package com.example.demo.cart.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteProductDTO {
	private Long id;
	private String name;
	private Integer price;
    private String imageBase64; // ← 必須加這個，前端才能顯示圖片
}