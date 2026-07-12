-- ============================================================
-- Hospital Patient Management System - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS hospital;
USE hospital;

-- ------------------------------------------------------------
-- Table 1: DOCTORS
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id        INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(80)  NOT NULL,
    age              INT,
    experience_years INT,
    department       VARCHAR(50),
    cases_handled    INT,
    salary           DECIMAL(10,2)
);

-- ------------------------------------------------------------
-- Table 2: PATIENTS
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patients (
    patient_id      INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(80)  NOT NULL,
    age             INT,
    address         VARCHAR(150),
    disease         VARCHAR(80),
    allergies       VARCHAR(120),
    insurance       ENUM('Yes','No') DEFAULT 'No',
    doctor_incharge VARCHAR(80),
    department      VARCHAR(50)
);

-- ------------------------------------------------------------
-- Table 3: ADMINISTRATORS  (hospital administrative staff)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS administrators (
    admin_id       INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(80) NOT NULL,
    designation    VARCHAR(60),
    department     VARCHAR(50),
    shift          ENUM('Morning','Evening','Night') DEFAULT 'Morning',
    contact_number VARCHAR(15)
);

-- ------------------------------------------------------------
-- Sample data (so the demo is not empty when you open it)
-- ------------------------------------------------------------
INSERT INTO doctors (name, age, experience_years, department, cases_handled, salary) VALUES
('Dr. Anita Rao',    45, 18, 'Cardiology',  920, 185000.00),
('Dr. Vikram Menon', 38, 11, 'Orthopedics', 540, 142000.00),
('Dr. Sneha Kapoor', 52, 25, 'Neurology',  1310, 210000.00);

INSERT INTO patients (name, age, address, disease, allergies, insurance, doctor_incharge, department) VALUES
('Ravi Sharma',  62, '14 MG Road, Bengaluru',  'Hypertension', 'Penicillin', 'Yes', 'Dr. Anita Rao',    'Cardiology'),
('Meera Nair',   29, '7 Lake View, Kochi',     'Fractured arm','None',       'No',  'Dr. Vikram Menon', 'Orthopedics'),
('Imran Qureshi',47, '3 Park Street, Kolkata', 'Migraine',     'Aspirin',    'Yes', 'Dr. Sneha Kapoor', 'Neurology');

INSERT INTO administrators (name, designation, department, shift, contact_number) VALUES
('Priya Deshmukh', 'Front Desk Manager', 'Reception', 'Morning', '9876543210'),
('Arjun Iyer',     'Billing Officer',    'Accounts',  'Evening', '9812345678'),
('Fatima Sheikh',  'Records Supervisor', 'Medical Records', 'Night', '9900112233');
