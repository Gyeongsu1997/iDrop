DROP DATABASE IF EXISTS idrop;
CREATE DATABASE idrop;

USE idrop;

CREATE TABLE users (
    users_id bigint unsigned AUTO_INCREMENT PRIMARY KEY,
    login_id varchar(50) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,
    name varchar(50) NOT NULL,
    birth_date date NOT NULL,
    gender char(1) NOT NULL CHECK (gender IN ('M', 'F')),
    phone_number varchar(50) NOT NULL,
    image_url varchar(255),
    role char(1) NOT NULL CHECK (role IN ('D', 'P'))
) ENGINE=InnoDB;

CREATE TABLE auth (
    users_id bigint unsigned PRIMARY KEY,
    refresh_token varchar(255),
    fcm_token varchar(255),
    FOREIGN KEY (users_id) REFERENCES users(users_id)
) ENGINE=InnoDB;

CREATE TABLE parent (
    users_id bigint unsigned PRIMARY KEY, -- todo: 컬럼명을 parent_id로 변경
    FOREIGN KEY (users_id) REFERENCES users(users_id)
) ENGINE=InnoDB;

CREATE TABLE child (
    child_id bigint unsigned AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL,
    birth_date date	NOT NULL,
    gender char(1) NOT NULL CHECK (gender IN ('M', 'F')),
    image_url varchar(255),
    parent_id bigint unsigned NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES parent(users_id)
) ENGINE=InnoDB;

CREATE TABLE driver (
    users_id bigint unsigned PRIMARY KEY, -- todo: 컬럼명을 driver_id로 변경
    career varchar(255) NOT NULL,
    introduction varchar(255) NOT NULL,
    FOREIGN KEY (users_id) REFERENCES users(users_id)
) ENGINE=InnoDB;

CREATE TABLE work_location (
    driver_id bigint unsigned PRIMARY KEY,
    latitude double NOT NULL,
    longitude double NOT NULL,
    radius int unsigned NOT NULL,
    point point NOT NULL SRID 4326,
    SPATIAL INDEX(point)
) ENGINE=InnoDB;

CREATE TABLE work_schedule (
    driver_id bigint unsigned NOT NULL,
    day	enum('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') NOT NULL,
    start_time time,
    end_time time,
    PRIMARY KEY (driver_id, day),
    FOREIGN KEY (driver_id) REFERENCES driver(users_id)
) ENGINE=InnoDB;

CREATE TABLE subscription_status (
    status_id tinyint unsigned PRIMARY KEY,
    status_name varchar(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE subscription (
    subscription_id	bigint unsigned	AUTO_INCREMENT PRIMARY KEY,
    request_date datetime NOT NULL,
    response_date datetime,
    start_date date,
    expired_date datetime,
    status_id tinyint unsigned NOT NULL,
    child_id bigint unsigned NOT NULL,
    driver_id bigint unsigned NOT NULL,
    FOREIGN KEY (status_id) REFERENCES subscription_status(status_id),
    FOREIGN KEY (child_id) REFERENCES child(child_id),
    FOREIGN KEY (driver_id) REFERENCES driver(users_id)
) ENGINE=InnoDB;

CREATE TABLE pick_up_location (
    subscription_id	bigint unsigned	PRIMARY KEY,
    start_address varchar(255) NOT NULL,
    start_latitude double NOT NULL,
    start_longitude	double NOT NULL,
    end_address	varchar(255) NOT NULL,
    end_latitude double	NOT NULL,
    end_longitude double NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
) ENGINE=InnoDB;

CREATE TABLE pick_up_schedule (
    subscription_id	bigint unsigned	NOT NULL,
    day	enum('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') NOT NULL,
    start_time time,
    PRIMARY KEY (subscription_id, day),
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
) ENGINE=InnoDB;

CREATE TABLE pick_up_history (
    subscription_id bigint unsigned NOT NULL,
    history_seq smallint unsigned NOT NULL,
    reserved_time datetime NOT NULL,
    start_time datetime,
    start_image varchar(255),
    start_message varchar(255),
    end_time datetime,
    end_image varchar(255),
    end_message varchar(255),
    PRIMARY KEY (subscription_id, history_seq),
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
) ENGINE=InnoDB;



CREATE TABLE notification (
    id bigint unsigned AUTO_INCREMENT PRIMARY KEY,
    pick_up_alarm_time datetime NOT NULL,
    driver_id bigint unsigned NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES driver(users_id)
) ENGINE=InnoDB;

INSERT INTO subscription_status values (1, 'REQUEST');
INSERT INTO subscription_status values (2, 'CANCELED');
INSERT INTO subscription_status values (3, 'PROGRESS');
INSERT INTO subscription_status values (4, 'REJECTED');
INSERT INTO subscription_status values (5, 'EXPIRED');