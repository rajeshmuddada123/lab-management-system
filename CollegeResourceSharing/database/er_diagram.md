# College Resource Sharing Platform — ER Diagram Documentation

## Database: `college_resource_db`

---

## 1. Entity Overview

| Entity | Table | PK |
|--------|-------|----|
| Role | `roles` | `role_id` |
| User | `users` | `user_id` |
| Student | `students` | `student_id` |
| Faculty | `faculty` | `faculty_id` |
| Laboratory | `laboratories` | `lab_id` |
| Faculty–Lab Assignment | `faculty_lab_assignments` | `assignment_id` |
| Equipment | `equipment` | `equipment_id` |
| Equipment Image | `equipment_images` | `image_id` |
| File Attachment | `file_attachments` | `file_id` |
| Booking | `bookings` | `booking_id` |
| Booking Approval | `booking_approvals` | `approval_id` |
| Waiting List | `waiting_lists` | `waitlist_id` |
| Notification | `notifications` | `notification_id` |
| Collection Record | `collection_records` | `collection_id` |
| Return Record | `return_records` | `return_id` |
| Condition Report | `condition_reports` | `report_id` |
| Damage Report | `damage_reports` | `damage_id` |
| Activity Log | `activity_logs` | `log_id` |
| Usage Rule | `usage_rules` | `rule_id` |

---

## 2. Entity–Relationship Diagram (Text / ASCII)

```
┌──────────┐     1       ┌──────────┐
│  roles   │────────────<│  users   │
└──────────┘             └────┬─────┘
                              │ 1
              ┌───────────────┼───────────────┐
              │               │               │
              ▼ 1             ▼ 1             ▼ N
         ┌─────────┐   ┌──────────┐   ┌──────────────┐
         │students │   │ faculty  │   │notifications │
         └─────────┘   └────┬─────┘   └──────────────┘
                            │ N
                            │
              ┌─────────────┴──────────────┐
              ▼ N                          ▼ N
  ┌───────────────────────┐       ┌──────────────┐
  │ faculty_lab_assignments│       │  (activity   │
  └───────────┬───────────┘       │   _logs)     │
              │ N                 └──────────────┘
              ▼ 1
       ┌─────────────┐
       │ laboratories│
       └──────┬──────┘
              │ 1
              │
              ▼ N
        ┌──────────┐     N    ┌───────────────┐
        │equipment │──────────│ usage_rules   │
        └─────┬────┘  (lab)   └───────────────┘
              │ 1
   ┌──────────┼──────────┬──────────────┐
   ▼ N        ▼ N        ▼ N            ▼ N
┌──────────┐ ┌────────┐ ┌──────────┐ ┌──────────────┐
│equipment │ │booking │ │condition │ │ damage       │
│ _images  │ │  s     │ │ _reports │ │ _reports     │
└──────────┘ └───┬────┘ └──────────┘ └──────────────┘
                 │ 1
     ┌───────────┼────────────┬──────────────┐
     ▼ 0..1      ▼ N          ▼ 0..1         ▼ N
┌──────────┐ ┌──────────┐ ┌──────────────┐ ┌────────────┐
│booking   │ │waiting   │ │collection    │ │ return     │
│_approvals│ │ _lists   │ │ _records     │ │ _records   │
└──────────┘ └──────────┘ └──────────────┘ └────────────┘

file_attachments ← referenced by collection_records, return_records, damage_reports
activity_logs    ← references users (actor)
```

---

## 3. Relationship Cardinalities

| Relationship | Cardinality | Description |
|---|---|---|
| roles → users | 1 : N | One role per user |
| users → students | 1 : 1 | Each student has one user account |
| users → faculty | 1 : 1 | Each faculty has one user account |
| faculty → faculty_lab_assignments | 1 : N | Faculty assigned to multiple labs |
| laboratories → faculty_lab_assignments | 1 : N | Lab has multiple assigned faculty |
| laboratories → equipment | 1 : N | Lab contains multiple equipment items |
| equipment → equipment_images | 1 : N | Multiple images per equipment |
| equipment → bookings | 1 : N | Equipment booked multiple times |
| students → bookings | 1 : N | Student makes multiple bookings |
| bookings → booking_approvals | 1 : 0..1 | Each booking has at most one approval record |
| bookings → collection_records | 1 : 0..1 | Each booking has at most one collection |
| bookings → return_records | 1 : 0..1 | Each booking has at most one return |
| bookings → waiting_lists | 1 : N | Multiple waitlist positions per booking slot |
| equipment → condition_reports | 1 : N | Equipment has many condition checks |
| equipment → damage_reports | 1 : N | Equipment has many damage reports |
| users → notifications | 1 : N | User receives many notifications |
| users → activity_logs | 1 : N | User generates many audit logs |
| laboratories → usage_rules | 1 : N | Lab has multiple usage rules |

