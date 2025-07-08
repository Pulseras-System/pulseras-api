# pulseras-api
A RESTful backend for managing products, categories, ratings, promotions, orders, and feedback for an e-commerce bracelet store. Built with **Spring Boot**, **MongoDB**, **JWT**, **AWS S3**, and **Firebase**.

---

## 🚀 Features

- 🔐 JWT-based Authentication
- 🛍 Product Management (with S3 Image Upload)
- 🗃 Category Management
- 🌟 Ratings & Feedback APIs
- 🎁 Promotions Support
- 📦 Order & Order Details
- 📂 File Upload via AWS S3
- 🔥 Firebase Integration (FCM)
- 🔍 Search, Sort, Pagination

---

## 🛠 Tech Stack

| Layer        | Tech                        |
|-------------|-----------------------------|
| Backend     | Spring Boot 3 (Maven)       |
| Database    | MongoDB                     |
| Auth        | JWT                         |
| Storage     | AWS S3                      |
| Notifications | Firebase Cloud Messaging |
| Docs        | Swagger UI / Postman        |

---

## 📁 Project Structure
src/
├── controller/
├── service/
├── service/impl/
├── dto/
├── entity/
├── repository/
├── mapper/
├── config/
└── PulserasApiApplication.java
