SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `cordis` ;
CREATE SCHEMA IF NOT EXISTS `cordis` DEFAULT CHARACTER SET utf8 ;
USE `cordis` ;

-- -----------------------------------------------------
-- Table `cordis`.`Author`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cordis`.`Author` ;

CREATE  TABLE IF NOT EXISTS `cordis`.`Author` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cordis`.`Publication`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cordis`.`Publication` ;

CREATE  TABLE IF NOT EXISTS `cordis`.`Publication` (
  `id` VARCHAR(32) NOT NULL ,
  `title` VARCHAR(1023) NOT NULL ,
  `url` VARCHAR(1023) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cordis`.`Authoring`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cordis`.`Authoring` ;

CREATE  TABLE IF NOT EXISTS `cordis`.`Authoring` (
  `author_id` INT NOT NULL ,
  `publication_id` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`author_id`, `publication_id`) ,
  INDEX `authoring_author` (`author_id` ASC) ,
  INDEX `authoring_publication` (`publication_id` ASC) ,
  CONSTRAINT `authoring_author`
    FOREIGN KEY (`author_id` )
    REFERENCES `cordis`.`Author` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `authoring_publication`
    FOREIGN KEY (`publication_id` )
    REFERENCES `cordis`.`Publication` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cordis`.`Project`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cordis`.`Project` ;

CREATE  TABLE IF NOT EXISTS `cordis`.`Project` (
  `rcn` INT NOT NULL ,
  `contract_type` VARCHAR(1023) NULL ,
  `cost` INT NULL ,
  `cost_currency` VARCHAR(20) NULL ,
  `eu_contribution` INT NULL ,
  `eu_contribution_currency` VARCHAR(20) NULL ,
  `dates_from` DATE NULL ,
  `dates_to` DATE NULL ,
  `general_information` TEXT NULL ,
  `last_updated` DATE NULL ,
  `name` VARCHAR(1023) NOT NULL ,
  `objective` TEXT NULL ,
  `programme_acronym` VARCHAR(255) NULL ,
  `reference` VARCHAR(255) NULL ,
  `status` VARCHAR(255) NULL ,
  `subprogramme_area` VARCHAR(255) NULL ,
  `title` VARCHAR(1023) NULL ,
  `website` VARCHAR(1023) NULL ,
  PRIMARY KEY (`rcn`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cordis`.`Participant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cordis`.`Participant` ;

CREATE  TABLE IF NOT EXISTS `cordis`.`Participant` (
  `id` VARCHAR(32) NOT NULL ,
  `address` VARCHAR(1023) NULL ,
  `administrative_contact` VARCHAR(255) NULL ,
  `country` VARCHAR(255) NULL ,
  `fax` VARCHAR(45) NULL ,
  `name` VARCHAR(1023) NULL ,
  `tel` VARCHAR(45) NULL ,
  `website` VARCHAR(1023) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cordis`.`Participation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cordis`.`Participation` ;

CREATE  TABLE IF NOT EXISTS `cordis`.`Participation` (
  `project_rcn` INT NOT NULL ,
  `participant_id` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`project_rcn`, `participant_id`) ,
  INDEX `participation_participant` (`participant_id` ASC) ,
  INDEX `participation_project` (`project_rcn` ASC) ,
  CONSTRAINT `participation_participant`
    FOREIGN KEY (`participant_id` )
    REFERENCES `cordis`.`Participant` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `participation_project`
    FOREIGN KEY (`project_rcn` )
    REFERENCES `cordis`.`Project` (`rcn` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cordis`.`Project_Publication`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cordis`.`Project_Publication` ;

CREATE  TABLE IF NOT EXISTS `cordis`.`Project_Publication` (
  `project_rcn` INT NOT NULL ,
  `publication_id` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`project_rcn`, `publication_id`) ,
  INDEX `pp_project` (`project_rcn` ASC) ,
  INDEX `pp_publication` (`publication_id` ASC) ,
  CONSTRAINT `pp_project`
    FOREIGN KEY (`project_rcn` )
    REFERENCES `cordis`.`Project` (`rcn` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `pp_publication`
    FOREIGN KEY (`publication_id` )
    REFERENCES `cordis`.`Publication` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
