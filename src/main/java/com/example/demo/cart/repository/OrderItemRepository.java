package com.example.demo.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.cart.model.dto.ProductSalesDTO;
import com.example.demo.cart.model.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

	 /**
     * 統計每個商品的總銷量與總金額
     */
    @Query("""
           SELECT new com.example.demo.cart.model.dto.ProductSalesDTO(
               oi.product.id,
               oi.product.name,
               SUM(oi.qty),
               SUM(oi.qty * oi.product.price)
           )
           FROM OrderItem oi
           GROUP BY oi.product.id, oi.product.name
           ORDER BY SUM(oi.qty) DESC
           """)
    List<ProductSalesDTO> findProductSalesSummary();
}
