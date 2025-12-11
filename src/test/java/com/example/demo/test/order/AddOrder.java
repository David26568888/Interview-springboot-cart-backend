package com.example.demo.test.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.cart.model.entity.Order;
import com.example.demo.cart.model.entity.OrderItem;
import com.example.demo.cart.model.entity.Product;
import com.example.demo.cart.model.entity.User;
import com.example.demo.cart.repository.OrderItemRepository;
import com.example.demo.cart.repository.OrderRepository;
import com.example.demo.cart.repository.ProductRepository;
import com.example.demo.cart.repository.UserRepository;

@SpringBootTest
public class AddOrder {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderItemRepository orderItemRepository;
	
	@Test
	public void add() {
		//user id=1 建立了一筆訂單， 購買了 product id = 1 , qty=5 +product id =2 qty=10
		//1.找到user id =1的資料
		User user = userRepository.findById(2L).get();
		
		//2. 建立訂單-主檔
		Order order = new Order();
		order.setUser(user);//建立關聯
		
		// 3.儲存訂單主檔
		orderRepository.save(order);
	
//---------------------------------------------------------------------		
		
		//4. 找到要買的商品
		Product apple = productRepository.findById(1L).get();
		Product banana = productRepository.findById(2L).get();
		
		// 建立訂單-項目
		OrderItem item1 = new OrderItem();
		item1.setProduct(apple);
		item1.setQty(5);
		item1.setOrder(order); // 建立關聯
		
		OrderItem item2 = new OrderItem();
		item2.setProduct(banana);
		item2.setQty(99);
		item2.setOrder(order); // 建立關聯
		
		// 儲存訂單項目
		orderItemRepository.save(item1);
		orderItemRepository.save(item2);

		System.out.println("order add OK !");
		
	}
	
}