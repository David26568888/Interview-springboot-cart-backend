
package com.example.demo.cart.service;

import java.util.List;

import com.example.demo.cart.exception.OrderItemEmptyException;
import com.example.demo.cart.exception.UserNotFoundException;
import com.example.demo.cart.model.dto.OrderDTO;
import com.example.demo.cart.model.dto.OrderItemDTO;
import com.example.demo.cart.model.dto.ProductSalesDTO;

public interface OrderService {
	// 根據用戶 id 取得訂單資料
	List<OrderDTO> findOrdersByUserId(Long userId) throws UserNotFoundException;
	
	// 新增訂單
	OrderDTO saveOrder(Long userId, List<OrderItemDTO> orderItems) throws UserNotFoundException, OrderItemEmptyException;
	
	//取得某使用者的歷史訂單
    List<OrderDTO> getUserOrderHistory(Long userId);
    
    //取得商品銷售統計
    List<ProductSalesDTO> getProductSalesSummary();
}
