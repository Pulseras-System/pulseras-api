# Pulseras Bracelet E-Commerce Backend API

A robust Spring Boot REST API for managing bracelet products, user feedback, ratings, promotions, authentication, and AWS S3 file uploads.

---

## ✨ Features

- 🛍 Product CRUD with image upload (S3) + category tagging
- 🎁 Promotion management with discount logic
- ⭐ Product ratings with average score tracking
- 💬 User feedback system
- 🔐 JWT Authentication and Role-based Authorization
- 🗂 Pagination, Search, Sorting on all GET APIs
- 📂 Upload images to AWS S3
- 📄 Swagger for easy API testing

---

## 🧱 Tech Stack

- Java 17 + Spring Boot 3
- MongoDB (Spring Data)
- AWS S3 SDK v2
- Spring Security + JWT
- Maven
- Swagger UI (Springdoc OpenAPI)

---

## Getting Started

### 1. Clone & Build
```bash
git clone https://github.com/your-username/pulseras-backend.git
cd pulseras-backend
./mvnw clean install

### 2. Configure Environment

Edit `src/main/resources/application.properties`:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>?retryWrites=true&w=majority

# JWT
jwt.secret=your-jwt-secret

# AWS S3
application.bucket.name=your-s3-bucket-name
aws.accessKey=your-access-key
aws.secretKey=your-secret-key
```

---

### 3. Build & Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

App will start at:  
📍 `http://localhost:8080`

---

## 📂 API Modules

| Module          | Endpoint                   | Description                                |
|-----------------|----------------------------|--------------------------------------------|
| Auth            | `/api/auth`               | User registration & login with JWT         |
| Account         | `/api/accounts`           | Manage user account info, CRUD                   |
| Role            | `/api/roles`              | Role-based access management               |
| Product         | `/api/products`           | Products with image upload & categories, CRUD    |
| Category        | `/api/categories`         | Product category CRUD                      |
| Promotion       | `/api/promotions`         | Time-limited discounts per product, CRUD         |
| Rating          | `/api/ratings`            | Rate & comment on products                 |
| Feedback        | `/api/feedbacks`          | User feedback about product/service        |
| Image Upload    | `/api/images/upload`      | Upload images directly to S3               |
| Cart            | `/api/carts`              | Manage shopping cart per user              |
| Order           | `/api/orders`             | Place & track orders                       |
| Order Details   | `/api/order-details`      | Detail line-items of orders                |
| Payment         | `/api/payments`           | Handle payment information or status       |
| Notification    | `/api/notifications`      | Push or retrieve system/user notifications |
| Voucher         | `/api/vouchers`           | Discount codes / vouchers apply to orders  |


---

## 🔗 Swagger API Docs

```bash
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 Example: Product Creation with Image

### ➕ POST `/api/products`

**Content-Type**: `multipart/form-data`

**Parts:**

| Name   | Type     | Description                  |
|--------|----------|------------------------------|
| data   | JSON     | Product JSON payload         |
| image  | File     | Image file (jpg/png)         |

**Sample CURL:**

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -F "data=@product.json;type=application/json" \
  -F "image=@bracelet.jpg;type=image/jpeg"
```

---

## 🔐 Authentication

1. **POST** `/api/auth/login`  
   Returns JWT token.

2. **Use JWT** in requests:

```http
Authorization: Bearer <your-token>
```

---

## 📊 Dashboard Endpoints

- `/api/products/latest` → 6 newest products  
- `/api/products/top-buy` → Top 6 most bought  
- `/api/products/type-distribution` → Pie chart data

---

## ✅ Future Improvements

- 🧑‍💼 Admin dashboard (React.js)
- 📧 Email notifications
- 📦 Docker support
- 📱 Mobile-ready API
- 📊 Analytics

---

## 🪪 License

**MIT** 


[MIT](https://choosealicense.com/licenses/mit/)
