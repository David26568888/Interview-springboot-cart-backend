package com.example.demo.cart.model.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
	private Long id;
	private String username;
	private String password;
	private boolean enabled;
	private Set<String> roles; // ← 必須是 Set<String>

	private String name;
	private String idNumber;
	private String phone;
	private LocalDate birthday;

}
