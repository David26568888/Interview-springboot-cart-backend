package com.example.demo.test.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.cart.model.entity.User;
import com.example.demo.cart.repository.UserRepository;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 讓測試可以照 @Order 執行
public class UserCrudTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用來在測試之間共用「同一個 User 的 id」
    private static Long testUserId;

    private static final String TEST_USERNAME = "crud_test_user";
    private static final String TEST_PASSWORD = "123456";
    private static final String NEW_PASSWORD = "654321";

    // 1️⃣ 建立 User（Create）
    @Test
    @Order(1)
    void testCreateUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD)); // BCrypt 加密
        user.setEnabled(true);
        user.getRoles().add("ROLE_USER");

        user = userRepository.save(user);

        assertNotNull(user.getId(), "新增 User 之後，id 不應該為 null");
        testUserId = user.getId();

        System.out.println("✅ 建立 User 完成，id = " + testUserId);
    }

    // 2️⃣ 查詢 User（Read）
    @Test
    @Order(2)
    void testReadUser() {
        assertNotNull(testUserId, "testUserId 應該在 testCreateUser 裡被賦值");

        Optional<User> optUser = userRepository.findById(testUserId);
        assertTrue(optUser.isPresent(), "應該可以找到剛剛新增的 User");

        User user = optUser.get();
        assertEquals(TEST_USERNAME, user.getUsername(), "帳號應該一致");

        // 密碼是加密過的，不應該等於明碼
        assertNotEquals(TEST_PASSWORD, user.getPassword(), "資料庫中的密碼不應該是明碼");

        // 但用 PasswordEncoder.matches 應該可以驗證成功
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, user.getPassword()),
                "BCrypt 應該可以驗證原始密碼");

        System.out.println("✅ 查詢 User 完成，username = " + user.getUsername());
    }

    // 3️⃣ 更新 User 密碼（Update）
    @Test
    @Order(3)
    void testUpdateUserPassword() {
        assertNotNull(testUserId, "testUserId 應該在 testCreateUser 裡被賦值");

        Optional<User> optUser = userRepository.findById(testUserId);
        assertTrue(optUser.isPresent(), "更新前應該可以找到 User");

        User user = optUser.get();

        // 修改密碼為新值（同樣用 BCrypt 加密）
        String encodedNewPwd = passwordEncoder.encode(NEW_PASSWORD);
        user.setPassword(encodedNewPwd);

        userRepository.save(user);

        // 再查一次確認有更新
        User updatedUser = userRepository.findById(testUserId).orElseThrow();
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, updatedUser.getPassword()),
                "更新後的密碼應該可以用新密碼驗證");
        assertFalse(passwordEncoder.matches(TEST_PASSWORD, updatedUser.getPassword()),
                "更新後的密碼不應該再通過舊密碼驗證");

        System.out.println("✅ 更新 User 密碼完成");
    }

    // 4️⃣ 刪除 User（Delete）
    @Test
    @Order(4)
    void testDeleteUser() {
        assertNotNull(testUserId, "testUserId 應該在 testCreateUser 裡被賦值");

        userRepository.deleteById(testUserId);

        Optional<User> optUser = userRepository.findById(testUserId);
        assertTrue(optUser.isEmpty(), "刪除後應該找不到該 User");

        System.out.println("✅ 刪除 User 完成");
    }
}
