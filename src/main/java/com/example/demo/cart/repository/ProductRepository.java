
package com.example.demo.cart.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.cart.model.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	// 分頁 + 模糊搜尋（依商品名稱）
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