---

## 4. Key Attributes per Entity

### `roles`
- `role_id` PK | `role_name` UNIQUE ('ADMIN','STUDENT','FACULTY')

### `users`
- `user_id` PK | `username` UNIQUE | `email` UNIQUE | `password_hash` | `role_id` FK | `status` | `created_at` | `last_login_at`

### `students`
- `student_id` PK | `user_id` FK UNIQUE | `registration_number` UNIQUE | `first_name` | `last_name` | `department` | `semester` | `phone`

### `faculty`
- `faculty_id` PK | `user_id` FK UNIQUE | `faculty_code` UNIQUE | `first_name` | `last_name` | `department` | `designation` | `phone`

### `laboratories`
- `lab_id` PK | `lab_code` UNIQUE | `lab_name` | `location` | `capacity` | `status`

### `faculty_lab_assignments`
- `assignment_id` PK | `faculty_id` FK | `lab_id` FK | `role` ('INCHARGE','ASSISTANT') | `assigned_date` | UNIQUE(faculty_id, lab_id)

### `equipment`
- `equipment_id` PK | `lab_id` FK | `equipment_code` UNIQUE | `name` | `description` | `category` | `quantity_total` | `quantity_available` | `condition_status` | `is_deleted`

### `equipment_images`
- `image_id` PK | `equipment_id` FK | `file_id` FK | `is_primary` | `display_order`

### `file_attachments`
- `file_id` PK | `storage_path` (server-side UUID path) | `original_filename` (display only) | `file_type` | `mime_type` | `file_size_bytes` | `uploaded_by` FK | `created_at`

### `bookings`
- `booking_id` PK | `student_id` FK | `equipment_id` FK | `purpose` | `booking_date` | `start_time` | `end_time` | `status` | `rejection_reason` | `created_at` | `updated_at`

### `booking_approvals`
- `approval_id` PK | `booking_id` FK UNIQUE | `approved_by` FK (faculty user) | `action` ('APPROVED','REJECTED') | `notes` | `actioned_at`

### `waiting_lists`
- `waitlist_id` PK | `equipment_id` FK | `student_id` FK | `booking_date` | `start_time` | `end_time` | `queue_position` | `status` | `created_at`

### `notifications`
- `notification_id` PK | `user_id` FK | `title` | `message` | `type` | `is_read` | `related_booking_id` FK | `created_at`

### `collection_records`
- `collection_id` PK | `booking_id` FK UNIQUE | `collected_by` FK | `collected_at` | `condition_on_collection` | `photo_file_id` FK | `notes`

### `return_records`
- `return_id` PK | `booking_id` FK UNIQUE | `returned_by` FK | `received_by` FK | `returned_at` | `condition_on_return` | `photo_file_id` FK | `notes`

### `condition_reports`
- `report_id` PK | `equipment_id` FK | `reported_by` FK | `condition_status` | `description` | `report_date` | `photo_file_id` FK

### `damage_reports`
- `damage_id` PK | `equipment_id` FK | `booking_id` FK nullable | `reported_by` FK | `severity` | `description` | `repair_cost` | `status` | `photo_file_id` FK | `reported_at` | `resolved_at`

### `activity_logs`
- `log_id` PK | `user_id` FK | `action_type` | `entity_type` | `entity_id` | `description` | `ip_address` | `created_at`

### `usage_rules`
- `rule_id` PK | `lab_id` FK | `max_booking_duration_hours` | `max_advance_booking_days` | `max_concurrent_bookings_per_student` | `allowed_days` | `open_time` | `close_time` | `notes`
