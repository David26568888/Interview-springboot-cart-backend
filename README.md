🛒 Spring Boot 電商後端系統 (Shopping Cart Backend)

本專案為 Spring Boot + Spring Security + MySQL + JPA 所打造的後端 API，
搭配 React 前端構成完整的購物流程示範系統。

提供使用者註冊、登入、驗證碼、防暴力登入、商品管理、購物車結帳、訂單紀錄、銷售統計、關注機制…等功能。
可作為面試展示或個人作品集使用。

📌 系統特色功能
👤 使用者系統 (User System)

 註冊（帳號、密碼、姓名、身分證字號、生日、電話）

 密碼加鹽加密（BCrypt）

 登入（Spring Security 驗證）

 自訂 Captcha 驗證碼（防止暴力攻擊 / bot 登入）

 忘記密碼（需比對身份證字號 + 電話）

權限管理：

 ROLE_USER

 ROLE_ADMIN（可管理商品、查看銷售）

🛒 商品系統 (Product System)

 商品查詢（支援分頁、關鍵字搜尋）

 商品新增 / 修改 / 刪除（僅 ADMIN）

 商品圖片使用 Base64 儲存

 卡片式商品資料 + 圖片顯示

 使用者可將商品加入購物車

❤️ 商品關注 (Favorite System)

 使用者可加入 / 取消關注

 每位使用者有自己的關注清單

 商品頁面顯示「我的關注清單」

📦 訂單系統 (Order System)

 前端把購物車內容送到後端 → 自動產生訂單

 每筆訂單包含多筆 OrderItem

 儲存下單當下的商品價格與數量（避免商品異動）

 歷史訂單紀錄（依使用者 Session 判定）

📊 銷售統計 (Admin)

 依商品統計：

 銷售數量

 銷售總額

 API 僅允許 ADMIN 呼叫

 前端用 Recharts 長條圖呈現（視覺化報表）

🏗 系統架構 (Architecture)
React (Front-end)
   │ REST API (Fetch + JSON)
   ▼
Spring Boot Backend
   ├─ Controller (接收請求)
   ├─ Service (商業邏輯)
   ├─ Repository (JPA/MySQL)
   ├─ Security (登入 / 驗證碼)
   └─ DTO / Entity
   ▼
MySQL Database

🗄 資料庫 ERD (簡化版)
User ——< UserRole
 |
 └——< FavoriteProduct ——> Product

Order ——< OrderItem ——> Product


User

id, username, password, enabled, name, phone, idNumber, birthday

Product

id, name, price, imageBase64

Order

id, userId, createdAt

OrderItem

id, orderId, productId, qty, price, subtotal

🔐 Spring Security 流程說明
Login 流程（含 Captcha）

前端輸入帳密 + 驗證碼

CaptchaFilter 檢查是否輸入正確

AuthenticationManager 認證帳密

CustomUserDetailsService 載入用戶

Spring Security 建立 Session

API 回傳 userDTO 給前端

📁 專案結構 (Backend)
src/main/java/com.example.demo.cart
│
├── controller        # API 入口
├── service           # 商業邏輯
├── service.impl      # Service 實作
├── repository        # JPA Repository
├── model
│   ├── entity        # 資料庫對應
│   └── dto           # 對前端傳遞資料
├── security          # Spring Security + Captcha
├── exception         # 自訂 Error
└── response          # 統一 API 回應格式

🚀 如何運行後端
1️⃣ 建立資料庫

MySQL 建立 Schema：

CREATE DATABASE shopping_db CHARACTER SET utf8mb4;


修改 application.properties：

spring.datasource.url=jdbc:mysql://localhost:3306/shopping_db?useSSL=false&serverTimezone=Asia/Taipei
spring.datasource.username=root
spring.datasource.password=你的密碼
spring.jpa.hibernate.ddl-auto=update

2️⃣ 啟動 Spring Boot

在 IDE (Eclipse / IntelliJ) 執行：

DemoApplication.java


後端預設啟動：

http://localhost:8080

📘 API Example（部分）
➤ 登入 /auth/login
POST /auth/login
{
  "username": "admin",
  "password": "123456",
  "captcha": "ABCD"
}

➤ 取得商品（支援分頁搜尋）
GET /products?page=0&size=6&keyword=蘋果

➤ 新增商品（ADMIN）
POST /products
{
  "name": "哈密瓜",
  "price": 250,
  "imageBase64": "data:image/png;base64,..."
}

➤ 結帳
POST /orders/checkout
[
  { "productId": 1, "qty": 1 },
  { "productId": 3, "qty": 2 }
]

➤ 查詢銷售統計（ADMIN）
GET /orders/sales/summary
