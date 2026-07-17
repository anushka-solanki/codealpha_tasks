# 🎓 GradeStream — Premium Student Grade Management & Analytics Dashboard

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" alt="HTML5" />
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" alt="CSS3" />
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript" />
  <img src="https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" />
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub" />
</p>

---

## 📌 Project Overview

**GradeStream** is a premium, high-performance Student Grade Management and Educational Analytics Dashboard. Designed to replace clunky legacy gradebooks, it provides teachers and administrators with real-time grade calculations, interactive data visualizations, automated performance insights, and professional reporting options in a stunning dark glassmorphic interface.

The application leverages a clean web-based frontend driven by a simulated **Java OOP Architecture** inside JavaScript. It mirrors structural Java design patterns, class-object mappings, arrays, and dynamic `ArrayList` operations to perform robust CRUD management.

---

## 🚀 Key Features

*   **🔐 Admin Authentication**: Secure login portal with beautiful particle effects and session guard checks.
*   **📊 Interactive Analytics Hub**: Six rich Chart.js widgets tracking subject performance, letter grade distributions, student averages, performance trends, and radar comparison mappings.
*   **📈 Real-time Analytics Cards**: Instant calculations for Class Average, Pass/Fail percentages, Highest/Lowest scores, and Top Performers.
*   **⚡ Automated Performance Insights**: Algorithmic advisory board identifying best/worst subject performance and at-risk students.
*   **🎓 Dynamic Student Directory**:
    *   Search by Name, ID, or Grade.
    *   Multi-column sorting and instant filtering.
    *   Clean pagination for large datasets.
    *   Inline actions and delete safety confirmations.
*   **📂 Comprehensive Reporting**:
    *   Export dataset to CSV file format.
    *   Export professionally styled PDF report tables.
    *   Printer-friendly dashboard layout output.
    *   Generate printable individual student report cards.
*   **⚙️ Dashboard Settings & Customizations**: Toggle between Midnight Blue, Cyberpunk, and Classic light/dark modes.

---

## 🛠️ Technologies Used

| Technology | Purpose |
| :--- | :--- |
| **Java / Java OOP** | Underlying data logic models, database array concepts, and structure design. |
| **HTML5** | Semantic structure, views navigation, dialog boxes, and template nodes. |
| **CSS3** | Premium glassmorphism layout, theme variables, transition animations, and print media layouts. |
| **JavaScript (ES6+)** | Business logic, state managers, CRUD operations, DOM events, and local database storage. |
| **Chart.js** | Visualizing analytics charts (radar, line, bar, doughnut, pie, horizontal-bar). |
| **html2canvas / jsPDF** | Client-side high-resolution PDF generation and report exporting. |

---

## 📐 Project Architecture

GradeStream uses a modular decoupled architectural pattern, separating the data-management tier (simulated Java model) from the presentation view-model tier.

```mermaid
graph TD
    Browser[index.html UI] <-->|Event Handlers| Controller[app.js Event Listener]
    Controller <-->|Calls CRUD| Manager[StudentManager Class]
    Manager <-->|Saves/Loads| Storage[(localStorage Database)]
    Manager -->|ArrayList Collection| StudentList[Student List]
    StudentList -->|Iterates| Student[Student Class Object]
    Student -->|Stores grades| GradeArray[grades double[] Array]
```

---

## 📂 Folder Structure

```text
Student-Grade-Tracker/
├── login.html             # Secure Admin Login Portal
├── index.html             # Core Single-Page Admin Dashboard View
├── styles.css             # Glassmorphism Layouts & Color Theme Variables
├── app.js                 # Simulated Java Backend & Core Application Logic
├── README.md              # Project Documentation
└── screenshots/           # Application screenshots for GitHub
    ├── dashboard.png
    ├── add-student.png
    ├── analytics.png
    └── report.png
```

---

## 🖼️ Application Screenshots

### 🖥️ Dashboard Overview
![Dashboard Overview](/screenshots/dashboard.png)
*Beautiful dark glassmorphism layout showing real-time statistics cards, recent activity, and performance insights.*

