# College Event & Club Management System

A comprehensive web application built with **Spring Boot** for managing college events, clubs, and student participation. This system provides role-based access for administrators, club heads, and students to efficiently organize and participate in college activities.

## 🚀 Features

### Core Features
- **User Management**: Role-based access (Admin, Club Head, Student)
- **Event Management**: Create, edit, delete events with registration system
- **Club Management**: Create and manage clubs with membership tracking
- **Registration System**: Students can register/unregister for events
- **Notification System**: Real-time notifications for events and activities
- **Dashboard**: Personalized dashboards for different user roles

### Advanced Features
- **Participation Tracking**: Complete history of student participation
- **Smart Notifications**: Email and in-app notifications
- **Responsive Design**: Mobile-friendly interface
- **Search & Filter**: Easy discovery of events and clubs
- **Admin Reports**: Comprehensive analytics and reports

## 🛠 Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3.1.2**
- **Spring Data JPA**
- **Spring Security**
- **Maven**

### Frontend
- **Thymeleaf** (Template Engine)
- **Bootstrap 5** (CSS Framework)
- **JavaScript/jQuery**
- **Font Awesome** (Icons)

### Database
- **MySQL** (Primary)
- **H2** (Testing)

## 📋 Prerequisites

Before running this application, make sure you have:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git**

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/event-management.git
cd event-management
```                                         

### 2. Database Setup

#### Create MySQL Database
```sql
CREATE DATABASE college_event_management;
```

#### Run the Schema Script
```bash
mysql -u root -p college_event_management < database/schema.sql
```

### 3. Configure Application

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/college_event_management
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

# Mail Configuration (Optional)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 4. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 5. Access the Application

Open your browser and navigate to: `http://localhost:8080`

## 👥 Default Users

The application comes with pre-configured users:

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@college.edu | admin123 |
| Club Head | john.smith@college.edu | admin123 |
| Student | jane.doe@college.edu | admin123 |

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/college/eventmanagement/
│   │   ├── EventManagementApplication.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   └── HomeController.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Club.java
│   │   │   ├── Event.java
│   │   │   ├── EventRegistration.java
│   │   │   └── Notification.java
│   │   └── repository/
│   │       ├── UserRepository.java
│   │       ├── ClubRepository.java
│   │       ├── EventRepository.java
│   │       ├── EventRegistrationRepository.java
│   │       └── NotificationRepository.java
│   ├── resources/
│   │   ├── application.properties
│   │   └── templates/
│   │       ├── layout.html
│   │       └── index.html
│   └── webapp/
│       ├── css/
│       │   └── style.css
│       └── js/
│           └── app.js
├── database/
│   └── schema.sql
└── pom.xml
```

## 🗄 Database Schema

The application uses the following main entities:

- **Users**: Store user information with roles
- **Clubs**: Manage college clubs and their details
- **Club Members**: Track club memberships
- **Events**: Store event information and details
- **Event Registrations**: Track student event registrations
- **Notifications**: Handle system notifications

## 🔧 Configuration

### Environment Variables
You can also configure the application using environment variables:

```bash
export DB_URL=jdbc:mysql://localhost:3306/college_event_management
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
```

### Profiles
The application supports different profiles:

- `dev`: Development profile with detailed logging
- `prod`: Production profile with optimized settings
- `test`: Testing profile with H2 database

```bash
# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

## 📖 API Documentation

The application provides REST endpoints for various operations:

- `/api/users` - User management
- `/api/clubs` - Club operations
- `/api/events` - Event management
- `/api/notifications` - Notification handling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🐛 Known Issues

- Email notifications require proper SMTP configuration
- File upload for event images not yet implemented
- Advanced search functionality is in development

## 🚧 Roadmap

- [ ] File upload for event images and club logos
- [ ] Advanced search and filtering
- [ ] Calendar integration
- [ ] Mobile app development
- [ ] Social media integration
- [ ] Advanced reporting and analytics

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:

- Create an issue on GitHub
- Email: support@college-events.com
- Documentation: [Wiki](https://github.com/your-username/event-management/wiki)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap for the responsive UI components
- Font Awesome for the beautiful icons
- MySQL for the reliable database solution

---

**Happy Coding! 🎉**
