# Quick Start Guide - Car Rental Management System

## Prerequisites Checklist

- [ ] Java JDK 8 or higher installed
- [ ] JavaFX SDK downloaded and configured
- [ ] MySQL Server installed and running
- [ ] MySQL JDBC Driver downloaded

## Setup Steps

### 1. Download MySQL JDBC Driver

1. Go to: https://dev.mysql.com/downloads/connector/j/
2. Download Platform Independent ZIP
3. Extract `mysql-connector-java-8.0.33.jar` (or latest version)
4. Copy to: `lib/mysql-connector-java-8.0.33.jar`

### 2. Configure Database

1. Open `src/application/DatabaseConnection.java`
2. Update these values:
   ```java
   private static final String USER = "root";        // Your MySQL username
   private static final String PASSWORD = "";        // Your MySQL password
   ```

### 3. Create Database

**Option A: Using MySQL Command Line**
```bash
mysql -u root -p
```
Then:
```sql
SOURCE C:/Users/ybeka/eclipse-workspace/CarRentalManagementSystem/resources/database/schema.sql;
```

**Option B: Using MySQL Workbench**
1. Open MySQL Workbench
2. File → Open SQL Script
3. Select `resources/database/schema.sql`
4. Execute script

### 4. Test Database Connection

Run `DatabaseTest.java`:
- In Eclipse: Right-click → Run As → Java Application
- Or: `java -cp "src;lib/*" application.DatabaseTest`

### 5. Run Application

**In Eclipse:**
1. Right-click `src/application/Main.java`
2. Run As → Java Application

**From Command Line:**
```bash
# Make sure JavaFX is in classpath
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp "src;lib/*" application.Main
```

## Default Login

- **Username:** `admin`
- **Password:** `admin123`

## Project Structure

```
CarRentalManagementSystem/
├── src/
│   ├── application/       # Main, DatabaseConnection, AppNavigator
│   ├── controllers/       # All FXML controllers
│   ├── models/           # Car, Customer, Employee, etc.
│   ├── dao/              # Data Access Objects
│   ├── services/         # Business logic layer
│   └── utils/            # Utility classes
├── resources/
│   ├── fxml/             # All FXML UI files
│   ├── css/              # Stylesheets
│   └── database/         # SQL schema
└── lib/                  # External JARs (MySQL driver, JavaFX)
```

## Features

✅ Login System
✅ Dashboard with Statistics
✅ Cars Management (Add, Edit, Delete, Search, Display)
✅ Customers Management
✅ Employees Management
✅ Rent Car
✅ Return Car

## Next Steps

1. Test database connection
2. Run the application
3. Login with admin credentials
4. Explore all features
5. Customize as needed

## Support

If you encounter issues:
1. Check `TEST-DATABASE.md` for database troubleshooting
2. Verify all prerequisites are installed
3. Check console for error messages
4. Ensure MySQL service is running



