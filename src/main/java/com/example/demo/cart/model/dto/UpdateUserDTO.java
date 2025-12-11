package com.example.demo.cart.model.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDTO {
    private Long id;           // 要被更新的 user id
    private String name;
    private String phone;
    private LocalDate birthday;
}
