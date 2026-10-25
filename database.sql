DROP DATABASE IF EXISTS ev_charging_station;
CREATE DATABASE ev_charging_station;
USE ev_charging_station;

CREATE TABLE station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    address VARCHAR(200) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE charging_module (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    port_number INT NOT NULL,
    port_type VARCHAR(50) NOT NULL,
    power DOUBLE NOT NULL,
    FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE incident (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    state VARCHAR(30) NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    integral_index DOUBLE NOT NULL,
    FOREIGN KEY (module_id) REFERENCES charging_module(id)
);

CREATE TABLE repair_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    incident_id BIGINT NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    planned_date VARCHAR(30),
    FOREIGN KEY (incident_id) REFERENCES incident(id)
);

INSERT INTO station (name, address, status) VALUES
('Autoenterprise', 'Зіньківська вулиця, 6, Полтава, Полтавська область, 36000', 'АКТИВНА'),
('ECOFACTOR', 'Київське шосе, 41, Полтава, 36040', 'ОБСЛУГОВУВАННЯ');

INSERT INTO charging_module (station_id, port_number, port_type, power) VALUES
(1, 1, 'CCS2', 120.0),
(1, 6, 'CCS', 60.0),
(2, 1, 'CHAdeMO', 50.0);

INSERT INTO incident (module_id, state, f1, f2, f3, integral_index) VALUES
(1, 'REPORTED', 80.0, 60.0, 40.0, 63.0),
(3, 'REPAIR', 30.0, 90.0, 10.0, 46.0);

INSERT INTO repair_task (incident_id, description, planned_date) VALUES
(2, 'Заміна контактора та перевірка силового кабелю CHAdeMO', '02.12.2026');