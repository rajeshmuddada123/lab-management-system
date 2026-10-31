-- ============================================================
-- College Resource Sharing Platform
-- Database Schema — schema.sql
-- MySQL 8.0+
-- Run this file FIRST before indexes.sql and sample_data.sql
-- ============================================================

-- ────────────────────────────────────────────────────────────
-- 0. Database Creation
-- ────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS college_resource_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE college_resource_db;

-- Enforce FK checks during the session
SET FOREIGN_KEY_CHECKS = 1;

-- ────────────────────────────────────────────────────────────
-- 1. ROLES
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS roles (
    role_id   TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(20)      NOT NULL,
    created_at TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_roles    PRIMARY KEY (role_id),
    CONSTRAINT uq_roles_name UNIQUE (role_name),
    CONSTRAINT chk_roles_name CHECK (role_name IN ('ADMIN', 'STUDENT', 'FACULTY'))
);

-- ────────────────────────────────────────────────────────────
-- 2. USERS  (authentication & account management)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id       BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)      NOT NULL,
    email         VARCHAR(100)     NOT NULL,
    password_hash VARCHAR(255)     NOT NULL COMMENT 'BCrypt hash — never plain text',
    role_id       TINYINT UNSIGNED NOT NULL,
    status        ENUM('ACTIVE','INACTIVE','SUSPENDED','PENDING') NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP        NULL,
    is_deleted    TINYINT(1)       NOT NULL DEFAULT 0,
    CONSTRAINT pk_users         PRIMARY KEY (user_id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email    UNIQUE (email),
    CONSTRAINT fk_users_role     FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- ────────────────────────────────────────────────────────────
-- 3. STUDENTS  (profile — 1:1 with users)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    student_id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id             BIGINT UNSIGNED NOT NULL,
    registration_number VARCHAR(30)     NOT NULL COMMENT 'College registration / roll number',
    first_name          VARCHAR(60)     NOT NULL,
    last_name           VARCHAR(60)     NOT NULL,
    department          VARCHAR(100)    NOT NULL,
    semester            TINYINT UNSIGNED NOT NULL,
    phone               VARCHAR(20)     NULL,
    address             VARCHAR(255)    NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_students              PRIMARY KEY (student_id),
    CONSTRAINT uq_students_user_id      UNIQUE (user_id),
    CONSTRAINT uq_students_reg_number   UNIQUE (registration_number),
    CONSTRAINT fk_students_user         FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_students_semester    CHECK (semester BETWEEN 1 AND 12)
);