### ➕ Add & Edit Student Modal
![Add Student Modal](/screenshots/add-student.png)
*Interactive modal with live preview calculations of grade averages and validation error messages.*

### 📊 Analytics Center
![Analytics Center](/screenshots/analytics.png)
*Six interactive Chart.js widgets depicting subject-wise averages, grade distributions, and student trends.*

### 📄 Reports Generator
![Reports Generator](/screenshots/report.png)
*Live preview report tables and buttons to export clean CSV, PDF, and print layouts.*

---

## ⚙️ Installation Guide

### Prerequisites
*   Any modern web browser (Google Chrome, Firefox, Microsoft Edge, Safari).
*   No database servers or local compiler installations are required (runs completely serverless in the browser).

### How to Run
1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/your-username/Student-Grade-Tracker.git
    ```
2.  **Navigate to Directory**:
    ```bash
    cd Student-Grade-Tracker
    ```
3.  **Open the Project**:
    *   Double-click the `login.html` file to open it in your browser.
    *   Or open it via command line:
        ```bash
        start chrome login.html
        ```

---

## 📖 Usage Instructions

1.  **Sign In**: Log in using the default credentials:
    *   **Username**: `admin`
    *   **Password**: `admin123`
2.  **Dashboard Home**: View general class stats, recent operation logs, and performance advisor tips.
3.  **Add Student**: Click the **Add Student** button. Complete the name, subject scores, and remarks, then save.
4.  **Manage Data**: Double-click a row's details to view attendance, grade metrics, and individual charts. Use the action buttons to edit details or delete entries.
5.  **Analytics**: Click the **Analytics** menu in the sidebar to review grade charts and subject comparisons.
6.  **Exporting**: Click **Reports** in the sidebar to export CSV files, generate PDF summaries, or print report cards.

---

## ☕ Java Concepts Used

GradeStream implements standard Java OOP (Object-Oriented Programming) concepts translated into ES6 class architecture:

1.  **Classes & Objects**: Core models are structured as blueprint classes (`Student` and `StudentManager`) instantiated dynamically.
2.  **Encapsulation**: Private class properties and methods (e.g. `#students`, `#save()`, `#generateAvatar()`) hide data access from outer scripts.
3.  **Arrays**: Subject marks are managed as a fixed-size numeric array (`double[] grades` equivalent) mimicking standard Java arrays.
4.  **ArrayList**: Student database collections are stored and manipulated using standard dynamic list methods (`push`, `splice`, `filter`) resembling Java's `ArrayList<Student>`.
5.  **Exception Handling**: Input fields enforce bounds checking and throw validation errors/exceptions to prevent invalid scores (e.g. negative grades or strings).
6.  **Methods**: Core operations are structured as modular member methods (e.g. `calculateAverage()`, `calculateGrade()`, `highestScore()`).

---

## 🔮 Future Enhancements

*   **☁️ Cloud Sync**: Integrate a live database backend (Firebase or MongoDB) with a real Java Spring Boot REST API.
*   **📧 Email Notifications**: Auto-email report cards and performance alerts directly to parents and students.
*   **📅 Attendance Tracker**: Integrate full interactive calendars for day-by-day attendance tracking.
*   **👥 Multi-User Roles**: Add distinct interfaces for Students, Teachers, and Super Admins.

---

## 🤝 Contributing

Contributions make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project.
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the Branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 👤 Author & Contact Info

*   **Developer**: [Your Name]
*   **Email**: your.email@example.com
*   **GitHub**: [github.com/your-username](https://github.com/your-username)
*   **LinkedIn**: [linkedin.com/in/your-username](https://linkedin.com/in/your-username)

---

## 🙏 Acknowledgements

*   [Chart.js Documentation](https://www.chartjs.org/docs/latest/)
*   [jsPDF & html2canvas Libraries](https://github.com/parallax/jsPDF)
*   [Shields.io Badges](https://shields.io/)
