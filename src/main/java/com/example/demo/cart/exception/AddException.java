package com.example.demo.cart.exception;

//自定義錯誤
public class AddException extends RuntimeException{
	public AddException(String message ) {
		super(message);
	}
}
