package com.example.demo.test.favorite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.cart.model.entity.Product;
import com.example.demo.cart.model.entity.User;
import com.example.demo.cart.repository.ProductRepository;
import com.example.demo.cart.repository.UserRepository;

@SpringBootTest
public class AddFavorite {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Test
	void add() {
		//所有商品
		Product apple = productRepository.findById(1L).get();
		Product banana = productRepository.findById(2L).get();
		
		
		//使用者
		User DAVID =userRepository.findById(1L).get();
		User max =userRepository.findById(2L).get();
		
		//加入關注
		DAVID.getFavoriteProducts().add(apple);
		DAVID.getFavoriteProducts().add(banana);
		
		max.getFavoriteProducts().add(banana);
		
		//保存
		userRepository.save(DAVID);
		userRepository.save(max);
		
		System.out.println("關注成功");
	}
}
