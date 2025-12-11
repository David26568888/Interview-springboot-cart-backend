package com.example.demo.cart.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class SecurityConfig {

	private final CaptchaFilter captchaFilter;
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

	public SecurityConfig(CaptchaFilter captchaFilter) {
		this.captchaFilter = captchaFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable()) // 前後端分離 + REST API，先關掉 CSRF
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        // ⭐ 讓 SecurityContextHolder 的變更自動存到 HttpSession
	        .securityContext(context -> context.requireExplicitSave(false))
	        .authorizeHttpRequests(auth -> auth
	            // CORS preflight
	            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
	            // 開放登入/登出/驗證碼/註冊等
	            .requestMatchers("/auth/**", "/captcha").permitAll()

	            // ⭐⭐ 開放「商品列表」給所有人瀏覽（不管有沒有登入）
	            .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
	            
	            // 如果你有後台管理商品（新增/修改/刪除），可以只給 ADMIN 用：
	            .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
	            .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
	            .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

	            // ⭐ 關注相關 API 全部放行，登入檢查在 Controller 裡做
	            .requestMatchers("/favorites/**").permitAll()

	            // ⭐ 結帳相關：先放行，登入檢查在 OrderController 裡做
	            .requestMatchers("/orders/checkout").permitAll()

	            // ⭐ 歷史訂單（登入與否由 Controller 自己看 Session）
	            .requestMatchers(HttpMethod.GET, "/orders/history").permitAll()

	            // ⭐ 銷售統計：只允許 ADMIN
	            .requestMatchers("/orders/sales/**").hasRole("ADMIN")
	            
	            // 只有ADMIN 可以刪除使用者
	            .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

	            // 管理員路徑
	            .requestMatchers("/admin/**").hasRole("ADMIN")

	            // 其他都要登入
	            .anyRequest().authenticated()
	        );

	    // 在 UsernamePasswordAuthenticationFilter 前加入驗證碼 filter
	    http.addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}


	// BCrypt 密碼編碼器
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 全域 CORS 設定
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
