# Database Connection Test Instructions

## Quick Test Steps

### Step 1: Download MySQL JDBC Driver

1. Download MySQL Connector/J from: https://dev.mysql.com/downloads/connector/j/
2. Extract the JAR file (e.g., `mysql-connector-java-8.0.33.jar`)
3. Place it in the `lib/` folder:
   ```
   CarRentalManagementSystem/
     └── lib/
         └── mysql-connector-java-8.0.33.jar
   ```

### Step 2: Configure Database Connection

Edit `src/application/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/car_rental_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";        // Your MySQL username
private static final String PASSWORD = "";        // Your MySQL password
```

### Step 3: Create Database

1. Open MySQL Command Line or MySQL Workbench
2. Run the SQL script:
   ```sql
   SOURCE C:/Users/ybeka/eclipse-workspace/CarRentalManagementSystem/resources/database/schema.sql;
   ```
   
   Or manually:
   ```sql
   CREATE DATABASE IF NOT EXISTS car_rental_db;
   USE car_rental_db;
   -- Then copy/paste all SQL from schema.sql
   ```

### Step 4: Test Connection

**In Eclipse:**
1. Right-click `src/application/DatabaseTest.java`
2. Run As → Java Application

**From Command Line:**
```bash
# Navigate to project root
cd C:\Users\ybeka\eclipse-workspace\CarRentalManagementSystem

# Compile (make sure MySQL driver is in lib/)
javac -cp "src;lib/*" src/application/DatabaseTest.java src/application/DatabaseConnection.java

# Run
java -cp "src;lib/*" application.DatabaseTest
```

**Or use the batch file:**
```bash
test-db-connection.bat
```

### Step 5: Expected Results

✅ **Success:**
```
=== Database Connection Test ===

1. Testing database connection...
✓ Connection successful!

2. Database Information:
   Database: MySQL
   Version: 8.0.x
   ...

3. Checking tables...
   ✓ Table found: users
   ✓ Table found: cars
   ...

=== All tests passed! ===
```

❌ **If connection fails:**
- Check MySQL service is running
- Verify database name is `car_rental_db`
- Check username/password
- Ensure MySQL JDBC driver is in `lib/` folder

## Test Application

Once database connection works:

1. **Run the application:**
   - In Eclipse: Right-click `Main.java` → Run As → Java Application
   - Or: `java -cp "src;lib/*" application.Main`

2. **Login:**
   - Username: `admin`
   - Password: `admin123`

3. **Test features:**
   - Dashboard should show statistics
   - Navigate to Cars → Display All Cars
   - Should see sample cars from database

## Troubleshooting

### "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
→ MySQL JDBC driver not in classpath. Download and place in `lib/` folder.

### "Access denied for user"
→ Wrong username/password. Update in `DatabaseConnection.java`.

### "Unknown database 'car_rental_db'"
→ Database doesn't exist. Run `schema.sql` to create it.

### "Communications link failure"
→ MySQL server not running. Start MySQL service.



