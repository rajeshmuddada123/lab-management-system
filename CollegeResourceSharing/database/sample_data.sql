-- ============================================================
-- College Resource Sharing Platform
-- Sample Test Data — sample_data.sql
-- MySQL 8.0+
-- Run AFTER schema.sql and indexes.sql
-- FOR TESTING ONLY — do NOT use as production data
-- ============================================================

USE college_resource_db;

SET FOREIGN_KEY_CHECKS = 1;

-- ────────────────────────────────────────────────────────────
-- USERS
-- All sample users share password: Password@123
-- Encoded using Spring BCryptPasswordEncoder(12)
-- ────────────────────────────────────────────────────────────
INSERT INTO users (user_id, username, email, password_hash, role_id, status) VALUES
(1, 'admin',         'admin@college.edu',    '$2a$12$makUihiXG/dY9MPTCdr8EevUWm2OvlxSXcBhQs5EMTokvTkf.0ym2', 1, 'ACTIVE'),
(2, 'student.arun',  'arun.k@college.edu',   '$2a$12$makUihiXG/dY9MPTCdr8EevUWm2OvlxSXcBhQs5EMTokvTkf.0ym2', 2, 'ACTIVE'),
(3, 'student.priya', 'priya.s@college.edu',  '$2a$12$makUihiXG/dY9MPTCdr8EevUWm2OvlxSXcBhQs5EMTokvTkf.0ym2', 2, 'ACTIVE'),
(4, 'faculty.ram',   'ram.v@college.edu',    '$2a$12$makUihiXG/dY9MPTCdr8EevUWm2OvlxSXcBhQs5EMTokvTkf.0ym2', 3, 'ACTIVE'),
(5, 'faculty.suba',  'subalakshmi@college.edu','$2a$12$makUihiXG/dY9MPTCdr8EevUWm2OvlxSXcBhQs5EMTokvTkf.0ym2', 3, 'ACTIVE'),
(6, 'student.rahul', 'rahul.m@college.edu',  '$2a$12$makUihiXG/dY9MPTCdr8EevUWm2OvlxSXcBhQs5EMTokvTkf.0ym2', 2, 'ACTIVE');

-- ────────────────────────────────────────────────────────────
-- STUDENT PROFILES
-- ────────────────────────────────────────────────────────────
INSERT INTO students (student_id, user_id, registration_number, first_name, last_name, department, semester, phone) VALUES
(1, 2, 'CS2024001', 'Arun',  'Kumar',   'Computer Science', 5, '9876543210'),
(2, 3, 'CS2024002', 'Priya', 'Sharma',  'Computer Science', 5, '9876543211'),
(3, 6, 'EC2024003', 'Rahul', 'Mehta',   'Electronics',      3, '9876543212');

-- ────────────────────────────────────────────────────────────
-- FACULTY PROFILES
-- ────────────────────────────────────────────────────────────
INSERT INTO faculty (faculty_id, user_id, faculty_code, first_name, last_name, department, designation, phone) VALUES
(1, 4, 'FAC-CS-001', 'Raman',      'Venkatesh',  'Computer Science', 'Associate Professor', '9988776655'),
(2, 5, 'FAC-CS-002', 'Subalakshmi','Rajagopal',  'Computer Science', 'Assistant Professor', '9988776644');

-- ────────────────────────────────────────────────────────────
-- LABORATORIES
-- ────────────────────────────────────────────────────────────
INSERT INTO laboratories (lab_id, lab_code, lab_name, location, capacity, status) VALUES
(1, 'CS-LAB-01', 'Computer Networks Lab',    'Block A, Floor 2, Room 201', 30, 'ACTIVE'),
(2, 'CS-LAB-02', 'Operating Systems Lab',    'Block A, Floor 2, Room 202', 30, 'ACTIVE'),
(3, 'EC-LAB-01', 'Electronics & Circuits Lab','Block B, Floor 1, Room 101', 25, 'ACTIVE');

