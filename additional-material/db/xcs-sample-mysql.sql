CREATE DATABASE IF NOT EXISTS petstore;
USE petstore;

DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS users;

--
-- Table structure for table `users`
--
CREATE TABLE users
(
    role     VARCHAR(10)  NOT NULL,
    login    VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (login)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- Table structure for table `pets`
--
CREATE TABLE pets
(
    id     INT                         NOT NULL AUTO_INCREMENT,
    animal ENUM ('BIRD', 'CAT', 'DOG') NOT NULL,
    birth  DATETIME                    NOT NULL,
    name   VARCHAR(100)                NOT NULL,
    owner  VARCHAR(100)                NOT NULL,
    PRIMARY KEY (id),
    KEY `FK_Pet_Owner` (owner),
    CONSTRAINT `FK_Pet_Owner_login` FOREIGN KEY (owner) REFERENCES users (login)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- User creation
--
CREATE USER IF NOT EXISTS 'petstore'@'%' IDENTIFIED BY 'petstore';
GRANT ALL PRIVILEGES ON petstore.* TO 'petstore'@'%';
FLUSH PRIVILEGES;

--
-- Data for table `users`
--
INSERT INTO users (role, login, password)
VALUES ('ADMIN', 'jose', 'A3F6F4B40B24E2FD61F08923ED452F34'),   -- password: 'josepass'
       ('OWNER', 'ana', '22BEEAE33E9B2657F9610621502CD7A4'),    -- password: 'anapass'
       ('OWNER', 'juan', 'B4FBB95580592697DC71488A1F19277E'),   -- password: 'juanpass'
       ('OWNER', 'lorena', '05009E420932C21E5A68F5EF1AADD530'), -- password: 'lorenapass'
       ('OWNER', 'pepe', 'B43B4D046860B2BD945BCA2597BF9F07');   -- password: 'pepepass'

--
-- Data for table `pets`
--
INSERT INTO pets (animal, birth, name, owner)
VALUES ('CAT', '2000-01-01 01:01:01', 'Pepecat', 'pepe'),
       ('CAT', '2000-01-01 01:01:01', 'Max', 'juan'),
       ('DOG', '2000-01-01 01:01:01', 'Juandog', 'juan'),
       ('CAT', '2000-01-01 01:01:01', 'Anacat', 'ana'),
       ('DOG', '2000-01-01 01:01:01', 'Max', 'ana'),
       ('BIRD', '2000-01-01 01:01:01', 'Anabird', 'ana');
