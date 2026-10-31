# College Resource Sharing Platform — Database Setup Instructions

## Prerequisites

| Requirement | Minimum Version |
|---|---|
| MySQL Server | 8.0+ |
| MySQL Client / Workbench | Any recent version |
| Java JDK | 17+ |
| Spring Boot | 3.x |
| Maven / Gradle | (matches your project) |

---

## Step 1 — Install MySQL (if not already installed)

### Windows
1. Download MySQL Installer from https://dev.mysql.com/downloads/installer/
2. Choose **MySQL Server 8.0** during setup.
3. Set a root password (remember it — you'll need it below).
4. Start the MySQL Windows Service.

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo mysql_secure_installation
```

---

## Step 2 — Create the Database User (recommended)

Log in as root:
```bash
mysql -u root -p
```

Then run:
```sql
-- Create a dedicated application user (do NOT use root in production)
CREATE USER 'college_app'@'localhost' IDENTIFIED BY 'StrongPassword@123';
GRANT ALL PRIVILEGES ON college_resource_db.* TO 'college_app'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

> **Security note**: Replace `StrongPassword@123` with a strong, unique password and store it in environment variables or a secrets manager — never hardcode in source files.

---

## Step 3 — Run the SQL Scripts (in order)

```bash
# 1. Create schema (database + all tables + role seed)
mysql -u college_app -p < schema.sql

# 2. Create indexes
mysql -u college_app -p college_resource_db < indexes.sql

# 3. (Optional) Load test data
mysql -u college_app -p college_resource_db < sample_data.sql
```

Or using MySQL Workbench:
- Open each `.sql` file → click **Execute (lightning bolt)** — in order: `schema.sql` → `indexes.sql` → `sample_data.sql`

---

## Step 4 — Verify the Installation

```sql
USE college_resource_db;

-- List all tables (should show 19 tables)
SHOW TABLES;

-- Verify foreign keys
SELECT TABLE_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE CONSTRAINT_SCHEMA = 'college_resource_db'
  AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME;

-- Spot-check sample data
SELECT u.username, r.role_name, u.status FROM users u JOIN roles r ON u.role_id = r.role_id;
SELECT * FROM students;
SELECT * FROM bookings;
```

---

## Step 5 — Spring Boot Configuration

### `src/main/resources/application.properties`

```properties
# ── DataSource ────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/college_resource_db?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
spring.datasource.username=college_app
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ── Hibernate / JPA ───────────────────────────────────────
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=validate
# Set to 'update' during development; 'validate' or 'none' in production
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ── Connection Pool (HikariCP) ────────────────────────────
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

> **Best practice**: Set `DB_PASSWORD` as an environment variable, not in `application.properties`.
> On Windows: `$env:DB_PASSWORD = "StrongPassword@123"` before running the app.

### `pom.xml` dependency (if not already present)
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## Step 6 — Test Spring Boot Connection

Start the Spring Boot app:
```bash
./mvnw spring-boot:run
```

Expected log output (no errors):
```
HikariPool-1 - Starting...
HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@...
Hibernate: show create table users
```

---

## Step 7 — Booking Conflict Query (Application Layer)

Use this query in your `BookingRepository` to detect time-slot conflicts before inserting a new booking:

```sql
SELECT COUNT(*) FROM bookings
WHERE equipment_id = :equipmentId
  AND booking_date = :bookingDate
  AND status NOT IN ('CANCELLED', 'REJECTED', 'NO_SHOW')
  AND (
      (start_time < :endTime AND end_time > :startTime)
  );
```

If `COUNT(*) > 0` → conflict exists → add student to `waiting_lists`.

---

## Resetting the Database (Development Only)

```sql
DROP DATABASE IF EXISTS college_resource_db;
-- Then re-run: schema.sql → indexes.sql → sample_data.sql
```

---

## Table Summary

| Table | Rows in sample_data.sql |
|---|---|
| roles | 3 |
| users | 6 |
| students | 3 |
| faculty | 2 |
| laboratories | 3 |
| faculty_lab_assignments | 4 |
| usage_rules | 3 |
| file_attachments | 5 |
| equipment | 5 |
| equipment_images | 2 |
| bookings | 6 |
| booking_approvals | 4 |
| collection_records | 1 |
| return_records | 1 |
| waiting_lists | 2 |
| condition_reports | 2 |
| damage_reports | 1 |
| notifications | 4 |
| activity_logs | 7 |
