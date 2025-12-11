package com.example.demo.cart.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.example.demo.cart.model.entity.User;

import jakarta.transaction.Transactional;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	List<User> findByUsername(String username);//屬於多筆查詢
	
	Optional<User> findFirstByUsername(String username);//屬於單筆查詢
	
	//利用原生sql
	@Query(value = "select * from user where username = :username", nativeQuery=true)
	Optional<User> findByUsernameNative(@Param("username") String username);
	
	//利用原生
	@Modifying
	@Transactional
	@Query(value="update user set password = :password where id = :id",nativeQuery = true)
	int updatePasswordByIdNative(@Param("id") Long id,@Param("password")String password);

	//透過PQL(SQL語法+物件操作)
	//nativeQuery 預設false 利用PQL寫法 如果是True就是用SQL原生
	@Modifying
	@Transactional
	@Query(value = "update User u set u.password = :password where u.id= :id ")
	int updatePasswordById(@Param("id") Long id,@Param("password")String password);
	
}