-- ────────────────────────────────────────────────────────────
-- FACULTY–LAB ASSIGNMENTS
-- ────────────────────────────────────────────────────────────
INSERT INTO faculty_lab_assignments (faculty_id, lab_id, assignment_role, assigned_date) VALUES
(1, 1, 'INCHARGE',  '2024-07-01'),
(1, 2, 'ASSISTANT', '2024-07-01'),
(2, 2, 'INCHARGE',  '2024-07-01'),
(2, 3, 'ASSISTANT', '2024-07-01');

-- ────────────────────────────────────────────────────────────
-- USAGE RULES (per lab)
-- ────────────────────────────────────────────────────────────
INSERT INTO usage_rules (lab_id, max_booking_duration_hours, max_advance_booking_days, max_concurrent_bookings_per_student, allowed_days, open_time, close_time) VALUES
(1, 3, 7, 1, 'MON,TUE,WED,THU,FRI', '09:00:00', '17:00:00'),
(2, 4, 7, 2, 'MON,TUE,WED,THU,FRI', '09:00:00', '17:00:00'),
(3, 2, 5, 1, 'MON,TUE,WED,THU,FRI,SAT', '08:00:00', '16:00:00');

-- ────────────────────────────────────────────────────────────
-- FILE ATTACHMENTS (dummy secure paths)
-- ────────────────────────────────────────────────────────────
INSERT INTO file_attachments (file_id, storage_path, original_filename, file_type, mime_type, file_size_bytes, uploaded_by) VALUES
(1, 'uploads/equipment/3f2a1b4c-router-main.jpg',    'cisco_router_photo.jpg',   'EQUIPMENT_IMAGE',  'image/jpeg', 204800, 1),
(2, 'uploads/equipment/7d8e9f0a-switch-main.jpg',    'cisco_switch_photo.jpg',   'EQUIPMENT_IMAGE',  'image/jpeg', 153600, 1),
(3, 'uploads/collections/c1d2e3f4-collection1.jpg',  'collection_booking1.jpg',  'COLLECTION_PHOTO', 'image/jpeg', 102400, 2),
(4, 'uploads/returns/a5b6c7d8-return1.jpg',          'return_booking1.jpg',      'RETURN_PHOTO',     'image/jpeg', 98304,  2),
(5, 'uploads/damage/e9f0a1b2-damage1.jpg',           'damage_switch1.jpg',       'DAMAGE_PHOTO',     'image/jpeg', 131072, 4);

-- ────────────────────────────────────────────────────────────
-- EQUIPMENT
-- ────────────────────────────────────────────────────────────
INSERT INTO equipment (equipment_id, lab_id, equipment_code, name, description, category, make_model, serial_number, purchase_date, quantity_total, quantity_available, condition_status) VALUES
(1, 1, 'EQ-NET-001', 'Cisco Router 2911',      'Cisco ISR 2911 Series Router for network experiments',  'Networking', 'Cisco 2911',  'FTX1234ABC', '2022-06-15', 5, 4, 'GOOD'),
(2, 1, 'EQ-NET-002', 'Cisco Switch 2960',      'Cisco Catalyst 2960 Layer-2 Switch',                    'Networking', 'Cisco 2960',  'FTX5678DEF', '2022-06-15', 8, 7, 'GOOD'),
(3, 1, 'EQ-NET-003', 'RJ-45 Crimping Kit',     'Network cable crimping tool with connectors',           'Tools',      'Generic Kit', NULL,         '2023-01-10', 10, 10, 'GOOD'),
(4, 2, 'EQ-OS-001',  'Raspberry Pi 4 Model B', '4GB RAM Raspberry Pi for OS experiments',               'SBC',        'RPi 4B 4GB',  NULL,         '2023-03-01', 15, 13, 'GOOD'),
(5, 3, 'EQ-EC-001',  'Digital Oscilloscope',   '100MHz Digital Storage Oscilloscope',                   'Instruments','RIGOL DS1054Z','DS1Z234567', '2021-11-20', 4,  4, 'FAIR');

-- ────────────────────────────────────────────────────────────
-- EQUIPMENT IMAGES
-- ────────────────────────────────────────────────────────────
INSERT INTO equipment_images (equipment_id, file_id, is_primary, display_order) VALUES
(1, 1, 1, 1),
(2, 2, 1, 1);

