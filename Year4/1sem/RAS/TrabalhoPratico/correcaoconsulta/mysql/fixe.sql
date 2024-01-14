
-- -----------------------------------------------------
-- database mydb
-- -----------------------------------------------------
CREATE DATABASE IF NOT EXISTS `fixe` ;
USE `fixe` ;
-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table mydb.Prova
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fixe`.Prova (
  `id_prova_realizada` VARCHAR(255) NOT NULL,
  `id_prova_duplicada` VARCHAR(255) NOT NULL,
  `classificacao_final` INT(10) NULL,
  num_Aluno VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_prova_realizada`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.TipoQuestão
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fixe`.TipoQuestao (
  `id_tipo` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_tipo`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table mydb.Questão
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fixe`.Questao (
  `id_questao` VARCHAR(255) NOT NULL,
  `cotacaoTotal` FLOAT(10) NULL,
  `Prova_id_prova_realizada` VARCHAR(255) NOT NULL,
  `TipoQuestão_id_tipo` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_questao`),
  CONSTRAINT fk_Questão_Prova
    FOREIGN KEY (Prova_id_prova_realizada)
    REFERENCES `fixe`.Prova (id_prova_realizada)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Questão_TipoQuestão1
    FOREIGN KEY (TipoQuestão_id_tipo)
    REFERENCES `fixe`.TipoQuestão (id_tipo)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE USER `gajofixe`@localhost IDENTIFIED BY `raposeira` ;
GRANT ALL PRIVILEGES ON `fixe`.* TO `gajofixe`@localhost;
GRANT ALL PRIVILEGES ON *.* TO `gajofixe`@localhost;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
