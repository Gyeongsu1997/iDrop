DROP DATABASE IF EXISTS idrop;
CREATE DATABASE idrop;

USE idrop;

CREATE TABLE users (
    users_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    gender CHAR(1) NOT NULL CHECK (gender IN ('M', 'F')),
    phone_number VARCHAR(50) NOT NULL,
    image_url VARCHAR(255),
    role CHAR(1) NOT NULL CHECK (role IN ('D', 'P'))
) ENGINE=InnoDB;

CREATE TABLE auth (
    users_id BIGINT UNSIGNED PRIMARY KEY,
    refresh_token VARCHAR(255),
    fcm_token VARCHAR(255),
    FOREIGN KEY (users_id) REFERENCES users(users_id)
) ENGINE=InnoDB;

CREATE TABLE parent (
    users_id BIGINT UNSIGNED PRIMARY KEY, -- todo: 컬럼명을 parent_id로 변경
    FOREIGN KEY (users_id) REFERENCES users(users_id)
) ENGINE=InnoDB;

CREATE TABLE child (
    child_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    birth_date DATE	NOT NULL,
    gender CHAR(1) NOT NULL CHECK (gender IN ('M', 'F')),
    image_url VARCHAR(255),
    parent_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES parent(users_id)
) ENGINE=InnoDB;

CREATE TABLE driver (
    users_id BIGINT UNSIGNED PRIMARY KEY, -- todo: 컬럼명을 driver_id로 변경
    career VARCHAR(255) NOT NULL,
    introduction VARCHAR(255) NOT NULL,
    FOREIGN KEY (users_id) REFERENCES users(users_id)
) ENGINE=InnoDB;

CREATE TABLE work_location (
    driver_id BIGINT UNSIGNED PRIMARY KEY,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    radius INT UNSIGNED NOT NULL,
    point POINT NOT NULL SRID 4326,
    FOREIGN KEY (driver_id) REFERENCES driver(users_id)
) ENGINE=InnoDB;

CREATE TABLE work_schedule (
    driver_id BIGINT UNSIGNED NOT NULL,
    day	ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') NOT NULL,
    start_time TIME,
    end_time TIME,
    PRIMARY KEY (driver_id, day),
    FOREIGN KEY (driver_id) REFERENCES driver(users_id)
) ENGINE=InnoDB;

CREATE TABLE subscription_status (
    status_id TINYINT UNSIGNED PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE subscription (
    subscription_id	BIGINT UNSIGNED	AUTO_INCREMENT PRIMARY KEY,
    request_date DATETIME NOT NULL,
    response_date DATETIME,
    start_date DATE,
    expired_date DATETIME,
    status_id TINYINT UNSIGNED NOT NULL,
    child_id BIGINT UNSIGNED NOT NULL,
    driver_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (status_id) REFERENCES subscription_status(status_id),
    FOREIGN KEY (child_id) REFERENCES child(child_id),
    FOREIGN KEY (driver_id) REFERENCES driver(users_id)
) ENGINE=InnoDB;

CREATE TABLE pick_up_location (
    subscription_id	BIGINT UNSIGNED	PRIMARY KEY,
    start_address VARCHAR(255) NOT NULL,
    start_detailed_address VARCHAR(255),
    start_latitude DOUBLE NOT NULL,
    start_longitude	DOUBLE NOT NULL,
    goal_address VARCHAR(255) NOT NULL,
    goal_detailed_address VARCHAR(255),
    goal_latitude DOUBLE	NOT NULL,
    goal_longitude DOUBLE NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
) ENGINE=InnoDB;

CREATE TABLE pick_up_schedule (
    subscription_id	BIGINT UNSIGNED	NOT NULL,
    day	ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') NOT NULL,
    start_time TIME,
    PRIMARY KEY (subscription_id, day),
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
) ENGINE=InnoDB;

CREATE TABLE pick_up_history (
    subscription_id BIGINT UNSIGNED NOT NULL,
    history_seq SMALLINT unsigned NOT NULL,
    reserved_time DATETIME NOT NULL,
    start_time DATETIME,
    start_image VARCHAR(255),
    start_message VARCHAR(255),
    end_time DATETIME,
    end_image VARCHAR(255),
    end_message VARCHAR(255),
    PRIMARY KEY (subscription_id, history_seq),
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
) ENGINE=InnoDB;



CREATE TABLE notification (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pick_up_alarm_time DATETIME NOT NULL,
    driver_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES driver(users_id)
) ENGINE=InnoDB;

INSERT INTO subscription_status VALUES (1, 'REQUEST');
INSERT INTO subscription_status VALUES (2, 'CANCELED');
INSERT INTO subscription_status VALUES (3, 'PROGRESS');
INSERT INTO subscription_status VALUES (4, 'REJECTED');
INSERT INTO subscription_status VALUES (5, 'EXPIRED');