-- ────────────────────────────────────────────────────────────
-- BOOKINGS  (various statuses for test coverage)
-- ────────────────────────────────────────────────────────────
INSERT INTO bookings (booking_id, student_id, equipment_id, purpose, booking_date, start_time, end_time, status, quantity_booked) VALUES
(1, 1, 1, 'Semester practical: BGP routing experiment',        '2026-11-05', '10:00:00', '12:00:00', 'APPROVED',   1),
(2, 2, 2, 'Assignment: VLAN configuration lab',                '2026-11-05', '13:00:00', '15:00:00', 'PENDING',    1),
(3, 1, 4, 'Mini project: Linux kernel module development',     '2026-11-06', '09:00:00', '11:00:00', 'RETURNED',   2),
(4, 3, 5, 'EC lab: Signal analysis experiment',                '2026-11-07', '10:00:00', '12:00:00', 'APPROVED',   1),
(5, 2, 1, 'Network troubleshooting project',                   '2026-11-10', '14:00:00', '16:00:00', 'CANCELLED',  1),
(6, 3, 2, 'Switch configuration for capstone project',         '2026-11-05', '09:00:00', '10:00:00', 'REJECTED',   1);

-- ────────────────────────────────────────────────────────────
-- BOOKING APPROVALS
-- ────────────────────────────────────────────────────────────
INSERT INTO booking_approvals (booking_id, approved_by, action, notes) VALUES
(1, 4, 'APPROVED', 'Approved for BGP routing experiment. Ensure router is returned with config reset.'),
(3, 4, 'APPROVED', 'Approved. Two units allocated.'),
(4, 5, 'APPROVED', 'Approved for signal analysis.'),
(6, 4, 'REJECTED', 'Duplicate booking detected — student already has an approved booking in this slot.');

-- ────────────────────────────────────────────────────────────
-- COLLECTION RECORDS
-- ────────────────────────────────────────────────────────────
INSERT INTO collection_records (booking_id, collected_by, verified_by, collected_at, condition_on_collection, photo_file_id, notes) VALUES
(3, 2, 4, '2026-11-06 09:10:00', 'GOOD', 3, 'Both units collected in good condition.');

-- ────────────────────────────────────────────────────────────
-- RETURN RECORDS
-- ────────────────────────────────────────────────────────────
INSERT INTO return_records (booking_id, returned_by, received_by, returned_at, condition_on_return, photo_file_id, notes) VALUES
(3, 2, 4, '2026-11-06 11:05:00', 'GOOD', 4, 'Returned in good condition. No issues.');

-- ────────────────────────────────────────────────────────────
-- WAITING LIST
-- ────────────────────────────────────────────────────────────
INSERT INTO waiting_lists (equipment_id, student_id, booking_date, start_time, end_time, queue_position, status) VALUES
(1, 3, '2026-11-05', '10:00:00', '12:00:00', 1, 'WAITING'),
(1, 2, '2026-11-05', '10:00:00', '12:00:00', 2, 'WAITING');

-- ────────────────────────────────────────────────────────────
-- CONDITION REPORTS
-- ────────────────────────────────────────────────────────────
INSERT INTO condition_reports (equipment_id, reported_by, condition_status, description, report_date) VALUES
(5, 5, 'FAIR',        'Oscilloscope probe tip worn slightly, measurement accuracy adequate.', '2026-10-01'),
(2, 4, 'GOOD',        'All 8 switches inspected and functional. Ports clean.', '2026-10-15');

-- ────────────────────────────────────────────────────────────
-- DAMAGE REPORTS
-- ────────────────────────────────────────────────────────────
INSERT INTO damage_reports (equipment_id, booking_id, reported_by, severity, description, status, photo_file_id) VALUES
(2, NULL, 4, 'MINOR', 'Port 7 on unit EQ-NET-002 not linking at 1Gbps, downgrades to 100Mbps.', 'UNDER_REVIEW', 5);

