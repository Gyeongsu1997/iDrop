DROP DATABASE IF EXISTS idrop;
CREATE DATABASE idrop;

USE idrop;

CREATE TABLE users (
    users_id bigint unsigned AUTO_INCREMENT PRIMARY KEY,
    login_id varchar(50) NOT NULL,
    password varchar(255) NOT NULL,
    name varchar(50) NOT NULL,
    birth_date	date NOT NULL,
    gender	char(1)	NOT NULL,
    phone_number varchar(50) NOT NULL,
    image_url varchar(255),
    role char(1) NOT NULL
);

CREATE TABLE auth (
    users_id bigint unsigned PRIMARY KEY,
    refresh_token varchar(255),
    fcm_token varchar(255),
    FOREIGN KEY (users_id) REFERENCES users(users_id)
);

CREATE TABLE parent (
    parent_id bigint unsigned PRIMARY KEY,
    FOREIGN KEY (parent_id) REFERENCES users(users_id)
);

CREATE TABLE child (
    child_id bigint unsigned AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL,
    birth_date date	NOT NULL,
    gender char(1) NOT NULL,
    image_url varchar(255),
    parent_id bigint unsigned NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES parent(parent_id)
);

CREATE TABLE driver (
    driver_id bigint unsigned PRIMARY KEY,
    career varchar(255) NOT NULL,
    introduction varchar(255) NOT NULL,
    star_rate double,
    FOREIGN KEY (driver_id) REFERENCES users(users_id)
);

CREATE TABLE work_hours (
    driver_id bigint unsigned NOT NULL,
    day	char(3)	NOT NULL,
    start_time time,
    end_time time,
    PRIMARY KEY (driver_id, day),
    FOREIGN KEY (driver_id) REFERENCES driver(driver_id)
);

CREATE TABLE subscription (
    subscription_id	bigint unsigned	AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    request_date datetime NOT NULL,
    response_date datetime,
    expired_date datetime,
    child_id bigint unsigned NOT NULL,
    driver_id bigint unsigned NOT NULL,
    FOREIGN KEY (child_id) REFERENCES child(child_id),
    FOREIGN KEY (driver_id) REFERENCES driver(driver_id)
);

CREATE TABLE pick_up_schedule (
    subscription_id	bigint unsigned	NOT NULL,
    day	char(3)	NOT NULL,
    start_time time,
    PRIMARY KEY (subscription_id, day),
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
);

CREATE TABLE pick_up_location (
    subscription_id	bigint unsigned	PRIMARY KEY,
    start_address varchar(255) NOT NULL,
    start_latitude double NOT NULL,
    start_longitude	double NOT NULL,
    end_address	varchar(255) NOT NULL,
    end_latitude double	NOT NULL,
    end_longitude double NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
);

CREATE TABLE pick_up_history (
    subscription_id bigint unsigned NOT NULL,
    history_id smallint unsigned NOT NULL,
    reserved_time datetime NOT NULL,
    start_time datetime,
    start_image varchar(255),
    start_message varchar(255),
    end_time datetime,
    end_image varchar(255),
    end_message varchar(255),
    PRIMARY KEY (subscription_id, history_id),
    FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id)
);



CREATE TABLE notification (
    id bigint unsigned AUTO_INCREMENT PRIMARY KEY,
    pick_up_alarm_time datetime NOT NULL,
    driver_id bigint unsigned NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES driver(driver_id)
);
