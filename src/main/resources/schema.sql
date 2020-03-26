CREATE DATABASE IF NOT EXISTS `telegrambots`;
USE `telegrambots`;

CREATE TABLE IF NOT EXISTS `tvseries` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `telegram_id` varchar(20) NOT NULL,
  `time_saved` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY(`username`)
) ENGINE=InnoDB;
