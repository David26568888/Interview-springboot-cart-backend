package com.example.demo.cart.security;

import com.example.demo.cart.controller.CaptchaController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 如果你是用 Spring Security formLogin, 通常是 POST /login
        // 如果你是自己寫 AuthController /login API，就改成對應 URL
        if ("/api/auth/login".equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod())) {

            String captchaInput = request.getParameter("captcha");
            HttpSession session = request.getSession(false);
            String captchaInSession = (session != null)
                    ? (String) session.getAttribute(CaptchaController.CAPTCHA_SESSION_KEY)
                    : null;

            if (captchaInSession == null ||
                    captchaInput == null ||
                    !captchaInSession.equalsIgnoreCase(captchaInput)) {

                // 驗證碼錯誤，可以改成回傳 JSON 或 redirect，看你登入是 API 還是表單
            	response.setStatus(400);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