-- ────────────────────────────────────────────────────────────
-- 4. FACULTY  (profile — 1:1 with users)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS faculty (
    faculty_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id      BIGINT UNSIGNED NOT NULL,
    faculty_code VARCHAR(20)     NOT NULL COMMENT 'Unique faculty employee ID',
    first_name   VARCHAR(60)     NOT NULL,
    last_name    VARCHAR(60)     NOT NULL,
    department   VARCHAR(100)    NOT NULL,
    designation  VARCHAR(100)    NOT NULL,
    phone        VARCHAR(20)     NULL,
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_faculty             PRIMARY KEY (faculty_id),
    CONSTRAINT uq_faculty_user_id     UNIQUE (user_id),
    CONSTRAINT uq_faculty_code        UNIQUE (faculty_code),
    CONSTRAINT fk_faculty_user        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ────────────────────────────────────────────────────────────
-- 5. LABORATORIES
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS laboratories (
    lab_id      BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    lab_code    VARCHAR(20)      NOT NULL COMMENT 'Short code e.g. CS-LAB-01',
    lab_name    VARCHAR(150)     NOT NULL,
    location    VARCHAR(200)     NOT NULL COMMENT 'Building / floor / room',
    capacity    SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    status      ENUM('ACTIVE','INACTIVE','UNDER_MAINTENANCE') NOT NULL DEFAULT 'ACTIVE',
    description TEXT             NULL,
    created_at  TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_laboratories       PRIMARY KEY (lab_id),
    CONSTRAINT uq_laboratories_code  UNIQUE (lab_code),
    CONSTRAINT chk_lab_capacity      CHECK (capacity >= 1)
);

-- ────────────────────────────────────────────────────────────
-- 6. FACULTY–LAB ASSIGNMENTS  (M:N bridge)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS faculty_lab_assignments (
    assignment_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    faculty_id    BIGINT UNSIGNED NOT NULL,
    lab_id        BIGINT UNSIGNED NOT NULL,
    assignment_role ENUM('INCHARGE','ASSISTANT') NOT NULL DEFAULT 'ASSISTANT',
    assigned_date DATE            NOT NULL,
    end_date      DATE            NULL COMMENT 'NULL means currently assigned',
    created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_faculty_lab_assignments PRIMARY KEY (assignment_id),
    CONSTRAINT uq_fla_faculty_lab         UNIQUE (faculty_id, lab_id),
    CONSTRAINT fk_fla_faculty             FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE CASCADE,
    CONSTRAINT fk_fla_lab                 FOREIGN KEY (lab_id)     REFERENCES laboratories(lab_id) ON DELETE CASCADE
);

-- ────────────────────────────────────────────────────────────
-- 7. FILE ATTACHMENTS  (secure server-side references)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS file_attachments (
    file_id           BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    storage_path      VARCHAR(500)     NOT NULL COMMENT 'Server-side UUID-based path — never raw user input',
    original_filename VARCHAR(255)     NOT NULL COMMENT 'Display name only, not used for access',
    file_type         ENUM('EQUIPMENT_IMAGE','COLLECTION_PHOTO','RETURN_PHOTO','DAMAGE_PHOTO','OTHER') NOT NULL,
    mime_type         VARCHAR(100)     NOT NULL,
    file_size_bytes   INT UNSIGNED     NOT NULL,
    uploaded_by       BIGINT UNSIGNED  NOT NULL,
    created_at        TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_file_attachments    PRIMARY KEY (file_id),
    CONSTRAINT uq_file_storage_path   UNIQUE (storage_path),
    CONSTRAINT fk_file_uploaded_by    FOREIGN KEY (uploaded_by) REFERENCES users(user_id),
    CONSTRAINT chk_file_size          CHECK (file_size_bytes > 0)
);

-- ────────────────────────────────────────────────────────────
-- 8. EQUIPMENT
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS equipment (
    equipment_id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    lab_id             BIGINT UNSIGNED NOT NULL,
    equipment_code     VARCHAR(30)     NOT NULL COMMENT 'Unique asset tag / barcode',
    name               VARCHAR(200)    NOT NULL,
    description        TEXT            NULL,
    category           VARCHAR(100)    NOT NULL,
    make_model         VARCHAR(200)    NULL,
    serial_number      VARCHAR(100)    NULL,
    purchase_date      DATE            NULL,
    warranty_expiry    DATE            NULL,
    quantity_total     SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    quantity_available SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    condition_status   ENUM('GOOD','FAIR','POOR','UNDER_REPAIR','DECOMMISSIONED') NOT NULL DEFAULT 'GOOD',
    is_deleted         TINYINT(1)      NOT NULL DEFAULT 0,
    created_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_equipment              PRIMARY KEY (equipment_id),
    CONSTRAINT uq_equipment_code         UNIQUE (equipment_code),
    CONSTRAINT fk_equipment_lab          FOREIGN KEY (lab_id) REFERENCES laboratories(lab_id),
    CONSTRAINT chk_equipment_qty_total   CHECK (quantity_total >= 0),
    CONSTRAINT chk_equipment_qty_avail   CHECK (quantity_available >= 0),
    CONSTRAINT chk_equipment_qty_logic   CHECK (quantity_available <= quantity_total)
);

-- ────────────────────────────────────────────────────────────
-- 9. EQUIPMENT IMAGES
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS equipment_images (
    image_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    equipment_id  BIGINT UNSIGNED NOT NULL,
    file_id       BIGINT UNSIGNED NOT NULL,
    is_primary    TINYINT(1)      NOT NULL DEFAULT 0,
    display_order TINYINT UNSIGNED NOT NULL DEFAULT 0,
    created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_equipment_images   PRIMARY KEY (image_id),
    CONSTRAINT uq_equip_img_file     UNIQUE (file_id),
    CONSTRAINT fk_equip_img_equip    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id) ON DELETE CASCADE,
    CONSTRAINT fk_equip_img_file     FOREIGN KEY (file_id)      REFERENCES file_attachments(file_id)
);

