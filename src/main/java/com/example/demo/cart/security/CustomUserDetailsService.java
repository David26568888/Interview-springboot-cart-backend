package com.example.demo.cart.security;

import com.example.demo.cart.model.entity.User;
import com.example.demo.cart.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findFirstByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        // ⭐ 關鍵防呆：roles 可能是 null
        Set<String> roleSet = user.getRoles();
        if (roleSet == null || roleSet.isEmpty()) {
            // 你可以選擇給預設角色，或直接丟錯誤
            roleSet = new HashSet<>();
            roleSet.add("ROLE_USER");  // 給預設一般使用者權限
        }

        String[] roles = roleSet.stream()
                .map(r -> r.replace("ROLE_", "")) // .roles() 會自動加上 ROLE_
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())   // 已經是加密後的
                .roles(roles)
                .disabled(!user.isEnabled())
                .build();
    }
}