-- ────────────────────────────────────────────────────────────
-- NOTIFICATIONS
-- ────────────────────────────────────────────────────────────
INSERT INTO notifications (user_id, title, message, type, is_read, related_booking_id) VALUES
(2, 'Booking Approved',    'Your booking for Cisco Router 2911 on 05-Nov has been approved.',      'APPROVAL',       0, 1),
(3, 'Booking on Waitlist', 'You are #1 in the waitlist for Cisco Router 2911 on 05-Nov.',          'WAITLIST',       0, NULL),
(2, 'Booking Rejected',    'Your booking #6 for Cisco Switch 2960 was rejected. Reason: Duplicate.','BOOKING_UPDATE', 1, 6),
(4, 'New Booking Request', 'Student Priya Sharma has submitted a new booking request #2.',          'BOOKING_UPDATE', 0, 2);

-- ────────────────────────────────────────────────────────────
-- ACTIVITY LOGS
-- ────────────────────────────────────────────────────────────
INSERT INTO activity_logs (user_id, action_type, entity_type, entity_id, description, ip_address) VALUES
(1, 'USER_CREATED',      'users',     2, 'Admin created student account: student.arun',   '192.168.1.10'),
(1, 'USER_CREATED',      'users',     3, 'Admin created student account: student.priya',  '192.168.1.10'),
(1, 'EQUIPMENT_CREATED', 'equipment', 1, 'Admin added equipment: Cisco Router 2911',       '192.168.1.10'),
(2, 'BOOKING_CREATED',   'bookings',  1, 'Student created booking for Cisco Router 2911', '192.168.1.55'),
(4, 'BOOKING_APPROVED',  'bookings',  1, 'Faculty approved booking #1',                   '192.168.1.22'),
(2, 'USER_LOGIN',        'users',     2, 'Student login: student.arun',                   '192.168.1.55'),
(4, 'BOOKING_REJECTED',  'bookings',  6, 'Faculty rejected booking #6: Duplicate booking', '192.168.1.22');

-- ============================================================
-- ── CONSTRAINT VIOLATION TESTS ─────────────────────────────
-- These statements are expected to FAIL.
-- Uncomment each block to test constraint enforcement.
-- ============================================================

-- TEST 1: Duplicate registration number → should fail with Duplicate entry
-- INSERT INTO students (user_id, registration_number, first_name, last_name, department, semester)
-- VALUES (4, 'CS2024001', 'Duplicate', 'Student', 'CS', 1);

-- TEST 2: Duplicate username → should fail
-- INSERT INTO users (username, email, password_hash, role_id, status)
-- VALUES ('admin', 'other@college.edu', 'hash', 1, 'ACTIVE');

-- TEST 3: FK violation — booking with non-existent student → should fail
-- INSERT INTO bookings (student_id, equipment_id, purpose, booking_date, start_time, end_time, status, quantity_booked)
-- VALUES (9999, 1, 'Test', '2026-12-01', '10:00:00', '12:00:00', 'PENDING', 1);

-- TEST 4: CHECK violation — end_time before start_time → should fail
-- INSERT INTO bookings (student_id, equipment_id, purpose, booking_date, start_time, end_time, status, quantity_booked)
-- VALUES (1, 1, 'Bad times', '2026-12-01', '14:00:00', '10:00:00', 'PENDING', 1);

-- TEST 5: Invalid status enum → should fail
-- INSERT INTO users (username, email, password_hash, role_id, status)
-- VALUES ('bad.user', 'bad@college.edu', 'hash', 2, 'UNKNOWN_STATUS');

-- TEST 6: quantity_available > quantity_total → should fail
-- INSERT INTO equipment (lab_id, equipment_code, name, category, quantity_total, quantity_available, condition_status)
-- VALUES (1, 'EQ-TEST-BAD', 'Bad Qty', 'Tools', 2, 5, 'GOOD');

-- TEST 7: Duplicate booking approval → should fail (UNIQUE on booking_id)
-- INSERT INTO booking_approvals (booking_id, approved_by, action, notes)
-- VALUES (1, 4, 'REJECTED', 'Duplicate approval attempt');

-- ============================================================
-- End of sample_data.sql
-- ============================================================
