package com.example.demo.cart.service;

import java.util.List;

import com.example.demo.cart.exception.AddException;
import com.example.demo.cart.exception.ProductNotFoundException;
import com.example.demo.cart.model.dto.ProductDTO;
import com.example.demo.cart.model.dto.ProductPageResponse;


public interface ProductService {
	//取得所有商品
	List<ProductDTO> getAllProducts();
	//取得指定商品
	ProductDTO getProductById(Long id) throws ProductNotFoundException;
	
	//新增商品
	ProductDTO saveProduct(ProductDTO productDTO) throws AddException;
	
	//更新商品
	ProductDTO updateProduct(Long id, ProductDTO productDTO)
            throws ProductNotFoundException, AddException;
	
	//移除商品
    void deleteProduct(Long id) throws ProductNotFoundException;
	
  // 支援分頁 + 搜尋
    ProductPageResponse getProducts(String keyword, int page, int size);
}
