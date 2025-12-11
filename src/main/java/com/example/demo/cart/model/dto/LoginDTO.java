package com.example.demo.cart.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDTO {
	private String username;
	private String password;
	private String captcha;    // 🔸 新增：前端填的驗證碼
	private Boolean isLoggedIn; //是否登入成功
}
