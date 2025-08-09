-- College Event Management System Database Schema
-- Database: college_event_management
-- ANSI SQL Standard Compatible

-- Create Database (MySQL specific, comment out for other databases)
-- CREATE DATABASE IF NOT EXISTS college_event_management 
-- CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE college_event_management;

-- Drop tables if they exist (for fresh setup)
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS event_registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS club_members;
DROP TABLE IF EXISTS clubs;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CLUB_HEAD', 'STUDENT') NOT NULL,
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_department (department),
    INDEX idx_users_active (is_active)
);

-- Create clubs table
CREATE TABLE clubs (
    club_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    head_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    logo_url VARCHAR(500),
    contact_email VARCHAR(255),
    
    FOREIGN KEY (head_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_clubs_name (club_name),
    INDEX idx_clubs_head (head_id),
    INDEX idx_clubs_active (is_active)
);

-- Create club_members table
CREATE TABLE club_members (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    member_role ENUM('MEMBER', 'MODERATOR', 'VICE_HEAD') DEFAULT 'MEMBER',
    
    FOREIGN KEY (club_id) REFERENCES clubs(club_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_club_user (club_id, user_id),
    INDEX idx_club_members_club (club_id),
    INDEX idx_club_members_user (user_id),
    INDEX idx_club_members_active (is_active)
);

-- Create events table
CREATE TABLE events (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    venue VARCHAR(255) NOT NULL,
    max_participants INT,
    remaining_spots INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    event_image_url VARCHAR(500),
    registration_deadline TIMESTAMP,
    event_status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    
    FOREIGN KEY (club_id) REFERENCES clubs(club_id) ON DELETE CASCADE,
    INDEX idx_events_club (club_id),
    INDEX idx_events_date (event_date),
    INDEX idx_events_status (event_status),
    INDEX idx_events_active (is_active),
    INDEX idx_events_upcoming (event_date, is_active)
);

-- Create event_registrations table
CREATE TABLE event_registrations (
    reg_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('REGISTERED', 'CANCELLED', 'ATTENDED', 'NO_SHOW') DEFAULT 'REGISTERED',
    notes TEXT,
    
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_event_user (event_id, user_id),
    INDEX idx_registrations_event (event_id),
    INDEX idx_registrations_user (user_id),
    INDEX idx_registrations_status (status),
    INDEX idx_registrations_date (registration_date)
);

-- Create notifications table
CREATE TABLE notifications (
    notif_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    message TEXT NOT NULL,
    date_sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_status BOOLEAN DEFAULT FALSE,
    notification_type ENUM('EVENT_CREATED', 'EVENT_UPDATED', 'EVENT_CANCELLED', 'EVENT_REMINDER', 
                          'REGISTRATION_CONFIRMED', 'REGISTRATION_CANCELLED', 'CLUB_INVITATION', 
                          'GENERAL_ANNOUNCEMENT', 'SYSTEM_NOTIFICATION') NOT NULL,
    related_entity_id BIGINT,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (read_status),
    INDEX idx_notifications_type (notification_type),
    INDEX idx_notifications_date (date_sent)
);

-- Insert sample data

-- Insert admin user (password: admin123)
INSERT INTO users (name, email, password, role, department) VALUES 
('Admin User', 'admin@college.edu', '$2a$10$eImiTXuWVxfM37uY4JANjOL4uFOi6L4C8.P8PvVGqhjhKzWE8LKXy', 'ADMIN', 'Administration'),
('John Smith', 'john.smith@college.edu', '$2a$10$eImiTXuWVxfM37uY4JANjOL4uFOi6L4C8.P8PvVGqhjhKzWE8LKXy', 'CLUB_HEAD', 'Computer Science'),
('Jane Doe', 'jane.doe@college.edu', '$2a$10$eImiTXuWVxfM37uY4JANjOL4uFOi6L4C8.P8PvVGqhjhKzWE8LKXy', 'STUDENT', 'Computer Science'),
('Bob Johnson', 'bob.johnson@college.edu', '$2a$10$eImiTXuWVxfM37uY4JANjOL4uFOi6L4C8.P8PvVGqhjhKzWE8LKXy', 'CLUB_HEAD', 'Electronics'),
('Alice Brown', 'alice.brown@college.edu', '$2a$10$eImiTXuWVxfM37uY4JANjOL4uFOi6L4C8.P8PvVGqhjhKzWE8LKXy', 'STUDENT', 'Electronics');

-- Insert sample clubs
INSERT INTO clubs (club_name, description, head_id, contact_email) VALUES 
('Programming Club', 'A club for programming enthusiasts to learn, share, and build together', 2, 'programming@college.edu'),
('Robotics Club', 'Building and programming robots for competitions and learning', 4, 'robotics@college.edu'),
('Literary Society', 'For students who love reading, writing, and literature discussions', NULL, 'literary@college.edu');

-- Insert club members
INSERT INTO club_members (club_id, user_id, member_role) VALUES 
(1, 2, 'MEMBER'), -- John Smith in Programming Club (he's also the head)
(1, 3, 'MEMBER'), -- Jane Doe in Programming Club
(2, 4, 'MEMBER'), -- Bob Johnson in Robotics Club (he's also the head)
(2, 5, 'MEMBER'), -- Alice Brown in Robotics Club
(3, 3, 'MEMBER'), -- Jane Doe in Literary Society
(3, 5, 'MEMBER'); -- Alice Brown in Literary Society

-- Insert sample events
INSERT INTO events (club_id, event_name, description, event_date, venue, max_participants, remaining_spots, registration_deadline) VALUES 
(1, 'Java Workshop', 'Learn Java programming fundamentals and build your first application', '2025-08-15 14:00:00', 'Computer Lab 1', 30, 30, '2025-08-14 23:59:59'),
(1, 'Hackathon 2025', '24-hour coding competition with exciting prizes', '2025-08-20 09:00:00', 'Main Auditorium', 100, 100, '2025-08-18 23:59:59'),
(2, 'Robot Building Workshop', 'Hands-on workshop to build and program your first robot', '2025-08-17 10:00:00', 'Robotics Lab', 20, 20, '2025-08-16 23:59:59'),
(3, 'Poetry Reading Session', 'Share your poems and listen to others in a cozy environment', '2025-08-19 16:00:00', 'Library Hall', 50, 50, '2025-08-18 23:59:59');

-- Insert sample notifications
INSERT INTO notifications (user_id, title, message, notification_type, related_entity_id) VALUES 
(3, 'Welcome to Programming Club!', 'You have successfully joined the Programming Club. Check out upcoming events!', 'CLUB_INVITATION', 1),
(5, 'New Event: Robot Building Workshop', 'Robotics Club has organized a new workshop. Register now!', 'EVENT_CREATED', 3),
(3, 'Event Reminder', 'Java Workshop is tomorrow at 2 PM. Don\'t forget to attend!', 'EVENT_REMINDER', 1),
(5, 'Welcome to Literary Society!', 'You have successfully joined the Literary Society. Explore our events!', 'CLUB_INVITATION', 3);

-- Create views for common queries

-- View for active events with club information
CREATE VIEW active_events_view AS
SELECT 
    e.event_id,
    e.event_name,
    e.description,
    e.event_date,
    e.venue,
    e.max_participants,
    e.remaining_spots,
    e.event_status,
    c.club_name,
    c.club_id,
    u.name as club_head_name
FROM events e
JOIN clubs c ON e.club_id = c.club_id
LEFT JOIN users u ON c.head_id = u.user_id
WHERE e.is_active = TRUE
ORDER BY e.event_date ASC;

-- View for club statistics
CREATE VIEW club_stats_view AS
SELECT 
    c.club_id,
    c.club_name,
    c.description,
    u.name as head_name,
    COUNT(DISTINCT cm.user_id) as member_count,
    COUNT(DISTINCT e.event_id) as event_count,
    COUNT(DISTINCT CASE WHEN e.event_status = 'UPCOMING' THEN e.event_id END) as upcoming_events
FROM clubs c
LEFT JOIN users u ON c.head_id = u.user_id
LEFT JOIN club_members cm ON c.club_id = cm.club_id AND cm.is_active = TRUE
LEFT JOIN events e ON c.club_id = e.club_id AND e.is_active = TRUE
WHERE c.is_active = TRUE
GROUP BY c.club_id, c.club_name, c.description, u.name;

-- View for user participation statistics
CREATE VIEW user_participation_view AS
SELECT 
    u.user_id,
    u.name,
    u.email,
    u.role,
    u.department,
    COUNT(DISTINCT cm.club_id) as clubs_joined,
    COUNT(DISTINCT er.event_id) as events_registered,
    COUNT(DISTINCT CASE WHEN er.status = 'ATTENDED' THEN er.event_id END) as events_attended
FROM users u
LEFT JOIN club_members cm ON u.user_id = cm.user_id AND cm.is_active = TRUE
LEFT JOIN event_registrations er ON u.user_id = er.user_id
WHERE u.is_active = TRUE
GROUP BY u.user_id, u.name, u.email, u.role, u.department;

COMMIT;
