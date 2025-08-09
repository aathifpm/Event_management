# SETUP INSTRUCTIONS - College Event Management System

## ✅ **Current Status:**
- **Compilation**: ✅ SUCCESS with Java 21
- **Database**: Ready to set up
- **Frontend**: Complete with responsive design
- **Spring Boot Structure**: Complete

## 🔧 **Environment Setup Required:**

### **1. Set JAVA_HOME Permanently**

Run this in **PowerShell as Administrator**:
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "Machine")
[Environment]::SetEnvironmentVariable("PATH", "$env:PATH;C:\Program Files\Java\jdk-21\bin", "Machine")
```

**OR** manually:
1. Press `Win + X` → `System`
2. Click `Advanced system settings`
3. Click `Environment Variables`
4. Under `System Variables`:
   - New → Variable: `JAVA_HOME`, Value: `C:\Program Files\Java\jdk-21`
   - Edit `PATH` → Add: `C:\Program Files\Java\jdk-21\bin`

### **2. Restart PowerShell/Terminal**
Close all terminals and reopen to apply environment changes.

### **3. Verify Setup**
```bash
java -version    # Should show Java 21
mvn --version   # Should show Java 21
```

## 🗄 **Database Setup:**

### **Option 1: MySQL (Recommended)**
1. Ensure MySQL is running
2. Create database:
   ```sql
   CREATE DATABASE college_event_management;
   ```
3. Run the schema:
   ```bash
   mysql -u root -p college_event_management < database/schema.sql
   ```
4. Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

### **Option 2: H2 Database (Simple Testing)**
Update `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

## 🚀 **Running the Application:**

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

**Access at:** `http://localhost:8080`

## 📝 **Default Users:**
| Email | Password | Role |
|-------|----------|------|
| admin@college.edu | admin123 | Admin |
| john.smith@college.edu | admin123 | Club Head |
| jane.doe@college.edu | admin123 | Student |

## 🔍 **Troubleshooting:**

### **Issue 1: Maven uses wrong Java version**
```bash
echo $env:JAVA_HOME  # Should show Java 21 path
mvn --version        # Should show Java 21
```

### **Issue 2: Database connection issues**
- Check MySQL is running: `services.msc` → MySQL
- Verify database exists: `SHOW DATABASES;`
- Check credentials in `application.properties`

### **Issue 3: Port 8080 already in use**
Add to `application.properties`:
```properties
server.port=8081
```

## 📁 **Project Structure:**
```
src/
├── main/java/com/college/eventmanagement/
│   ├── EventManagementApplication.java (✅ Main class)
│   ├── model/ (✅ Complete entities)
│   ├── repository/ (✅ JPA repositories)
│   ├── controller/ (✅ Basic controller)
│   └── config/ (✅ Security config)
├── main/resources/
│   ├── application.properties (✅ Configuration)
│   └── templates/ (✅ Thymeleaf templates)
└── main/webapp/
    ├── css/ (✅ Styles)
    └── js/ (✅ JavaScript)
```

## 🎯 **Next Development Steps:**
1. **Service Layer** - Business logic
2. **Controllers** - REST APIs
3. **Authentication** - User login/registration
4. **Frontend Integration** - Connect UI to backend
5. **Testing** - Unit and integration tests

## 🆘 **If Still Having Issues:**

1. **Temporary Workaround - Use IDE:**
   - Import project in IntelliJ IDEA or Eclipse
   - Set Project SDK to Java 21
   - Run the main class directly

2. **Check Dependencies:**
   ```bash
   mvn dependency:tree
   ```

3. **Clean and Rebuild:**
   ```bash
   mvn clean install -U
   ```

## 📧 **Support:**
If you encounter any issues, the project structure is complete and ready for development. The main challenge is the Java environment setup, which once resolved, will allow smooth development.

**The foundation is solid! 🎉**
