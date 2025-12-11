package com.example.demo.cart.model.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDTO {
    private String username;
    private String password;
    private String name;
    private String idNumber;
    private String phone;
    private LocalDate birthday;
}
