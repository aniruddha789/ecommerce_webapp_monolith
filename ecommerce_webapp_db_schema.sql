-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema footwear_ecommerce_webapp
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema footwear_ecommerce_webapp
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `footwear_ecommerce_webapp` DEFAULT CHARACTER SET utf8mb3 ;
USE `footwear_ecommerce_webapp` ;

-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`address` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `address_type` VARCHAR(255) NULL,
  `address_line1` VARCHAR(255) NULL,
  `address_line2` VARCHAR(255) NULL,
  `city` VARCHAR(255) NULL DEFAULT NULL,
  `district` VARCHAR(255) NULL DEFAULT NULL,
  `state` VARCHAR(255) NULL DEFAULT NULL,
  `pincode` INT NULL DEFAULT NULL,
  `country` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`event_publication`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`event_publication` (
  `id` BINARY(16) NOT NULL,
  `completion_date` DATETIME(6) NULL DEFAULT NULL,
  `event_type` VARCHAR(255) NULL DEFAULT NULL,
  `listener_id` VARCHAR(255) NULL DEFAULT NULL,
  `publication_date` DATETIME(6) NULL DEFAULT NULL,
  `serialized_event` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`product` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `brandid` VARCHAR(255) NOT NULL,
  `color` VARCHAR(255) NOT NULL,
  `listprice` INT NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `supplierID` INT NULL DEFAULT NULL,
  `image` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`, `name`, `brandid`, `color`),
  UNIQUE INDEX `ID_UNIQUE` (`ID` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`inventory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`inventory` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `quantity` INT NULL DEFAULT NULL,
  `productid` INT NOT NULL,
  `size` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK3i7p043bjgl1s0y2ptrgfn78x` (`productid` ASC) VISIBLE,
  CONSTRAINT `FK3i7p043bjgl1s0y2ptrgfn78x`
    FOREIGN KEY (`productid`)
    REFERENCES `footwear_ecommerce_webapp`.`product` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`user` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NULL DEFAULT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `firstname` VARCHAR(255) NULL DEFAULT NULL,
  `lastname` VARCHAR(255) NULL DEFAULT NULL,
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `phone` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `ID_UNIQUE` (`ID` ASC) VISIBLE,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `UKsb8bbouer5wak8vyiiy4pf2bx` (`username` ASC) VISIBLE,
  UNIQUE INDEX `UKob8kqyqqgmefl0aco34akdtpe` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`shop_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`shop_order` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `userid` INT NULL DEFAULT NULL,
  `order_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `order_status` VARCHAR(255) NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `ID_UNIQUE` (`ID` ASC) VISIBLE,
  INDEX `FKfk1h7bs3k4suxb9527k1fnv01` (`userid` ASC) VISIBLE,
  CONSTRAINT `FKfk1h7bs3k4suxb9527k1fnv01`
    FOREIGN KEY (`userid`)
    REFERENCES `footwear_ecommerce_webapp`.`user` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`order_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`order_item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `orderid` INT NULL DEFAULT NULL,
  `productid` INT NULL DEFAULT NULL,
  `quantity` INT NULL DEFAULT NULL,
  `size` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `FKfolqhfvaeo6gxntwd3ia2iw2j` (`orderid` ASC) VISIBLE,
  INDEX `FK65riinaev2asy1q07c9nrgrxr` (`productid` ASC) VISIBLE,
  CONSTRAINT `FK65riinaev2asy1q07c9nrgrxr`
    FOREIGN KEY (`productid`)
    REFERENCES `footwear_ecommerce_webapp`.`product` (`ID`),
  CONSTRAINT `FKfolqhfvaeo6gxntwd3ia2iw2j`
    FOREIGN KEY (`orderid`)
    REFERENCES `footwear_ecommerce_webapp`.`shop_order` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`user_address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`user_address` (
  `userid` INT NOT NULL,
  `addressid` INT NOT NULL,
  INDEX `FKq9knkmq8hfk3g8lb583uanlpg` (`addressid` ASC) VISIBLE,
  INDEX `FKkwc6y9q6paegwfqetiktewipu` (`userid` ASC) VISIBLE,
  CONSTRAINT `FKkwc6y9q6paegwfqetiktewipu`
    FOREIGN KEY (`userid`)
    REFERENCES `footwear_ecommerce_webapp`.`user` (`ID`),
  CONSTRAINT `FKq9knkmq8hfk3g8lb583uanlpg`
    FOREIGN KEY (`addressid`)
    REFERENCES `footwear_ecommerce_webapp`.`address` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `footwear_ecommerce_webapp`.`user_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `footwear_ecommerce_webapp`.`user_roles` (
  `userid` INT NOT NULL,
  `roleid` INT NOT NULL,
  INDEX `FKmet9tvqxk3rk51yhrcje7dwd5` (`roleid` ASC) VISIBLE,
  INDEX `FK5irsm4b6tad7ekxxb7wqvwjak` (`userid` ASC) VISIBLE,
  CONSTRAINT `FK5irsm4b6tad7ekxxb7wqvwjak`
    FOREIGN KEY (`userid`)
    REFERENCES `footwear_ecommerce_webapp`.`user` (`ID`),
  CONSTRAINT `FKmet9tvqxk3rk51yhrcje7dwd5`
    FOREIGN KEY (`roleid`)
    REFERENCES `footwear_ecommerce_webapp`.`roles` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


