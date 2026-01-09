-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: club_gestion
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `adherent`
--

DROP TABLE IF EXISTS `adherent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adherent` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKhqawufl1s434djnsa36fu0pqe` FOREIGN KEY (`id`) REFERENCES `utilisateur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adherent`
--

LOCK TABLES `adherent` WRITE;
/*!40000 ALTER TABLE `adherent` DISABLE KEYS */;
INSERT INTO `adherent` VALUES (10),(16);
/*!40000 ALTER TABLE `adherent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `adherent_club`
--

DROP TABLE IF EXISTS `adherent_club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adherent_club` (
  `club_id` bigint NOT NULL,
  `adherent_id` bigint NOT NULL,
  KEY `FKoaray7s5mihf6smfg6x25ttml` (`adherent_id`),
  KEY `FKrcd1kirpli1y2igdptc9imqie` (`club_id`),
  CONSTRAINT `FKoaray7s5mihf6smfg6x25ttml` FOREIGN KEY (`adherent_id`) REFERENCES `adherent` (`id`),
  CONSTRAINT `FKrcd1kirpli1y2igdptc9imqie` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adherent_club`
--

LOCK TABLES `adherent_club` WRITE;
/*!40000 ALTER TABLE `adherent_club` DISABLE KEYS */;
INSERT INTO `adherent_club` VALUES (28,10),(27,10),(32,16);
/*!40000 ALTER TABLE `adherent_club` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKgodqjbbtwk30kf3s0xuxklkr3` FOREIGN KEY (`id`) REFERENCES `utilisateur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club`
--

DROP TABLE IF EXISTS `club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_creation` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `objectifs` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club`
--

LOCK TABLES `club` WRITE;
/*!40000 ALTER TABLE `club` DISABLE KEYS */;
INSERT INTO `club` VALUES (1,NULL,'Mechatronics','Mechatronics','hufdhu'),(27,NULL,'tyt','GDG','s'),(28,NULL,'grts','Majoug','weerw'),(32,NULL,'Le Club Innov’Tech ENSAM Casablanca est un club scientifique et technologique dédié à l’innovation, à l’ingénierie et aux nouvelles technologies.','Club Innov’Tech ENSAM Casablanca','Il vise à développer les compétences techniques et professionnelles des étudiants à travers des ateliers pratiques, des conférences, des projets collaboratifs et des événements technologiques en partenariat avec des entreprises et des experts du domaine.');
/*!40000 ALTER TABLE `club` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_membre_bureau_adheran_list`
--

DROP TABLE IF EXISTS `club_membre_bureau_adheran_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_membre_bureau_adheran_list` (
  `club_list_adherent_id` bigint NOT NULL,
  `membre_bureau_adheran_list_id` bigint NOT NULL,
  KEY `FKbg9uygkvqcw9y3y4vjycetg4l` (`membre_bureau_adheran_list_id`),
  KEY `FKrqfittvvxcuv15cyp5icjnyi1` (`club_list_adherent_id`),
  CONSTRAINT `FKbg9uygkvqcw9y3y4vjycetg4l` FOREIGN KEY (`membre_bureau_adheran_list_id`) REFERENCES `membre_bureau` (`id`),
  CONSTRAINT `FKrqfittvvxcuv15cyp5icjnyi1` FOREIGN KEY (`club_list_adherent_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_membre_bureau_adheran_list`
--

LOCK TABLES `club_membre_bureau_adheran_list` WRITE;
/*!40000 ALTER TABLE `club_membre_bureau_adheran_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `club_membre_bureau_adheran_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_membre_bureau_list`
--

DROP TABLE IF EXISTS `club_membre_bureau_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_membre_bureau_list` (
  `club_list_id` bigint NOT NULL,
  `membre_bureau_list_id` bigint NOT NULL,
  KEY `FKtaf7u1imw87siwbwl7sy7c1q2` (`membre_bureau_list_id`),
  KEY `FKe7qcscr8w4iev2041ts50f4b0` (`club_list_id`),
  CONSTRAINT `FKe7qcscr8w4iev2041ts50f4b0` FOREIGN KEY (`club_list_id`) REFERENCES `club` (`id`),
  CONSTRAINT `FKtaf7u1imw87siwbwl7sy7c1q2` FOREIGN KEY (`membre_bureau_list_id`) REFERENCES `membre_bureau` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_membre_bureau_list`
--

LOCK TABLES `club_membre_bureau_list` WRITE;
/*!40000 ALTER TABLE `club_membre_bureau_list` DISABLE KEYS */;
INSERT INTO `club_membre_bureau_list` VALUES (32,15);
/*!40000 ALTER TABLE `club_membre_bureau_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `demande_adhesion`
--

DROP TABLE IF EXISTS `demande_adhesion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demande_adhesion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_demande` datetime(6) DEFAULT NULL,
  `statut` varchar(255) DEFAULT NULL,
  `club_id` bigint DEFAULT NULL,
  `demandeur_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKy8bsln419bw6c15q52laklyb` (`club_id`),
  KEY `FKlemoosqlqouh44bdswmxvjedt` (`demandeur_id`),
  CONSTRAINT `FKlemoosqlqouh44bdswmxvjedt` FOREIGN KEY (`demandeur_id`) REFERENCES `utilisateur` (`id`),
  CONSTRAINT `FKy8bsln419bw6c15q52laklyb` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `demande_adhesion`
--

LOCK TABLES `demande_adhesion` WRITE;
/*!40000 ALTER TABLE `demande_adhesion` DISABLE KEYS */;
INSERT INTO `demande_adhesion` VALUES (1,'2026-01-09 12:06:53.085778','EN_ATTENTE',1,1);
/*!40000 ALTER TABLE `demande_adhesion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `demande_club`
--

DROP TABLE IF EXISTS `demande_club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demande_club` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `commentaire_admin` varchar(255) DEFAULT NULL,
  `date_demande` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `emails_membres` varbinary(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `objectifs` varchar(255) DEFAULT NULL,
  `statut` varchar(255) DEFAULT NULL,
  `demandeur_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi05i1pp6hmplchrrug4cws8he` (`demandeur_id`),
  CONSTRAINT `FKi05i1pp6hmplchrrug4cws8he` FOREIGN KEY (`demandeur_id`) REFERENCES `utilisateur` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `demande_club`
--

LOCK TABLES `demande_club` WRITE;
/*!40000 ALTER TABLE `demande_club` DISABLE KEYS */;
/*!40000 ALTER TABLE `demande_club` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evenement`
--

DROP TABLE IF EXISTS `evenement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evenement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `etat` varchar(255) DEFAULT NULL,
  `lieu` varchar(255) DEFAULT NULL,
  `titre` varchar(255) DEFAULT NULL,
  `club_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjuvku5denanixuwpps4vcy6px` (`club_id`),
  CONSTRAINT `FKjuvku5denanixuwpps4vcy6px` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evenement`
--

LOCK TABLES `evenement` WRITE;
/*!40000 ALTER TABLE `evenement` DISABLE KEYS */;
INSERT INTO `evenement` VALUES (2,'2026-01-22','TRTR',NULL,NULL,'AGIOS',27),(3,'2026-01-22','ghhg',NULL,NULL,'fgfgfg',28),(4,'2026-01-23','dsaadsads',NULL,NULL,'dsdads',27),(5,'2025-12-30','fbffdfdfgd',NULL,NULL,'AGIOS',27),(9,'2026-01-12','Un atelier pratique destiné aux débutants pour comprendre les bases de l’intelligence artificielle.',NULL,NULL,'Workshop : Introduction à l’Intelligence Artificielle',32),(10,'2025-12-31','achine learning',NULL,NULL,'AGIOS',32);
/*!40000 ALTER TABLE `evenement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evenement_participants`
--

DROP TABLE IF EXISTS `evenement_participants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evenement_participants` (
  `evenements_id` bigint NOT NULL,
  `participants_id` bigint NOT NULL,
  KEY `FKi8927obsdr0g1283mggbetgiq` (`participants_id`),
  KEY `FK52j9jyqoc6logko1alvpssnyu` (`evenements_id`),
  CONSTRAINT `FK52j9jyqoc6logko1alvpssnyu` FOREIGN KEY (`evenements_id`) REFERENCES `evenement` (`id`),
  CONSTRAINT `FKi8927obsdr0g1283mggbetgiq` FOREIGN KEY (`participants_id`) REFERENCES `adherent` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evenement_participants`
--

LOCK TABLES `evenement_participants` WRITE;
/*!40000 ALTER TABLE `evenement_participants` DISABLE KEYS */;
/*!40000 ALTER TABLE `evenement_participants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membre_bureau`
--

DROP TABLE IF EXISTS `membre_bureau`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membre_bureau` (
  `poste` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKrra09a10xohymdbl497s57a1g` FOREIGN KEY (`id`) REFERENCES `utilisateur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membre_bureau`
--

LOCK TABLES `membre_bureau` WRITE;
/*!40000 ALTER TABLE `membre_bureau` DISABLE KEYS */;
INSERT INTO `membre_bureau` VALUES ('PRESIDENT',9),('PRESIDENT',15);
/*!40000 ALTER TABLE `membre_bureau` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membre_bureau_club`
--

DROP TABLE IF EXISTS `membre_bureau_club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membre_bureau_club` (
  `club_id` bigint NOT NULL,
  `membre_bureau_id` bigint NOT NULL,
  KEY `FKr5d5u4dfxk873m01t6sv7mpja` (`membre_bureau_id`),
  KEY `FKbfcx97x8d5j5elpwischv8e05` (`club_id`),
  CONSTRAINT `FKbfcx97x8d5j5elpwischv8e05` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
  CONSTRAINT `FKr5d5u4dfxk873m01t6sv7mpja` FOREIGN KEY (`membre_bureau_id`) REFERENCES `membre_bureau` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membre_bureau_club`
--

LOCK TABLES `membre_bureau_club` WRITE;
/*!40000 ALTER TABLE `membre_bureau_club` DISABLE KEYS */;
/*!40000 ALTER TABLE `membre_bureau_club` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membre_bureau_evenement_organises`
--

DROP TABLE IF EXISTS `membre_bureau_evenement_organises`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membre_bureau_evenement_organises` (
  `membre_bureau_list_id` bigint NOT NULL,
  `evenement_organises_id` bigint NOT NULL,
  KEY `FKinoidyu8jtxyfvqhbef0744hu` (`evenement_organises_id`),
  KEY `FKk0my4inx1f07w6wxyrov4aiku` (`membre_bureau_list_id`),
  CONSTRAINT `FKinoidyu8jtxyfvqhbef0744hu` FOREIGN KEY (`evenement_organises_id`) REFERENCES `evenement` (`id`),
  CONSTRAINT `FKk0my4inx1f07w6wxyrov4aiku` FOREIGN KEY (`membre_bureau_list_id`) REFERENCES `membre_bureau` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membre_bureau_evenement_organises`
--

LOCK TABLES `membre_bureau_evenement_organises` WRITE;
/*!40000 ALTER TABLE `membre_bureau_evenement_organises` DISABLE KEYS */;
/*!40000 ALTER TABLE `membre_bureau_evenement_organises` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realisation`
--

DROP TABLE IF EXISTS `realisation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `realisation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `titre` varchar(255) DEFAULT NULL,
  `club_id` bigint DEFAULT NULL,
  `membre_bureau_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9h74lbr3w15vr9b94mjmuirja` (`club_id`),
  KEY `FK4648g3mu2p4wkxuhlwko6p618` (`membre_bureau_id`),
  CONSTRAINT `FK4648g3mu2p4wkxuhlwko6p618` FOREIGN KEY (`membre_bureau_id`) REFERENCES `membre_bureau` (`id`),
  CONSTRAINT `FK9h74lbr3w15vr9b94mjmuirja` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realisation`
--

LOCK TABLES `realisation` WRITE;
/*!40000 ALTER TABLE `realisation` DISABLE KEYS */;
/*!40000 ALTER TABLE `realisation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilisateur` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `mot_de_passe` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrma38wvnqfaf66vvmi57c71lo` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES (1,'ilyassmajoug@gmail.com','12234','Majoug','ilyass','MEMBREBUREAU'),(2,'admin@ensam.ma','1234','Admin','Admin','ADMIN'),(8,'admin@ecole.ma','1234','Admin','Admin','ADMIN'),(9,'a@gmail.com','a','a','a','MEMBREBUREAU'),(10,'adherent@gmail.com','adherent','adherent ','adherent','ADHERENT'),(15,'ilyassmajougg@gmail.com','1234','Majoug','ilyass','MEMBREBUREAU'),(16,'ilyassmajouggg@gmail.com','1234','Majoug','ilyass','ADHERENT');
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-09 23:55:52