-- ────────────────────────────────────────────────────────────
-- 10. BOOKINGS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS bookings (
    booking_id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    student_id       BIGINT UNSIGNED NOT NULL,
    equipment_id     BIGINT UNSIGNED NOT NULL,
    purpose          TEXT            NOT NULL,
    booking_date     DATE            NOT NULL COMMENT 'The date on which equipment is to be used',
    start_time       TIME            NOT NULL,
    end_time         TIME            NOT NULL,
    status           ENUM('PENDING','APPROVED','REJECTED','COLLECTED','RETURNED','CANCELLED','NO_SHOW') NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT            NULL,
    quantity_booked  SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_bookings             PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_student     FOREIGN KEY (student_id)   REFERENCES students(student_id),
    CONSTRAINT fk_bookings_equipment   FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    CONSTRAINT chk_booking_times       CHECK (end_time > start_time),
    CONSTRAINT chk_booking_qty        CHECK (quantity_booked >= 1)
);

-- ────────────────────────────────────────────────────────────
-- 11. BOOKING APPROVALS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS booking_approvals (
    approval_id  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    booking_id   BIGINT UNSIGNED NOT NULL,
    approved_by  BIGINT UNSIGNED NOT NULL COMMENT 'FK → users (FACULTY or ADMIN)',
    action       ENUM('APPROVED','REJECTED') NOT NULL,
    notes        TEXT            NULL,
    actioned_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_booking_approvals   PRIMARY KEY (approval_id),
    CONSTRAINT uq_booking_approval    UNIQUE (booking_id)  COMMENT 'Each booking has one approval record',
    CONSTRAINT fk_ba_booking          FOREIGN KEY (booking_id)  REFERENCES bookings(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_ba_approved_by      FOREIGN KEY (approved_by) REFERENCES users(user_id)
);

-- ────────────────────────────────────────────────────────────
-- 12. WAITING LISTS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS waiting_lists (
    waitlist_id    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    equipment_id   BIGINT UNSIGNED NOT NULL,
    student_id     BIGINT UNSIGNED NOT NULL,
    booking_date   DATE            NOT NULL,
    start_time     TIME            NOT NULL,
    end_time       TIME            NOT NULL,
    queue_position SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    status         ENUM('WAITING','PROMOTED','CANCELLED','EXPIRED') NOT NULL DEFAULT 'WAITING',
    created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_waiting_lists       PRIMARY KEY (waitlist_id),
    CONSTRAINT fk_wl_equipment        FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    CONSTRAINT fk_wl_student          FOREIGN KEY (student_id)   REFERENCES students(student_id),
    CONSTRAINT chk_wl_times           CHECK (end_time > start_time),
    CONSTRAINT chk_wl_queue_pos       CHECK (queue_position >= 1)
);

-- ────────────────────────────────────────────────────────────
-- 13. NOTIFICATIONS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    notification_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id           BIGINT UNSIGNED NOT NULL,
    title             VARCHAR(200)    NOT NULL,
    message           TEXT            NOT NULL,
    type              ENUM('BOOKING_UPDATE','APPROVAL','REMINDER','DAMAGE_ALERT','SYSTEM','WAITLIST') NOT NULL,
    is_read           TINYINT(1)      NOT NULL DEFAULT 0,
    related_booking_id BIGINT UNSIGNED NULL,
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_notifications        PRIMARY KEY (notification_id),
    CONSTRAINT fk_notif_user           FOREIGN KEY (user_id)            REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_notif_booking        FOREIGN KEY (related_booking_id) REFERENCES bookings(booking_id) ON DELETE SET NULL
);

