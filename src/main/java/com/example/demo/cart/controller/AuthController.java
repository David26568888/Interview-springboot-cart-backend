
package com.example.demo.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.cart.exception.AddException;
import com.example.demo.cart.exception.LoginException;
import com.example.demo.cart.exception.UserNotFoundException;
import com.example.demo.cart.model.dto.LoginDTO;
import com.example.demo.cart.model.dto.RegisterDTO;
import com.example.demo.cart.model.dto.ResetPasswordDTO;
import com.example.demo.cart.model.dto.UserDTO;
import com.example.demo.cart.response.ApiResponse;
import com.example.demo.cart.service.UserService;

import jakarta.servlet.http.HttpSession;

/**
 * AuthController:
 * Request Mapping: "/auth"
 * ---------------------------------------------------------------------------------------------------
 * POST	"/register"	 		註冊			範例:"/auth/register"
 * POST "/login"      		登入      	範例: "/auth/login"
 * GET  "/logout"     		登出      	範例: "/auth/logout"
 * GET  "/isLoggedIn" 		判斷登入狀態 	範例: "/auth/isLoggedIn"
 * GET 	"/forgotPassword"	忘記密碼		範例: "/auth/forgotPassword"
 * */
@RestController
@RequestMapping("/auth")
//允許client端 存儲Session ID
@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173"}, allowCredentials = "true")
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	// ⭐ 注入 AuthenticationManager
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody RegisterDTO registerDTO) {
        try {
            UserDTO userDTO = userService.register(registerDTO);
            return new ApiResponse<>(200, "註冊成功", userDTO);
        } catch (AddException e) {
            return new ApiResponse<>(400, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "其他錯誤: " + e.getMessage(), null);
        }
    }
	
	@PostMapping("/login")
    public ApiResponse<UserDTO> login(@RequestBody LoginDTO loginDTO, HttpSession httpSession) {
        try {
            // 1️⃣ 交給 Spring Security 做帳號密碼驗證
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    );

            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // ⭐⭐ 這兩行很重要：印出目前的使用者＆角色
            System.out.println("登入成功 Authentication: " + authentication);
            System.out.println("Authorities: " + authentication.getAuthorities());
            
            // 2️⃣ 驗證成功後，放進 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3️⃣ 你原本的 userDTO 流程（從 DB 取出資料塞到 Session 給前端）
            UserDTO userDTO = userService.findByUsername(loginDTO.getUsername());
            userDTO.setPassword("******"); // 避免把密碼回傳出去
            httpSession.setAttribute("userDTO", userDTO);

            return new ApiResponse<>(200, "登入成功", userDTO);

        } catch (Exception e) {
            // 驗證失敗（帳號錯誤 / 密碼錯誤 / 被停用）
            return new ApiResponse<>(401, "登入失敗：" + e.getMessage(), null);
        }
    }
	
	@GetMapping("/logout")
	public  ApiResponse<Void> logout(HttpSession httpSession){
		httpSession.invalidate();
		return new ApiResponse<>(200,"登出成功",null);
	}
	
	@PostMapping("/forgot-password")
	public ApiResponse<Void> forgotPassword(@RequestBody ResetPasswordDTO dto) {
	    try {
	        userService.resetPasswordWithVerify(dto);
	        return new ApiResponse<>(200, "密碼更新成功", null);
	    } catch (UserNotFoundException e) {
	        return new ApiResponse<>(400, e.getMessage(), null);
	    } catch (LoginException e) {
	        return new ApiResponse<>(400, e.getMessage(), null);
	    } catch (Exception e) {
	        return new ApiResponse<>(400, "其他錯誤: " + e.getMessage(), null);
	    }
	}
	
	
	@GetMapping("/isLoggedIn")
	public ApiResponse<LoginDTO> isLoggedIn(HttpSession httpSession) {
		LoginDTO loginDTO = new LoginDTO();
		
		if(httpSession.getAttribute("userDTO") == null) {
			loginDTO.setIsLoggedIn(false);
			return new ApiResponse<>(400, "無登入資料", loginDTO);
		}
		
		UserDTO userDTO = (UserDTO)httpSession.getAttribute("userDTO");
		loginDTO.setIsLoggedIn(true);
		loginDTO.setUsername(userDTO.getUsername());
		loginDTO.setPassword(userDTO.getPassword());
		return new ApiResponse<>(200, "仍在登入狀態", loginDTO);
	}
}
