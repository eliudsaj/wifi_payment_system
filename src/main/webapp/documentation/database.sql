-- Database: wifi_payment_system
CREATE DATABASE wifi_payment_system;
USE wifi_payment_system;

-- Table to store product categories
CREATE TABLE product_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Table to store different quota options and associated prices
CREATE TABLE quota (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    price DECIMAL(10, 2) NOT NULL,
    quota_limit VARCHAR(100) NOT NULL,
    color VARCHAR(20) DEFAULT 'gray',
    FOREIGN KEY (category_id) REFERENCES product_category(id)
);

-- Table for storing payment settings and configurations
CREATE TABLE payment_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    server VARCHAR(255) NOT NULL,
    lnmoapi VARCHAR(255),
    shortcode VARCHAR(20),
    paybill TINYINT(1) DEFAULT 0,
    paybill_no VARCHAR(20)
);

-- Insert sample data for product categories
INSERT INTO product_category (name, description) VALUES 
('Data Package', 'Various data quota options available for purchase');

-- Insert sample data for quota options
INSERT INTO quota (category_id, price, quota_limit, color) VALUES 
(1, 50.00, '500MB', 'green'),
(1, 100.00, '1GB', 'blue'),
(1, 200.00, '2GB', 'yellow');

-- Insert sample data for payment settings
INSERT INTO payment_settings (title, server, lnmoapi, shortcode, paybill, paybill_no) VALUES
('WiFi Service', 'https://api.wifiserver.com', 'https://api.mpesa.com/lnmo', '123456', 1, '567890');
