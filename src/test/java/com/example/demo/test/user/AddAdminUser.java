package com.example.demo.test.user;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.cart.model.entity.User;
import com.example.demo.cart.repository.UserRepository;

@SpringBootTest
public class AddAdminUser {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void addAdminUser() {
        // 想要創建的 admin 帳號資料
        String username = "admin";
        String rawPassword = "1234";

        // 先看 DB 裡是不是已經有這個帳號，避免重複
        if (userRepository.findFirstByUsername(username).isPresent()) {
            System.out.println("帳號 " + username + " 已存在，略過新增");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword)); // 使用 BCrypt 加密
        user.setEnabled(true);

        // 基本資料（隨便給一組測試用）
        user.setName("系統管理員");
        user.setIdNumber("A123456789");
        user.setPhone("0912345678");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // 指派角色：同時有 ROLE_ADMIN & ROLE_USER
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        user.setRoles(roles);

        // 儲存
        user = userRepository.save(user);
        System.out.println("管理者建立完成，id = " + user.getId());
        System.out.println("username = " + user.getUsername());
        System.out.println("roles    = " + user.getRoles());
    }
}
