# 🏨 Smart Hotel Reservation System

> **A professional, internship-level Java desktop application** built with Core Java, Swing GUI, OOP principles, MVC architecture, and File Handling. Designed for GitHub portfolio and internship submission.

---

## 📸 Features At a Glance

| Feature | Description |
|---|---|
| 🔐 Login System | Admin & Customer login, registration, forgot password simulation |
| 🛌 Room Management | 10+ rooms across 5 types, status badges, amenities filters |
| 📅 Reservations | Book, modify, cancel, check availability — with duplicate prevention |
| 💳 Payments | Simulated gateway (Credit Card / UPI / Cash etc.), GST, coupon codes |
| 👤 Customers | Profile management, loyalty points tier system, wishlist, feedback |
| ⚙️ Admin Panel | Room inventory, reports, backups, restore, system audit logs |
| 📊 Dashboard | Revenue stats, occupancy rate, custom Java2D bar chart, weather widget |
| 🌙 Dark/Light Mode | Instant theme switching across all windows |
| 💵 Currency Converter | USD → EUR / INR / GBP simulation widget |
| 💾 Auto Backup | Triggered automatically on logout and manually on demand |
| 🖨️ Invoice Generation | Detailed text invoice saved to `reports/` folder |
| 📋 Export Reports | PDF/Excel simulated exports of analytics to `reports/` folder |

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (Tested with Java 26)

### Run (Windows)
```bash
cd HotelReservationSystem
run.bat
```

### Compile & Run Manually
```bash
# Compile
javac -d bin -sourcepath src src/com/hotel/Main.java

# Run
java -cp bin com.hotel.Main
```

---

## 🔑 Default Login Credentials

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Admin (Manager) | `manager` | `manager123` |
| Customer | `john_doe` | `pass123` |
| Customer | `jane_smith` | `pass456` |

> To create a new customer, click **"Create Account"** on the login screen.

---

## 🗂 Project Structure

```
HotelReservationSystem/
│
├── src/
│   └── com/hotel/
│       ├── Main.java                   ← Application Entry Point
│       ├── model/                      ← OOP Data Models
│       │   ├── User.java               (Abstract base class)
│       │   ├── Admin.java              (Extends User)
│       │   ├── Customer.java           (Extends User, wishlist, loyalty)
│       │   ├── Room.java               (Comparable, Serializable)
│       │   ├── Reservation.java        (Comparable, Serializable)
│       │   ├── Payment.java
│       │   └── Feedback.java
│       ├── database/
│       │   └── FileDatabase.java       ← File I/O Persistence Layer
│       ├── service/                    ← Business Logic
│       │   ├── RoomService.java
│       │   ├── CustomerService.java
│       │   ├── ReservationService.java
│       │   ├── PaymentService.java
│       │   ├── ReportService.java
│       │   └── BackupService.java
│       ├── controller/
│       │   └── HotelController.java    ← Central MVC Coordinator
│       ├── ui/                         ← Swing GUI Layer
│       │   ├── ThemeManager.java       (Design tokens & dark/light mode)
│       │   ├── RoundedPanel.java       (Custom rounded card component)
│       │   ├── GradientPanel.java      (Custom gradient background)
│       │   ├── CustomButton.java       (Hover animations, flat design)
│       │   ├── ToastNotification.java  (Auto-fade notification)
│       │   ├── SplashScreen.java       (Multi-threaded splash screen)
│       │   ├── MainFrame.java          (Master window + sidebar nav)
│       │   ├── LoginPanel.java         (Login / Register / Forgot Password)
│       │   ├── DashboardPanel.java     (Stats, bar chart, currency widget)
│       │   ├── RoomPanel.java          (Room cards, wishlist, filters)
│       │   ├── BookingPanel.java       (Booking form + table)
│       │   ├── CustomerPanel.java      (Profile, loyalty, feedback)
│       │   ├── PaymentPanel.java       (Billing ledger)
│       │   └── AdminPanel.java         (Tabbed admin tools)
│       └── util/
│           ├── ValidationUtil.java     (Email, phone, date validation)
│           └── DateTimeUtil.java       (Date math, overlap detection)
│
├── data/                               ← Persistent File Storage
│   ├── admins.txt
│   ├── rooms.txt
│   ├── customers.txt
│   ├── bookings.txt
│   ├── payments.txt
│   ├── feedback.txt
│   ├── settings.txt
│   ├── logs.txt
│   └── backup/                         ← Auto & manual backups here
│
├── reports/                            ← Generated invoices & exports
├── bin/                                ← Compiled class files
├── run.bat                             ← One-click Windows launcher
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🧠 OOP Concepts Demonstrated

| Concept | Where Used |
|---|---|
| **Encapsulation** | All model classes — private fields, validated setters |
| **Inheritance** | `Admin` and `Customer` extend abstract `User` |
| **Abstraction** | Abstract `User` class |
| **Polymorphism** | Service method overloading (search with/without filters) |
| **Interfaces** | `Serializable`, `Comparable<Room>`, `Comparable<Reservation>`, `ThemeListener` |
| **Method Overloading** | Search rooms by query vs. full filter set |
| **Method Overriding** | `compareTo()` in `Room` (by price) and `Reservation` (by check-in date), `paintComponent()` in custom panels |
| **Exception Handling** | Try-catch in all I/O operations and input parsing |
| **Collections** | `List<Room>`, `List<Reservation>`, `Map<String, String>` for settings |
| **Multi-threading** | Splash screen loading thread, live clock thread |

---

## 💳 Coupon Codes

| Code | Discount |
|---|---|
| `WELCOME10` | 10% off base room charge |
| `SUMMER20` | 20% off base room charge |
| `LOYAL50` | $50 flat discount (orders > $150) |
| `FESTIVE15` | 15% off base room charge |

---

## 📦 Data Files

All data is stored as comma-separated text in the `data/` folder and is **auto-loaded on startup** and **auto-saved on every operation**.

---

## 🔮 Future Scope

- Online booking via REST API integration
- PDF invoice generation with iText library
- Database migration from flat files to SQLite/MySQL
- Email notification on booking confirmation
- Mobile companion app (Android)
- AI-based room recommendation engine

---

## 👨‍💻 Developer

**Smart Hotel Reservation System** — Built as an internship-level Java portfolio project demonstrating MVC architecture, Swing GUI, file-based persistence, and comprehensive OOP principles.

> *Built with ❤️ using Core Java 17+ and Java Swing*
