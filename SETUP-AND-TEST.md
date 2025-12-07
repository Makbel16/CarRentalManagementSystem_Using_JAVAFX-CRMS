# Complete Setup and Testing Guide

## 🚀 Quick Setup (5 Steps)

### Step 1: Download MySQL JDBC Driver ⬇️

1. Visit: https://dev.mysql.com/downloads/connector/j/
2. Select "Platform Independent"
3. Download ZIP file
4. Extract `mysql-connector-java-8.0.33.jar` (or latest)
5. **Copy to:** `lib/mysql-connector-java-8.0.33.jar`

**Your lib folder should look like:**
```
lib/
  └── mysql-connector-java-8.0.33.jar
```

### Step 2: Configure Database Connection ⚙️

Edit `src/application/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/car_rental_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";        // ← Change if different
private static final String PASSWORD = "";        // ← Enter your MySQL password
```

### Step 3: Create Database 🗄️

**Using MySQL Command Line:**
```bash
mysql -u root -p
```
Then execute:
```sql
SOURCE C:/Users/ybeka/eclipse-workspace/CarRentalManagementSystem/resources/database/schema.sql;
```

**Or using MySQL Workbench:**
1. Open MySQL Workbench
2. File → Open SQL Script
3. Navigate to: `resources/database/schema.sql`
4. Click Execute (⚡)

### Step 4: Test Database Connection ✅

**In Eclipse:**
1. Right-click `src/application/DatabaseTest.java`
2. Run As → Java Application

**From Command Line:**
```bash
# Compile
javac -cp "src;lib/*" src/application/DatabaseTest.java src/application/DatabaseConnection.java

# Run
java -cp "src;lib/*" application.DatabaseTest
```

**Expected Output:**
```
=== Database Connection Test ===

1. Testing database connection...
✓ Connection successful!

2. Database Information:
   Database: MySQL
   Version: 8.0.33
   Driver: MySQL Connector/J
   URL: jdbc:mysql://localhost:3306/car_rental_db

3. Checking tables...
   ✓ Table found: users
   ✓ Table found: cars
   ✓ Table found: customers
   ✓ Table found: employees
   ✓ Table found: rental_records
   ✓ Table found: return_records
   Total tables: 6

4. Testing queries...
   ✓ Users table: 1 records
   ✓ Cars table: 3 records
   ✓ Customers table: 2 records
   ✓ Employees table: 2 records

=== All tests passed! ===
```

### Step 5: Run Application 🎯

**In Eclipse:**
1. Right-click `src/application/Main.java`
2. Run As → Java Application

**Login Credentials:**
- Username: `admin`
- Password: `admin123`

---

## 🔧 Troubleshooting

### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Solution:**
1. Download MySQL Connector/J (see Step 1)
2. Place JAR in `lib/` folder
3. In Eclipse: Right-click project → Properties → Java Build Path → Libraries → Add JARs → Select `lib/mysql-connector-java-8.0.33.jar`

### Error: "Access denied for user 'root'@'localhost'"

**Solution:**
1. Check MySQL username/password in `DatabaseConnection.java`
2. Test with MySQL command line:
   ```bash
   mysql -u root -p
   ```
3. If password is wrong, update `PASSWORD` in `DatabaseConnection.java`

### Error: "Unknown database 'car_rental_db'"

**Solution:**
1. Create database:
   ```sql
   CREATE DATABASE car_rental_db;
   ```
2. Run schema.sql to create tables
3. Or manually run all SQL from `resources/database/schema.sql`

### Error: "Communications link failure"

**Solution:**
1. Check if MySQL service is running:
   - Windows: Services → MySQL → Start
   - Linux: `sudo systemctl start mysql`
2. Verify MySQL is listening on port 3306
3. Check firewall settings

### Error: "Table 'users' doesn't exist"

**Solution:**
Run the schema.sql script to create all tables:
```sql
USE car_rental_db;
SOURCE resources/database/schema.sql;
```

---

## 📋 Verification Checklist

Before running the application, verify:

- [ ] MySQL server is running
- [ ] MySQL JDBC driver is in `lib/` folder
- [ ] Database `car_rental_db` exists
- [ ] All tables are created (users, cars, customers, employees, rental_records, return_records)
- [ ] DatabaseConnection.java has correct credentials
- [ ] DatabaseTest.java runs successfully

---

## 🎮 Testing the Application

Once database connection works:

1. **Run Main.java**
2. **Login Screen:**
   - Username: `admin`
   - Password: `admin123`
3. **Dashboard:**
   - Should show statistics
   - Click cards to view details
4. **Cars Section:**
   - Click "Cars" in sidebar
   - Click "Display All Cars"
   - Should show 3 sample cars
5. **Add Car:**
   - Fill form and submit
   - Should add to database
6. **Test other features:**
   - Customers, Employees, Rent, Return

---

## 📊 Database Schema

The database includes:
- **users** - Login credentials
- **cars** - Vehicle information
- **customers** - Customer details
- **employees** - Staff information
- **rental_records** - Active rentals
- **return_records** - Return history

Sample data is included in schema.sql for testing.

---

## 💡 Tips

1. **Keep MySQL running** while testing
2. **Check console** for error messages
3. **Use MySQL Workbench** to verify data
4. **Backup database** before making changes
5. **Read error messages** - they usually tell you what's wrong

---

## ✅ Success Indicators

You'll know everything is working when:
- ✅ DatabaseTest.java runs without errors
- ✅ Application starts and shows login screen
- ✅ Login works with admin/admin123
- ✅ Dashboard shows statistics
- ✅ Cars section displays data
- ✅ You can add/edit/delete records

Good luck! 🚀



