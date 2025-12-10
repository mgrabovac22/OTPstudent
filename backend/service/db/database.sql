SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema otp_student
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema otp_student
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `otp_student` DEFAULT CHARACTER SET utf8 ;
USE `otp_student` ;

-- -----------------------------------------------------
-- Table `Higher_Education_Body`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Higher_Education_Body` ;

CREATE TABLE IF NOT EXISTS `Higher_Education_Body` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User` ;

CREATE TABLE IF NOT EXISTS `User` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(60) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(70) NOT NULL,
  `yearOfStudy` INT NOT NULL,
  `areaOfStudy` VARCHAR(100) NOT NULL,
  `imagePath` VARCHAR(100),
  `cvPath` VARCHAR(100),
  `dateOfBirth` DATE NOT NULL,
  `experiencePoints` INT NOT NULL DEFAULT 0,
  `unlockedLevel` INT NOT NULL DEFAULT 1,
  `Higher_Education_Body_id` INT NOT NULL,
  PRIMARY KEY (`id`, `Higher_Education_Body_id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  INDEX `fk_User_Higher_Education_Body_idx` (`Higher_Education_Body_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_Higher_Education_Body`
    FOREIGN KEY (`Higher_Education_Body_id`)
    REFERENCES `Higher_Education_Body` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Student_Internship_Job`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Student_Internship_Job` ;

CREATE TABLE IF NOT EXISTS `Student_Internship_Job` (
  `id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Location`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Location` ;

CREATE TABLE IF NOT EXISTS `Location` (
  `id` INT NOT NULL,
  `address` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Student_Job`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Student_Job` ;

CREATE TABLE IF NOT EXISTS `Student_Job` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(1000) NOT NULL,
  `startDate` DATE NOT NULL,
  `Location_id` INT NOT NULL,
  PRIMARY KEY (`id`, `Location_id`),
  INDEX `fk_Student_Job_Location1_idx` (`Location_id` ASC) VISIBLE,
  CONSTRAINT `fk_Student_Job_Location1`
    FOREIGN KEY (`Location_id`)
    REFERENCES `Location` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Job_Application`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Job_Application` ;

CREATE TABLE IF NOT EXISTS `Job_Application` (
  `User_id` INT NOT NULL,
  `Student_Job_id` INT NOT NULL,
  `applicationDate` DATE NOT NULL,
  PRIMARY KEY (`User_id`, `Student_Job_id`),
  INDEX `fk_User_has_Student_Job_Student_Job1_idx` (`Student_Job_id` ASC) VISIBLE,
  INDEX `fk_User_has_Student_Job_User1_idx` (`User_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_has_Student_Job_User1`
    FOREIGN KEY (`User_id`)
    REFERENCES `User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_Student_Job_Student_Job1`
    FOREIGN KEY (`Student_Job_id`)
    REFERENCES `Student_Job` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Quiz`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Quiz` ;

CREATE TABLE IF NOT EXISTS `Quiz` (
  `id` INT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `imageUrl` VARCHAR(100) NOT NULL,
  `description` VARCHAR(200) NOT NULL,
  `bonusExperience` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Quiz_Question`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Quiz_Question` ;

CREATE TABLE IF NOT EXISTS `Quiz_Question` (
  `id` INT NOT NULL,
  `questionText` VARCHAR(100) NOT NULL,
  `experience` INT NOT NULL,
  `Quiz_id` INT NOT NULL,
  PRIMARY KEY (`id`, `Quiz_id`),
  INDEX `fk_Quiz_Question_Quiz1_idx` (`Quiz_id` ASC) VISIBLE,
  CONSTRAINT `fk_Quiz_Question_Quiz1`
    FOREIGN KEY (`Quiz_id`)
    REFERENCES `Quiz` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Quiz_Answer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Quiz_Answer` ;

CREATE TABLE IF NOT EXISTS `Quiz_Answer` (
  `id` INT NOT NULL,
  `answerText` VARCHAR(100) NOT NULL,
  `isCorrect` TINYINT NOT NULL,
  `Quiz_Question_id` INT NOT NULL,
  PRIMARY KEY (`id`, `Quiz_Question_id`),
  INDEX `fk_Quiz_Answer_Quiz_Question1_idx` (`Quiz_Question_id` ASC) VISIBLE,
  CONSTRAINT `fk_Quiz_Answer_Quiz_Question1`
    FOREIGN KEY (`Quiz_Question_id`)
    REFERENCES `Quiz_Question` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Survey`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Survey` ;

CREATE TABLE IF NOT EXISTS `Survey` (
  `id` INT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(200) NOT NULL,
  `experiencePoints` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `User_Has_Completed_Quiz`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User_Has_Completed_Quiz` ;

CREATE TABLE IF NOT EXISTS `User_Has_Completed_Quiz` (
  `User_id` INT NOT NULL,
  `Quiz_id` INT NOT NULL,
  `achievedPoints` INT NOT NULL,
  PRIMARY KEY (`User_id`, `Quiz_id`),
  INDEX `fk_User_has_Quiz_Quiz1_idx` (`Quiz_id` ASC) VISIBLE,
  INDEX `fk_User_has_Quiz_User1_idx` (`User_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_has_Quiz_User1`
    FOREIGN KEY (`User_id`)
    REFERENCES `User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_Quiz_Quiz1`
    FOREIGN KEY (`Quiz_id`)
    REFERENCES `Quiz` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `User_Has_Completed_Survey`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User_Has_Completed_Survey` ;

CREATE TABLE IF NOT EXISTS `User_Has_Completed_Survey` (
  `Survey_id` INT NOT NULL,
  `User_id` INT NOT NULL,
  PRIMARY KEY (`Survey_id`, `User_id`),
  INDEX `fk_Survey_has_User_User1_idx` (`User_id` ASC) VISIBLE,
  INDEX `fk_Survey_has_User_Survey1_idx` (`Survey_id` ASC) VISIBLE,
  CONSTRAINT `fk_Survey_has_User_Survey1`
    FOREIGN KEY (`Survey_id`)
    REFERENCES `Survey` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Survey_has_User_User1`
    FOREIGN KEY (`User_id`)
    REFERENCES `User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Survey_Question`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Survey_Question` ;

CREATE TABLE IF NOT EXISTS `Survey_Question` (
  `id` INT NOT NULL,
  `questionText` VARCHAR(100) NOT NULL,
  `questionType` VARCHAR(45) NOT NULL,
  `Survey_id` INT NOT NULL,
  PRIMARY KEY (`id`, `Survey_id`),
  INDEX `fk_Survey_Question_Survey1_idx` (`Survey_id` ASC) VISIBLE,
  CONSTRAINT `fk_Survey_Question_Survey1`
    FOREIGN KEY (`Survey_id`)
    REFERENCES `Survey` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Survey_Answer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Survey_Answer` ;

CREATE TABLE IF NOT EXISTS `Survey_Answer` (
  `id` INT NOT NULL,
  `answerValue` VARCHAR(500) NOT NULL,
  `User_id` INT NOT NULL,
  `Survey_Question_id` INT NOT NULL,
  PRIMARY KEY (`id`, `User_id`, `Survey_Question_id`),
  INDEX `fk_Survey_Answer_User1_idx` (`User_id` ASC) VISIBLE,
  INDEX `fk_Survey_Answer_Survey_Question1_idx` (`Survey_Question_id` ASC) VISIBLE,
  CONSTRAINT `fk_Survey_Answer_User1`
    FOREIGN KEY (`User_id`)
    REFERENCES `User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Survey_Answer_Survey_Question1`
    FOREIGN KEY (`Survey_Question_id`)
    REFERENCES `Survey_Question` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Informational_Content`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Informational_Content` ;

CREATE TABLE IF NOT EXISTS `Informational_Content` (
  `id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `experiencePoints` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Student_Internship_Application`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Student_Internship_Application` ;

CREATE TABLE IF NOT EXISTS `Student_Internship_Application` (
  `id` INT NOT NULL,
  `studentExpectations` VARCHAR(500) NOT NULL,
  `studentAdress` VARCHAR(100) NOT NULL,
  `contactNumber` VARCHAR(30) NOT NULL,
  `dateOfApplication` DATE NOT NULL,
  `duration` INT NOT NULL,
  `expectedStartDate` DATE NOT NULL,
  `expectedEndDate` DATE NOT NULL,
  `User_id` INT NOT NULL,
  PRIMARY KEY (`id`, `User_id`),
  INDEX `fk_Student_Internship_Application_User1_idx` (`User_id` ASC) VISIBLE,
  CONSTRAINT `fk_Student_Internship_Application_User1`
    FOREIGN KEY (`User_id`)
    REFERENCES `User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `User_Has_Read_Info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User_Has_Read_Info` ;

CREATE TABLE IF NOT EXISTS `User_Has_Read_Info` (
  `User_id` INT NOT NULL,
  `Informational_Content_id` INT NOT NULL,
  PRIMARY KEY (`User_id`, `Informational_Content_id`),
  INDEX `fk_User_has_Informational_Content_Informational_Con_idx` (`Informational_Content_id` ASC) VISIBLE,
  INDEX `fk_User_has_Informational_Content_User1_idx` (`User_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_has_Informational_Content_User1`
    FOREIGN KEY (`User_id`)
    REFERENCES `User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_Informational_Content_Informational_Conte1`
    FOREIGN KEY (`Informational_Content_id`)
    REFERENCES `Informational_Content` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Mentor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Mentor` ;

CREATE TABLE IF NOT EXISTS `Mentor` (
  `id` INT NOT NULL,
  `firstName` VARCHAR(60) NOT NULL,
  `lastName` VARCHAR(60) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `contactNumber` VARCHAR(30) NOT NULL,
  `description` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Student_Internship_Application_Expected_Job`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Student_Internship_Application_Expected_Job` ;

CREATE TABLE IF NOT EXISTS `Student_Internship_Application_Expected_Job` (
  `Student_Internship_Application_id` INT NOT NULL,
  `Student_Internship_Job_id` INT NOT NULL,
  PRIMARY KEY (`Student_Internship_Application_id`, `Student_Internship_Job_id`),
  INDEX `fk_Student_Internship_Application_has_Student_Internship_Jo_idx` (`Student_Internship_Job_id` ASC) VISIBLE,
  INDEX `fk_Student_Internship_Application_has_Student_Internship_Jo_idx1` (`Student_Internship_Application_id` ASC) VISIBLE,
  CONSTRAINT `fk_Student_Internship_Application_has_Student_Internship_Job_1`
    FOREIGN KEY (`Student_Internship_Application_id`)
    REFERENCES `Student_Internship_Application` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Student_Internship_Application_has_Student_Internship_Job_2`
    FOREIGN KEY (`Student_Internship_Job_id`)
    REFERENCES `Student_Internship_Job` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Postavljanje podataka za tablice bez vanjskih ključeva
-- -----------------------------------------------------

-- Higher_Education_Body (Visokoškolska Ustanova)
INSERT INTO `Higher_Education_Body` (`id`, `name`) VALUES
(1, 'Sveučilište u Zagrebu, FER'),
(2, 'Veleučilište u Rijeci'),
(3, 'Ekonomski Fakultet Split'),
(4, 'Fakultet organizacije i informatike Varaždin');

-- Student_Internship_Job (Tipovi poslova/praksi koje student očekuje)
INSERT INTO `Student_Internship_Job` (`id`, `name`) VALUES
(1, 'Razvoj web aplikacija'),
(2, 'Analiza podataka'),
(3, 'Digitalni marketing'),
(4, 'Razvoj mobilnih aplikacija'),
(5, 'Financije i računovodstvo'),
(6, 'Projektni menadžment'),
(7, 'Korisnička podrška');

-- Location (Lokacije poslova)
INSERT INTO `Location` (`id`, `address`, `city`) VALUES
(101, 'Ulica kneza Branimira 29, Zagreb', 'Zagreb'),
(102, 'Riječka ulica 44, Rijeka', 'Rijeka'),
(103, 'Trg slobode 1, Osijek', 'Osijek'),
(104, 'Ulica Ivana Gundulića 36, Split', 'Split');

-- Student_Job (Trenutne ponude za studentske poslove/prakse)
INSERT INTO `Student_Job` (`id`, `name`, `description`, `startDate`, `Location_id`) VALUES
(1, 'Mlađi Android Developer', 'Tražimo studenta za rad na razvoju nove mobilne aplikacije. Poželjno poznavanje Kotlina.', '2025-01-15', 101),
(2, 'Pomoćnik u računovodstvu', 'Praksa u trajanju od 3 mjeseca. Potrebno osnovno znanje MS Excela i računovodstva.', '2025-02-01', 102),
(3, 'Asistent u prodaji', 'Rad u dinamičnom okruženju, podrška prodajnom timu i komunikacija s klijentima.', '2025-03-01', 103);

-- Quiz (Kvizovi za skupljanje bodova)
INSERT INTO `Quiz` (`id`, `name`, `imageUrl`, `description`, `bonusExperience`) VALUES
(1, 'Osnove HTML-a i CSS-a', 'quiz_web_url', 'Provjerite svoje znanje web tehnologija.', 25),
(2, 'Financijska pismenost', 'quiz_fin_url', 'Pitanja o osobnom budžetiranju i ulaganjima.', 30);

-- Quiz_Question
INSERT INTO `Quiz_Question` (`id`, `questionText`, `experience`, `Quiz_id`) VALUES
(10, 'Koji HTML tag služi za najveći naslov?', 5, 1),
(11, 'Koje CSS svojstvo mijenja boju teksta?', 7, 1),
(20, 'Što je neto plaća?', 10, 2);

-- Quiz_Answer
INSERT INTO `Quiz_Answer` (`id`, `answerText`, `isCorrect`, `Quiz_Question_id`) VALUES
(100, '<h1>', 1, 10),
(101, '<p>', 0, 10),
(110, 'color', 1, 11),
(111, 'background-color', 0, 11),
(200, 'Iznos koji radnik dobiva nakon odbitaka (porezi, doprinosi).', 1, 20),
(201, 'Ukupni iznos plaće prije svih odbitaka.', 0, 20);

-- Survey (Ankete)
INSERT INTO `Survey` (`id`, `name`, `description`, `experiencePoints`) VALUES
(1, 'Anketa o zadovoljstvu platformom', 'Vaše mišljenje je važno za unaprjeđenje platforme.', 15),
(2, 'Profesionalne ambicije', 'Istraživanje željenih karijera studenata.', 20);

-- Survey_Question
INSERT INTO `Survey_Question` (`id`, `questionText`, `questionType`, `Survey_id`) VALUES
(100, 'Kako biste ocijenili korisničko iskustvo (1-5)?', 'Rating', 1),
(101, 'Koju novu funkcionalnost biste željeli vidjeti?', 'Text', 1),
(200, 'Koji sektor (IT/Financije/Marketing) vas najviše zanima za budući posao?', 'Multiple Choice', 2);

-- Informational_Content (Edukativni sadržaj)
INSERT INTO `Informational_Content` (`id`, `name`, `description`, `experiencePoints`) VALUES
(1, 'Kako se pripremiti za intervju?', 'Savjeti za uspješno predstavljanje poslodavcu.', '10'),
(2, 'Osnove izrade portfolija', 'Vodič za studente tehničkih smjerova.', '15');

-- Mentor
INSERT INTO `Mentor` (`id`, `firstName`, `lastName`, `email`, `contactNumber`, `description`) VALUES
(1, 'Iva', 'Horvat', 'iva.horvat@mentor.hr', '091-123-4567', 'Specijalist za razvoj karijere i tehničku rekrutaciju.'),
(2, 'Marko', 'Perić', 'marko.peric@mentor.hr', '098-765-4321', 'Iskusni projekt menadžer s fokusom na digitalnu transformaciju.');

-- -----------------------------------------------------
-- Postavljanje podataka za tablice s vanjskim ključevima
-- -----------------------------------------------------

-- User (id=1 i id=2)
-- Napomena: Polje 'password' koristi fikcionalni hash
INSERT INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `yearOfStudy`, `areaOfStudy`, `imagePath`, `cvPath`, `dateOfBirth`, `experiencePoints`, `unlockedLevel`, `Higher_Education_Body_id`) VALUES
(1, 'Ivan', 'Marić', 'ivan.maric@student.hr', '$2b$10$jxyZd5pdKQolBvfnJJ7SB.PqPpzZe487G9Go.yZ/O1vKq0CzETZPG', 3, 'Računarstvo', '/images/ivan.jpg', '/cv/ivan.pdf', '2001-05-10', 50, 2, 1),
(2, 'Petra', 'Jurić', 'petra.juric@student.hr', '$2b$10$jxyZd5pdKQolBvfnJJ7SB.PqPpzZe487G9Go.yZ/O1vKq0CzETZPG', 2, 'Ekonomija', '/images/petra.jpg', '/cv/petra.pdf', '2002-11-20', 10, 1, 3);

-- Job_Application (Ivan se prijavljuje na Student_Job o id=1)
INSERT INTO `Job_Application` (`User_id`, `Student_Job_id`, `applicationDate`) VALUES
(1, 1, '2024-12-01');

-- User_Has_Completed_Quiz (Ivan je ispunio Quiz 1)
INSERT INTO `User_Has_Completed_Quiz` (`User_id`, `Quiz_id`, `achievedPoints`) VALUES
(1, 1, 12);

-- User_Has_Completed_Survey (Petra je ispunila Survey 1)
INSERT INTO `User_Has_Completed_Survey` (`Survey_id`, `User_id`) VALUES
(1, 2);

-- Survey_Answer (Petrini odgovori na Survey 1)
INSERT INTO `Survey_Answer` (`id`, `answerValue`, `User_id`, `Survey_Question_id`) VALUES
(1, '4', 2, 100), -- Ocjena 4/5
(2, 'Bolja integracija sa sustavom prijava poslova.', 2, 101); -- Prijedlog

-- Student_Internship_Application (Ivan podnosi prijavu za studentsku praksu)
INSERT INTO `Student_Internship_Application` (`id`, `studentExpectations`, `studentAdress`, `contactNumber`, `dateOfApplication`, `duration`, `expectedStartDate`, `expectedEndDate`, `User_id`) VALUES
(1, 'Želim steći iskustvo u radu s bazama podataka i back-endom.', 'Ulica lipa 5, Zagreb', '091-500-1111', '2024-11-15', 6, '2025-02-01', '2025-08-01', 1);

-- User_Has_Read_Info (Petra je pročitala Informational_Content 2)
INSERT INTO `User_Has_Read_Info` (`User_id`, `Informational_Content_id`) VALUES
(2, 2);

-- Student_Internship_Application_Expected_Job (Ivan očekuje razvoj web ili mobilnih aplikacija)
INSERT INTO `Student_Internship_Application_Expected_Job` (`Student_Internship_Application_id`, `Student_Internship_Job_id`) VALUES
(1, 1), -- Razvoj web aplikacija (Frontend)
(1, 4); -- Programiranje mobilnih aplikacija

-- -----------------------------------------------------
-- Resetiranje postavki sesije
-- -----------------------------------------------------
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;