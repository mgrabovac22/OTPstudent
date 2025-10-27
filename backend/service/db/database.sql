CREATE DATABASE IF NOT EXISTS otp_student;
USE otp_student;

SET foreign_key_checks = 0;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(45) NOT NULL,
    `lastName` VARCHAR(45) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(256) NOT NULL,
    `studyYear` INT NOT NULL,
    `courseOfStudy` VARCHAR(100) NOT NULL,
    `imagePath` VARCHAR(255),
    `cvPath` VARCHAR(255),
    `birthDate` DATE NOT NULL
);

INSERT INTO `user` (`name`, `lastName`, `email`, `password`, `studyYear`, `courseOfStudy`, `imagePath`, `cvPath`, `birthDate`)
VALUES
('Ana', 'Anić', 'ana@me.com', '$2b$10$jxyZd5pdKQolBvfnJJ7SB.PqPpzZe487G9Go.yZ/O1vKq0CzETZPG', 2, 'Computer Science', '/images/ana.jpg', '/cv/ana.pdf', '2000-01-01'),
('Ivan', 'Ivić', 'ivan@me.com', '$2b$10$jxyZd5pdKQolBvfnJJ7SB.PqPpzZe487G9Go.yZ/O1vKq0CzETZPG', 1, 'Mathematics', '/images/ivan.jpg', '/cv/ivan.pdf', '1999-05-15');

SET foreign_key_checks = 1;
