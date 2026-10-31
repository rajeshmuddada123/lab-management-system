-- ============================================================
-- College Resource Sharing Platform
-- Indexes — indexes.sql
-- MySQL 8.0+
-- Run AFTER schema.sql
-- ============================================================

USE college_resource_db;

-- ────────────────────────────────────────────────────────────
-- users
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_users_role_id    ON users(role_id);
CREATE INDEX idx_users_status     ON users(status);
CREATE INDEX idx_users_is_deleted ON users(is_deleted);

-- ────────────────────────────────────────────────────────────
-- students
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_students_department ON students(department);
CREATE INDEX idx_students_user_id    ON students(user_id);      -- covers FK lookup

-- ────────────────────────────────────────────────────────────
-- faculty
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_faculty_department ON faculty(department);
CREATE INDEX idx_faculty_user_id    ON faculty(user_id);

-- ────────────────────────────────────────────────────────────
-- laboratories
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_labs_status ON laboratories(status);

-- ────────────────────────────────────────────────────────────
-- faculty_lab_assignments
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_fla_lab_id     ON faculty_lab_assignments(lab_id);
CREATE INDEX idx_fla_faculty_id ON faculty_lab_assignments(faculty_id);

-- ────────────────────────────────────────────────────────────
-- equipment
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_equip_lab_id          ON equipment(lab_id);
CREATE INDEX idx_equip_category        ON equipment(category);
CREATE INDEX idx_equip_condition       ON equipment(condition_status);
CREATE INDEX idx_equip_is_deleted      ON equipment(is_deleted);
CREATE INDEX idx_equip_qty_available   ON equipment(quantity_available);
-- Full-text search on equipment name and description
CREATE FULLTEXT INDEX ft_equip_name_desc ON equipment(name, description);

-- ────────────────────────────────────────────────────────────
-- bookings  — critical for conflict detection
-- ────────────────────────────────────────────────────────────
-- Booking conflict check: equipment on a given date + overlapping time range
CREATE INDEX idx_bookings_equip_date_time
    ON bookings(equipment_id, booking_date, start_time, end_time);

CREATE INDEX idx_bookings_student_id  ON bookings(student_id);
CREATE INDEX idx_bookings_status      ON bookings(status);
CREATE INDEX idx_bookings_date        ON bookings(booking_date);

-- ────────────────────────────────────────────────────────────
-- booking_approvals
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_ba_approved_by ON booking_approvals(approved_by);
CREATE INDEX idx_ba_actioned_at ON booking_approvals(actioned_at);

-- ────────────────────────────────────────────────────────────
-- waiting_lists
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_wl_equipment_date ON waiting_lists(equipment_id, booking_date);
CREATE INDEX idx_wl_student_id     ON waiting_lists(student_id);
CREATE INDEX idx_wl_status         ON waiting_lists(status);

-- ────────────────────────────────────────────────────────────
-- notifications
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_notif_user_id    ON notifications(user_id);
CREATE INDEX idx_notif_is_read    ON notifications(is_read);
CREATE INDEX idx_notif_created_at ON notifications(created_at);

-- ────────────────────────────────────────────────────────────
-- collection_records & return_records
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_col_collected_at ON collection_records(collected_at);
CREATE INDEX idx_ret_returned_at  ON return_records(returned_at);

-- ────────────────────────────────────────────────────────────
-- condition_reports & damage_reports
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_cr_equipment_id ON condition_reports(equipment_id);
CREATE INDEX idx_cr_report_date  ON condition_reports(report_date);
CREATE INDEX idx_dr_equipment_id ON damage_reports(equipment_id);
CREATE INDEX idx_dr_status       ON damage_reports(status);
CREATE INDEX idx_dr_reported_at  ON damage_reports(reported_at);

-- ────────────────────────────────────────────────────────────
-- activity_logs  — most frequently queried by user and date
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_al_user_id     ON activity_logs(user_id);
CREATE INDEX idx_al_action_type ON activity_logs(action_type);
CREATE INDEX idx_al_entity      ON activity_logs(entity_type, entity_id);
CREATE INDEX idx_al_created_at  ON activity_logs(created_at);

-- ────────────────────────────────────────────────────────────
-- file_attachments
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_fa_file_type     ON file_attachments(file_type);
CREATE INDEX idx_fa_uploaded_by   ON file_attachments(uploaded_by);

-- ============================================================
-- End of indexes.sql
-- ============================================================
