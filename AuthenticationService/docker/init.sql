CREATE DATABASE IF NOT EXISTS auth;
USE auth;

CREATE TABLE key_value
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    key_   VARCHAR(255) not null,
    value_ VARCHAR(255) not null
);