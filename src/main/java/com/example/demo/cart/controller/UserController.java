package com.example.demo.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.demo.cart.exception.AddException;
import com.example.demo.cart.exception.UserNotFoundException;
import com.example.demo.cart.model.dto.UpdateUserDTO;
import com.example.demo.cart.model.dto.UserDTO;
import com.example.demo.cart.response.ApiResponse;
import com.example.demo.cart.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173"}, allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    // ⭐ 更新使用者資料（自己或 Admin）
    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO dto,
            HttpSession session) {

        // 確保 body 裡的 id 跟 path 相同
        dto.setId(id);

        // 取得目前登入者
        UserDTO sessionUser = (UserDTO) session.getAttribute("userDTO");
        if (sessionUser == null) {
            return new ApiResponse<>(401, "請先登入", null);
        }

        // 若不是本人、也沒有 ROLE_ADMIN，就拒絕
        boolean isAdmin = sessionUser.getRoles() != null &&
                sessionUser.getRoles().contains("ROLE_ADMIN");

        if (!isAdmin && !sessionUser.getId().equals(id)) {
            return new ApiResponse<>(403, "無權限更新此使用者資料", null);
        }

        try {
            UserDTO updated = userService.updateUser(dto);
            return new ApiResponse<>(200, "更新成功", updated);
        } catch (UserNotFoundException e) {
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (AddException e) {
            return new ApiResponse<>(400, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "其他錯誤: " + e.getMessage(), null);
        }
    }

    // ⭐ 刪除使用者（只允許 ADMIN，SecurityConfig 另外限制）
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ApiResponse<>(200, "刪除成功", null);
        } catch (UserNotFoundException e) {
            return new ApiResponse<>(404, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "其他錯誤: " + e.getMessage(), null);
        }
    }
}
