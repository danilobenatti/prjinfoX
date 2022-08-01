CREATE DATABASE  IF NOT EXISTS `dbinfox` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `dbinfox`;
-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: dbinfox
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tbclientes`
--

DROP TABLE IF EXISTS `tbclientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbclientes` (
  `idcli` int NOT NULL AUTO_INCREMENT,
  `nomecli` varchar(50) NOT NULL,
  `endcli` varchar(100) DEFAULT NULL,
  `fonecli` varchar(50) NOT NULL,
  `emailcli` varchar(50) NOT NULL,
  PRIMARY KEY (`idcli`),
  UNIQUE KEY `emailcli_UNIQUE` (`emailcli`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbclientes`
--

LOCK TABLES `tbclientes` WRITE;
/*!40000 ALTER TABLE `tbclientes` DISABLE KEYS */;
INSERT INTO `tbclientes` VALUES (1,'Linus Torvalds','Rua Tux, 2016','8888-9999','linus@email.com'),(2,'José Maria','Rua Um, 101','9999-8888','josemaria@email.com'),(3,'Maria José','Rua Dois, 202','8888-7777','mariajose@email.com'),(4,'Lucas Silva de Assis','Rua Três, 303','7777-6666','lucas@email.com'),(5,'Pedro Jorge Zurich','Rua Quatro, 404','5555-3333','pedro@email.com'),(9,'Paulo Ricardo','Rua Cinco, 505','5555-7777','pauloricardo@email.com'),(10,'Novo Teste','Rua Teste 1234','8976-5432','novoteste@email.com');
/*!40000 ALTER TABLE `tbclientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbos`
--

DROP TABLE IF EXISTS `tbos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbos` (
  `idos` int NOT NULL AUTO_INCREMENT,
  `data_os` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo` char(1) NOT NULL,
  `status` int NOT NULL,
  `equipamento` varchar(150) NOT NULL,
  `defeito` varchar(150) NOT NULL,
  `servico` varchar(150) DEFAULT NULL,
  `tecnico` varchar(30) DEFAULT NULL,
  `valor` decimal(10,2) DEFAULT NULL,
  `idcli` int NOT NULL,
  `data_up` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`idos`),
  KEY `idcli_idx` (`idcli`),
  CONSTRAINT `idcli` FOREIGN KEY (`idcli`) REFERENCES `tbclientes` (`idcli`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbos`
--

LOCK TABLES `tbos` WRITE;
/*!40000 ALTER TABLE `tbos` DISABLE KEYS */;
INSERT INTO `tbos` VALUES (1,'2022-06-11 02:34:48','S',2,'notebook acer 7800','não liga','trocar a fonte e tela','José Carlos',2502.00,1,'2022-07-29 20:29:49'),(2,'2022-07-17 17:59:56','O',3,'teste','teste','teste','teste',150.30,5,'2022-07-29 02:19:06'),(3,'2022-07-17 18:15:13','S',4,'Impressora laser','não imprimi','trocar o tonner','José',250.50,2,'2022-07-29 02:19:06'),(4,'2022-07-17 18:30:56','S',2,'Console PS4','não liga','trocar placa lógica','Kayo',1568.32,2,'2022-07-29 02:19:06'),(5,'2022-07-17 20:36:44','O',1,'teste','teste','teste','teste',2450.50,9,'2022-07-29 02:19:06'),(6,'2022-07-18 11:22:11','S',2,'Monitor LCD','não liga','trocar fonte','João',125.40,5,'2022-07-29 04:19:24'),(9,'2022-07-18 14:55:43','S',2,'Radio AM/FM','Sem audio nos alto-falantes','troca de alto-falantes','João',50.92,4,'2022-07-29 02:19:06'),(10,'2022-07-18 19:38:57','S',2,'Filmadora UHD','não liga','falha no botão de ligar','Joal',89.62,9,'2022-07-29 02:19:06'),(11,'2022-07-18 19:51:56','S',1,'Forno Elétrico','resistência queimada','troca de resistência','Oswaldo Luiz',5089.56,9,'2022-07-29 02:19:06'),(16,'2022-07-18 20:40:46','O',0,'Monitor LCD','não liga','trocar o botão liga/desliga','',50.23,5,'2022-07-29 02:19:06'),(17,'2022-07-19 18:42:14','S',0,'Celular','tela quebrada','enviar para assistência técnica','Oswaldo',4500.40,5,'2022-07-29 02:19:06'),(18,'2022-07-26 14:52:05','S',1,'TV OLED','Tela trincada','substituir tela','Jorge',5585.46,5,'2022-07-29 02:19:06'),(19,'2022-07-26 16:12:29','S',1,'Radio AM/FM','antena quebrada','trocar a antena','Gabriel',78.85,9,'2022-07-29 02:19:06'),(20,'2022-07-27 14:45:59','O',1,'Fone de ouvido gamer','espumas desgastadas','troca de espumas','Joel',54.32,9,'2022-07-29 02:19:06'),(22,'2022-07-29 00:40:38','S',1,'teste','teste','teste','teste',80.00,1,'2022-07-29 02:19:06'),(24,'2022-07-29 04:25:29','O',0,'teste','teste','','',45.00,1,'2022-07-29 04:25:29'),(25,'2022-07-29 04:31:06','S',6,'teste    treee','teste','hththt','jjjjj',8900.59,1,'2022-07-29 20:51:30'),(26,'2022-07-29 16:41:59','S',3,'teste teste','teste','','',56.80,2,'2022-07-29 20:39:12');
/*!40000 ALTER TABLE `tbos` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tbos_BEFORE_INSERT` BEFORE INSERT ON `tbos` FOR EACH ROW BEGIN
SET NEW.data_up = CURRENT_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tbos_BEFORE_UPDATE` BEFORE UPDATE ON `tbos` FOR EACH ROW BEGIN
SET NEW.data_up = CURRENT_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `tbusuarios`
--

DROP TABLE IF EXISTS `tbusuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbusuarios` (
  `iduser` int NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `fone` varchar(15) DEFAULT NULL,
  `login` varchar(15) NOT NULL,
  `senha` varchar(15) NOT NULL,
  `perfil` varchar(20) NOT NULL,
  PRIMARY KEY (`iduser`),
  UNIQUE KEY `login_UNIQUE` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbusuarios`
--

LOCK TABLES `tbusuarios` WRITE;
/*!40000 ALTER TABLE `tbusuarios` DISABLE KEYS */;
INSERT INTO `tbusuarios` VALUES (1,'José de Assis','9999-5555','jose3','123','user'),(2,'Administrator','8888-8888','admin','123','admin'),(3,'Sucupira Souza','5555-6666','pira99','123','user'),(13,'teste 13','1234-5678','test13','123','admin'),(15,'Carlos Aparecido','1234-5678','carap','123','guest');
/*!40000 ALTER TABLE `tbusuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-07-29 18:01:56