-- ────────────────────────────────────────────────────────────
-- 14. COLLECTION RECORDS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS collection_records (
    collection_id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    booking_id           BIGINT UNSIGNED NOT NULL,
    collected_by         BIGINT UNSIGNED NOT NULL COMMENT 'FK → users (student who collected)',
    verified_by          BIGINT UNSIGNED NULL     COMMENT 'FK → users (faculty who verified)',
    collected_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    condition_on_collection ENUM('GOOD','FAIR','POOR') NOT NULL DEFAULT 'GOOD',
    photo_file_id        BIGINT UNSIGNED NULL,
    notes                TEXT            NULL,
    CONSTRAINT pk_collection_records   PRIMARY KEY (collection_id),
    CONSTRAINT uq_collection_booking   UNIQUE (booking_id) COMMENT 'One collection per booking',
    CONSTRAINT fk_col_booking          FOREIGN KEY (booking_id)    REFERENCES bookings(booking_id),
    CONSTRAINT fk_col_collected_by     FOREIGN KEY (collected_by)  REFERENCES users(user_id),
    CONSTRAINT fk_col_verified_by      FOREIGN KEY (verified_by)   REFERENCES users(user_id),
    CONSTRAINT fk_col_photo            FOREIGN KEY (photo_file_id) REFERENCES file_attachments(file_id)
);

-- ────────────────────────────────────────────────────────────
-- 15. RETURN RECORDS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS return_records (
    return_id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    booking_id         BIGINT UNSIGNED NOT NULL,
    returned_by        BIGINT UNSIGNED NOT NULL COMMENT 'FK → users (student)',
    received_by        BIGINT UNSIGNED NOT NULL COMMENT 'FK → users (faculty/admin)',
    returned_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    condition_on_return ENUM('GOOD','FAIR','POOR','DAMAGED') NOT NULL DEFAULT 'GOOD',
    photo_file_id      BIGINT UNSIGNED NULL,
    notes              TEXT            NULL,
    CONSTRAINT pk_return_records      PRIMARY KEY (return_id),
    CONSTRAINT uq_return_booking      UNIQUE (booking_id) COMMENT 'One return per booking',
    CONSTRAINT fk_ret_booking         FOREIGN KEY (booking_id)    REFERENCES bookings(booking_id),
    CONSTRAINT fk_ret_returned_by     FOREIGN KEY (returned_by)   REFERENCES users(user_id),
    CONSTRAINT fk_ret_received_by     FOREIGN KEY (received_by)   REFERENCES users(user_id),
    CONSTRAINT fk_ret_photo           FOREIGN KEY (photo_file_id) REFERENCES file_attachments(file_id)
);

-- ────────────────────────────────────────────────────────────
-- 16. CONDITION REPORTS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS condition_reports (
    report_id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    equipment_id     BIGINT UNSIGNED NOT NULL,
    reported_by      BIGINT UNSIGNED NOT NULL COMMENT 'FK → users (faculty/admin)',
    condition_status ENUM('GOOD','FAIR','POOR','NEEDS_REPAIR') NOT NULL,
    description      TEXT            NOT NULL,
    report_date      DATE            NOT NULL,
    photo_file_id    BIGINT UNSIGNED NULL,
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_condition_reports   PRIMARY KEY (report_id),
    CONSTRAINT fk_cr_equipment        FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    CONSTRAINT fk_cr_reported_by      FOREIGN KEY (reported_by)  REFERENCES users(user_id),
    CONSTRAINT fk_cr_photo            FOREIGN KEY (photo_file_id) REFERENCES file_attachments(file_id)
);

