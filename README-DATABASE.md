# Database Setup and Testing Guide

## Prerequisites

1. **MySQL Server** must be installed and running
2. **MySQL JDBC Driver** (mysql-connector-java-8.0.x.jar or later) must be in the `lib/` directory

## Database Configuration

Edit `src/application/DatabaseConnection.java` and update:
- `URL`: Database connection string (default: `jdbc:mysql://localhost:3306/car_rental_db`)
- `USER`: MySQL username (default: `root`)
- `PASSWORD`: MySQL password (default: empty string)

## Setup Steps

### 1. Create Database

Run the SQL script to create the database and tables:

```sql
-- Open MySQL command line or MySQL Workbench
-- Run the script: resources/database/schema.sql
```

Or manually:
```sql
CREATE DATABASE car_rental_db;
USE car_rental_db;
-- Then run all CREATE TABLE statements from schema.sql
```

### 2. Download MySQL JDBC Driver

Download `mysql-connector-java-8.0.33.jar` (or later version) from:
- https://dev.mysql.com/downloads/connector/j/

Place it in the `lib/` directory:
```
lib/
  └── mysql-connector-java-8.0.33.jar
```

### 3. Test Database Connection

**Option A: Using DatabaseTest.java**
```bash
# Compile
javac -cp ".;lib/*" src/application/DatabaseTest.java src/application/DatabaseConnection.java

# Run
java -cp "src;lib/*" application.DatabaseTest
```

**Option B: Using the batch file (Windows)**
```bash
test-db-connection.bat
```

**Option C: In Eclipse/IDE**
1. Right-click on `DatabaseTest.java`
2. Run As → Java Application

### 4. Expected Output

If successful, you should see:
```
=== Database Connection Test ===

1. Testing database connection...
✓ Connection successful!

2. Database Information:
   Database: MySQL
   Version: 8.0.x
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

## Troubleshooting

### Connection Failed

1. **MySQL not running**
   ```bash
   # Check MySQL service (Windows)
   services.msc → Find MySQL → Start if stopped
   
   # Check MySQL service (Linux/Mac)
   sudo systemctl status mysql
   ```

2. **Wrong database name**
   - Check `DatabaseConnection.java` - should be `car_rental_db`
   - Or create the database: `CREATE DATABASE car_rental_db;`

3. **Wrong credentials**
   - Update USER and PASSWORD in `DatabaseConnection.java`
   - Test with MySQL command line:
     ```bash
     mysql -u root -p
     ```

4. **JDBC Driver not found**
   - Download MySQL Connector/J
   - Place in `lib/` directory
   - Add to classpath in IDE

5. **Port 3306 blocked**
   - Check firewall settings
   - Verify MySQL is listening on port 3306

### Tables Not Found

Run the schema script:
```sql
SOURCE resources/database/schema.sql;
```

Or manually create tables using the SQL in `resources/database/schema.sql`

## Default Login Credentials

After running schema.sql, you can login with:
- Username: `admin`
- Password: `admin123`

## Application Testing

Once database connection is successful:

1. **Run the application:**
   ```bash
   java -cp "src;lib/*" application.Main
   ```

2. **Login with default credentials:**
   - Username: `admin`
   - Password: `admin123`

3. **Test features:**
   - Dashboard should show statistics
   - Cars section should display sample cars
   - All CRUD operations should work



