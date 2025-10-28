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
-- Table `User_Account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User_Account` ;

CREATE TABLE IF NOT EXISTS `User_Account` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(60) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(70) NOT NULL,
  `yearOfStudy` INT NOT NULL,
  `areaOfStudy` VARCHAR(100) NOT NULL,
  `imagePath` VARCHAR(100) NOT NULL,
  `cvPath` VARCHAR(100) NOT NULL,
  `dateOfBirth` DATE NOT NULL,
  `experiencePoints` INT NOT NULL DEFAULT 0,
  `unlockedLevel` INT NOT NULL DEFAULT 1,
  `Higher_Education_Body_id` INT NOT NULL,
  PRIMARY KEY (`id`, `Higher_Education_Body_id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  INDEX `fk_User_Account_Higher_Education_Body_idx` (`Higher_Education_Body_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_Account_Higher_Education_Body`
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
  `User_Account_id` INT NOT NULL,
  `Student_Job_id` INT NOT NULL,
  `applicationDate` DATE NOT NULL,
  PRIMARY KEY (`User_Account_id`, `Student_Job_id`),
  INDEX `fk_User_Account_has_Student_Job_Student_Job1_idx` (`Student_Job_id` ASC) VISIBLE,
  INDEX `fk_User_Account_has_Student_Job_User_Account1_idx` (`User_Account_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_Account_has_Student_Job_User_Account1`
    FOREIGN KEY (`User_Account_id`)
    REFERENCES `User_Account` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_Account_has_Student_Job_Student_Job1`
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
  `User_Account_id` INT NOT NULL,
  `Quiz_id` INT NOT NULL,
  `achievedPoints` INT NOT NULL,
  PRIMARY KEY (`User_Account_id`, `Quiz_id`),
  INDEX `fk_User_Account_has_Quiz_Quiz1_idx` (`Quiz_id` ASC) VISIBLE,
  INDEX `fk_User_Account_has_Quiz_User_Account1_idx` (`User_Account_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_Account_has_Quiz_User_Account1`
    FOREIGN KEY (`User_Account_id`)
    REFERENCES `User_Account` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_Account_has_Quiz_Quiz1`
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
  `User_Account_id` INT NOT NULL,
  PRIMARY KEY (`Survey_id`, `User_Account_id`),
  INDEX `fk_Survey_has_User_Account_User_Account1_idx` (`User_Account_id` ASC) VISIBLE,
  INDEX `fk_Survey_has_User_Account_Survey1_idx` (`Survey_id` ASC) VISIBLE,
  CONSTRAINT `fk_Survey_has_User_Account_Survey1`
    FOREIGN KEY (`Survey_id`)
    REFERENCES `Survey` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Survey_has_User_Account_User_Account1`
    FOREIGN KEY (`User_Account_id`)
    REFERENCES `User_Account` (`id`)
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
  `User_Account_id` INT NOT NULL,
  `Survey_Question_id` INT NOT NULL,
  PRIMARY KEY (`id`, `User_Account_id`, `Survey_Question_id`),
  INDEX `fk_Survey_Answer_User_Account1_idx` (`User_Account_id` ASC) VISIBLE,
  INDEX `fk_Survey_Answer_Survey_Question1_idx` (`Survey_Question_id` ASC) VISIBLE,
  CONSTRAINT `fk_Survey_Answer_User_Account1`
    FOREIGN KEY (`User_Account_id`)
    REFERENCES `User_Account` (`id`)
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
  `User_Account_id` INT NOT NULL,
  PRIMARY KEY (`id`, `User_Account_id`),
  INDEX `fk_Student_Internship_Application_User_Account1_idx` (`User_Account_id` ASC) VISIBLE,
  CONSTRAINT `fk_Student_Internship_Application_User_Account1`
    FOREIGN KEY (`User_Account_id`)
    REFERENCES `User_Account` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `User_Has_Read_Info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User_Has_Read_Info` ;

CREATE TABLE IF NOT EXISTS `User_Has_Read_Info` (
  `User_Account_id` INT NOT NULL,
  `Informational_Content_id` INT NOT NULL,
  PRIMARY KEY (`User_Account_id`, `Informational_Content_id`),
  INDEX `fk_User_Account_has_Informational_Content_Informational_Con_idx` (`Informational_Content_id` ASC) VISIBLE,
  INDEX `fk_User_Account_has_Informational_Content_User_Account1_idx` (`User_Account_id` ASC) VISIBLE,
  CONSTRAINT `fk_User_Account_has_Informational_Content_User_Account1`
    FOREIGN KEY (`User_Account_id`)
    REFERENCES `User_Account` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_Account_has_Informational_Content_Informational_Conte1`
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


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;