-- ────────────────────────────────────────────────────────────
-- 17. DAMAGE REPORTS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS damage_reports (
    damage_id    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    equipment_id BIGINT UNSIGNED NOT NULL,
    booking_id   BIGINT UNSIGNED NULL COMMENT 'NULL if damage found outside a booking',
    reported_by  BIGINT UNSIGNED NOT NULL,
    severity     ENUM('MINOR','MODERATE','SEVERE','TOTAL_LOSS') NOT NULL,
    description  TEXT            NOT NULL,
    repair_cost  DECIMAL(10,2)   NULL,
    status       ENUM('REPORTED','UNDER_REVIEW','REPAIR_IN_PROGRESS','RESOLVED','WRITTEN_OFF') NOT NULL DEFAULT 'REPORTED',
    photo_file_id BIGINT UNSIGNED NULL,
    reported_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at  TIMESTAMP       NULL,
    CONSTRAINT pk_damage_reports      PRIMARY KEY (damage_id),
    CONSTRAINT fk_dr_equipment        FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    CONSTRAINT fk_dr_booking          FOREIGN KEY (booking_id)   REFERENCES bookings(booking_id) ON DELETE SET NULL,
    CONSTRAINT fk_dr_reported_by      FOREIGN KEY (reported_by)  REFERENCES users(user_id),
    CONSTRAINT fk_dr_photo            FOREIGN KEY (photo_file_id) REFERENCES file_attachments(file_id),
    CONSTRAINT chk_dr_repair_cost     CHECK (repair_cost IS NULL OR repair_cost >= 0)
);

-- ────────────────────────────────────────────────────────────
-- 18. ACTIVITY LOGS  (audit trail)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS activity_logs (
    log_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NULL COMMENT 'NULL for system-initiated actions',
    action_type VARCHAR(60)     NOT NULL COMMENT 'e.g. BOOKING_CREATED, USER_LOGIN, EQUIPMENT_UPDATED',
    entity_type VARCHAR(60)     NOT NULL COMMENT 'Table/entity affected e.g. bookings, equipment',
    entity_id   BIGINT UNSIGNED NULL,
    description TEXT            NULL,
    ip_address  VARCHAR(45)     NULL COMMENT 'IPv4 or IPv6',
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_activity_logs  PRIMARY KEY (log_id),
    CONSTRAINT fk_al_user        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED;

-- ────────────────────────────────────────────────────────────
-- 19. USAGE RULES  (per-lab booking policies)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usage_rules (
    rule_id                             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    lab_id                              BIGINT UNSIGNED NOT NULL,
    max_booking_duration_hours          TINYINT UNSIGNED NOT NULL DEFAULT 4,
    max_advance_booking_days            TINYINT UNSIGNED NOT NULL DEFAULT 7,
    max_concurrent_bookings_per_student TINYINT UNSIGNED NOT NULL DEFAULT 1,
    allowed_days                        VARCHAR(50)      NOT NULL DEFAULT 'MON,TUE,WED,THU,FRI'
                                                         COMMENT 'Comma-separated day abbreviations',
    open_time                           TIME             NOT NULL DEFAULT '08:00:00',
    close_time                          TIME             NOT NULL DEFAULT '18:00:00',
    notes                               TEXT             NULL,
    created_at                          TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                          TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_usage_rules              PRIMARY KEY (rule_id),
    CONSTRAINT fk_ur_lab                   FOREIGN KEY (lab_id) REFERENCES laboratories(lab_id) ON DELETE CASCADE,
    CONSTRAINT chk_ur_duration             CHECK (max_booking_duration_hours BETWEEN 1 AND 24),
    CONSTRAINT chk_ur_advance_days         CHECK (max_advance_booking_days BETWEEN 1 AND 90),
    CONSTRAINT chk_ur_concurrent           CHECK (max_concurrent_bookings_per_student BETWEEN 1 AND 10),
    CONSTRAINT chk_ur_times                CHECK (close_time > open_time)
);

-- ────────────────────────────────────────────────────────────
-- Seed: Role lookup values (always required before any user)
-- ────────────────────────────────────────────────────────────
INSERT IGNORE INTO roles (role_name) VALUES ('ADMIN'), ('STUDENT'), ('FACULTY');

-- ============================================================
-- End of schema.sql
-- ============================================================
