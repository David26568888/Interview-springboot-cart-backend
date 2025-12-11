package com.example.demo.cart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.cart.exception.OrderItemEmptyException;
import com.example.demo.cart.exception.UserNotFoundException;
import com.example.demo.cart.model.dto.OrderDTO;
import com.example.demo.cart.model.dto.OrderItemDTO;
import com.example.demo.cart.model.dto.ProductSalesDTO;
import com.example.demo.cart.model.dto.UserDTO;
import com.example.demo.cart.response.ApiResponse;
import com.example.demo.cart.service.OrderService;

import jakarta.servlet.http.HttpSession;

/**
 * OrderController:
 * Request Mapping: "/orders"
 * ---------------------------------------------------------------------------------------------------
 * GET  "", "/"      查詢該用戶(已登入)的訂單 範例: "/orders" 或 "/orders/"
 * POST "/checkout"  該用戶(已登入)進行結帳  範例: "/checkout"
 * GET "/history"	取得目前登入使用者的歷史訂單
 * GET "/sales/summary" 取得商品銷售統計（給管理人查看）
 * */
@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping(value = {"", "/"})
	public ApiResponse<List<OrderDTO>> getAllOrders(HttpSession httpSession) {
		// 是否有登入資訊
		if(httpSession.getAttribute("userDTO") == null) {
			return new ApiResponse<>(400, "無登入資料", null);
		}
		
		UserDTO userDTO = (UserDTO)httpSession.getAttribute("userDTO");
		List<OrderDTO> orderDTOs;
		try {
			orderDTOs = orderService.findOrdersByUserId(userDTO.getId());
		} catch (UserNotFoundException e) {
			return new ApiResponse<>(400, "查無使用者", null);
		}
		return new ApiResponse<>(200, "查詢成功", orderDTOs);
	}
	
	@PostMapping("/checkout")
	public ApiResponse<OrderDTO> checkout(@RequestBody List<OrderItemDTO> orderItems, HttpSession httpSession) {
		// 是否有登入資訊
		if(httpSession.getAttribute("userDTO") == null) {
			return new ApiResponse<>(400, "無登入資料, 請先登入", null);
		}
		
		UserDTO userDTO = (UserDTO)httpSession.getAttribute("userDTO");
		OrderDTO orderDTO = null;
		try {
			orderDTO = orderService.saveOrder(userDTO.getId(), orderItems);
		} catch (UserNotFoundException e1) {
			return new ApiResponse<>(400, e1.getMessage(), null);
		} catch (OrderItemEmptyException e2) {
			return new ApiResponse<>(400, e2.getMessage(), null);
		}
		
		return new ApiResponse<>(200, "結帳成功", orderDTO);
	}
	
	 // 取得目前登入使用者的歷史訂單
    @GetMapping("/history")
    public ApiResponse<List<OrderDTO>> getOrderHistory(HttpSession session) {

        UserDTO userDTO = (UserDTO) session.getAttribute("userDTO");

        if (userDTO == null) {
            // 和其它 controller 一樣，用 400 / 401 自行判斷未登入
            return new ApiResponse<>(401, "尚未登入，無法查詢歷史訂單", null);
        }

        Long userId = userDTO.getId();
        List<OrderDTO> orders = orderService.getUserOrderHistory(userId);

        return new ApiResponse<>(200, "查詢歷史訂單成功", orders);
    }
    
 // 取得商品銷售統計（給管理人查看）
    @GetMapping("/sales/summary")
    public ApiResponse<List<ProductSalesDTO>> getSalesSummary(HttpSession session) {

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("現在請求的使用者 Authentication: " + auth);
    	 
        UserDTO userDTO = (UserDTO) session.getAttribute("userDTO");
        if (userDTO == null) {
            return new ApiResponse<>(401, "尚未登入，無法查看銷售統計", null);
        }

        // 簡單檢查是否有 ROLE_ADMIN（你的 User.roles 是 Set<String>）
        if (userDTO.getRoles() == null ||
            userDTO.getRoles().stream().noneMatch(r -> r.equals("ROLE_ADMIN"))) {
            return new ApiResponse<>(403, "只有管理人可以查看銷售統計", null);
        }

        List<ProductSalesDTO> list = orderService.getProductSalesSummary();
        return new ApiResponse<>(200, "取得商品銷售統計成功", list);
    }
}