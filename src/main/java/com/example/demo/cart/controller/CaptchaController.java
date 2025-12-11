package com.example.demo.cart.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.SecureRandom;

@RestController
public class CaptchaController {

    public static final String CAPTCHA_SESSION_KEY = "CAPTCHA_CODE";
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response, HttpSession session) throws IOException {

        int width = 100;
        int height = 40;

        // 產生 4 碼
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int idx = random.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(idx));
        }
        String code = sb.toString();

        // 存進 Session
        session.setAttribute(CAPTCHA_SESSION_KEY, code);

        // 建立圖片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 干擾線
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 8; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 畫文字
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(code);
        int textHeight = fm.getAscent();

        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2 - 4;
        g.drawString(code, x, y);

        g.dispose();

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-store, no-cache");
        ImageIO.write(image, "png", response.getOutputStream());
    }
}
