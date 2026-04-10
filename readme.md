# 🚀 Resume Builder Application

A full-stack **Resume Builder Web Application** that allows users to create, manage, and download professional resumes
with ease. The platform supports authentication, premium features, secure payments, and cloud-based media handling.

---

## 📌 Features

### 👤 User Features

* User Registration & Login (JWT based authentication)
* Secure authentication using Spring Security
* Create, update, and delete resumes
* Manage multiple resumes per user
* Upload profile image and resume thumbnail
* Organized resume sections:

    * Profile Info
    * Contact Info
    * Education
    * Work Experience
    * Skills
    * Projects
    * Certifications
    * Languages
    * Interests

---

### 💳 Subscription Plans

* **Basic Plan**

    * Limited features
* **Premium Plan**

    * Full access to advanced features

---

### 💰 Payment Integration

* Integrated **Razorpay Payment Gateway**
* Secure order creation and payment verification
* Plan upgrade system (Basic → Premium)

---

### ☁️ Cloud Integration

* Image storage using **Cloudinary**

    * Profile image
    * Resume thumbnails

---

## 🛠️ Tech Stack

### 🔙 Backend

* Java + Spring Boot
* Spring Security
* JWT (Authentication & Authorization)
* Spring Data MongoDB

### 🗄️ Database

* MongoDB (NoSQL, document-based)

### ☁️ Cloud Services

* Cloudinary (Media storage)

### 💳 Payments

* Razorpay API

---

## 📂 Project Structure

```
com.project.resume
 ├── controller
 ├── service
 ├── repository
 ├── documents
 ├── dto
 ├── security
 ├── exception
 └── util
```

---

## ⚙️ Installation & Setup

### 1️⃣ Clone Repository

```
git clone https://github.com/SamBhusnar/Resume-builder-backend-api.git resume-builder
cd resume-builder
```

---

### 2️⃣ Configure Environment Variables

Create `.env` or configure in `application.yml`:

```
MONGODB_URI=your_mongodb_uri
key=your_bravo_key
jwt_secret=your_jwt_secret
cloudinary_secret=your_cloudinary_secret
razorpay_key=your_key
razorpay_secret=your_secret
```

---

### 3️⃣ Run Application

```
mvn spring-boot:run
```

---

## 🔐 Authentication

* Uses **JWT Token**
* Token must be passed in headers:

```
Authorization: Bearer <token>
```

---

## 📡 API Endpoints (Sample)

### 🔑 Auth

* `POST /api/v1/auth/register`
* `POST /api/v1/auth/login`

### 📄 Resume

* `POST /api/v1/resumes`
* `GET /api/v1/resumes`
* `GET /api/v1/resumes/{id}`
* `PUT /api/v1/resumes/{id}`
* `DELETE /api/v1/resumes/{id}`

### 💳 Payments

* `POST /api/v1/payments/create-order`
* `POST /api/v1/payments/verify-payment`
* `GET /api/v1/payments/history`

---

## 🧠 Key Concepts Used

* RESTful API Design
* JWT-based authentication
* Role/plan-based access control
* NoSQL schema design (MongoDB)
* Cloud media handling
* Payment gateway integration
* Global exception handling

---

## 🚧 Current Status

✅ Core features completed
✅ Authentication & Security implemented
✅ Resume management system ready
✅ Cloudinary integration done
✅ Razorpay integration completed

---

## 🔮 Future Enhancements

* Resume PDF download
* Multiple resume templates
* AI-based resume suggestions
* Admin dashboard
* Analytics & usage tracking

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork the repo and submit a pull request.

---

## 📜 License

This project is open-source .

---

## 👨‍💻 Author

Developed by **Samadhan Bhusnar**

---
