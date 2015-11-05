CREATE DATABASE  IF NOT EXISTS `shpp` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `shpp`;
-- MySQL dump 10.13  Distrib 5.1.63, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: shpp
-- ------------------------------------------------------
-- Server version	5.1.63-0ubuntu0.11.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auth_permission`
--

DROP TABLE IF EXISTS `auth_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auth_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `codename` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `content_type_id` (`content_type_id`,`codename`),
  KEY `auth_permission_1bb8f392` (`content_type_id`)
) ENGINE=MyISAM AUTO_INCREMENT=107 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_permission`
--

LOCK TABLES `auth_permission` WRITE;
/*!40000 ALTER TABLE `auth_permission` DISABLE KEYS */;
INSERT INTO `auth_permission` VALUES (1,'Can add permission',1,'add_permission'),(2,'Can change permission',1,'change_permission'),(3,'Can delete permission',1,'delete_permission'),(4,'Can add group',2,'add_group'),(5,'Can change group',2,'change_group'),(6,'Can delete group',2,'delete_group'),(7,'Can add user',3,'add_user'),(8,'Can change user',3,'change_user'),(9,'Can delete user',3,'delete_user'),(10,'Can add content type',4,'add_contenttype'),(11,'Can change content type',4,'change_contenttype'),(12,'Can delete content type',4,'delete_contenttype'),(13,'Can add redirect',5,'add_redirect'),(14,'Can change redirect',5,'change_redirect'),(15,'Can delete redirect',5,'delete_redirect'),(16,'Can add session',6,'add_session'),(17,'Can change session',6,'change_session'),(18,'Can delete session',6,'delete_session'),(19,'Can add site',7,'add_site'),(20,'Can change site',7,'change_site'),(21,'Can delete site',7,'delete_site'),(22,'Can add Setting',8,'add_setting'),(23,'Can change Setting',8,'change_setting'),(24,'Can delete Setting',8,'delete_setting'),(25,'Can add Comment',9,'add_threadedcomment'),(26,'Can change Comment',9,'change_threadedcomment'),(27,'Can delete Comment',9,'delete_threadedcomment'),(28,'Can add Keyword',10,'add_keyword'),(29,'Can change Keyword',10,'change_keyword'),(30,'Can delete Keyword',10,'delete_keyword'),(31,'Can add assigned keyword',11,'add_assignedkeyword'),(32,'Can change assigned keyword',11,'change_assignedkeyword'),(33,'Can delete assigned keyword',11,'delete_assignedkeyword'),(34,'Can add Rating',12,'add_rating'),(35,'Can change Rating',12,'change_rating'),(36,'Can delete Rating',12,'delete_rating'),(37,'Can add Blog post',13,'add_blogpost'),(38,'Can change Blog post',13,'change_blogpost'),(39,'Can delete Blog post',13,'delete_blogpost'),(40,'Can add Blog Category',14,'add_blogcategory'),(41,'Can change Blog Category',14,'change_blogcategory'),(42,'Can delete Blog Category',14,'delete_blogcategory'),(43,'Can add Form',15,'add_form'),(44,'Can change Form',15,'change_form'),(45,'Can delete Form',15,'delete_form'),(46,'Can add Field',16,'add_field'),(47,'Can change Field',16,'change_field'),(48,'Can delete Field',16,'delete_field'),(49,'Can add Form entry',17,'add_formentry'),(50,'Can change Form entry',17,'change_formentry'),(51,'Can delete Form entry',17,'delete_formentry'),(52,'Can add Form field entry',18,'add_fieldentry'),(53,'Can change Form field entry',18,'change_fieldentry'),(54,'Can delete Form field entry',18,'delete_fieldentry'),(55,'Can add Page',19,'add_page'),(56,'Can change Page',19,'change_page'),(57,'Can delete Page',19,'delete_page'),(58,'Can add Rich text page',20,'add_richtextpage'),(59,'Can change Rich text page',20,'change_richtextpage'),(60,'Can delete Rich text page',20,'delete_richtextpage'),(61,'Can add Link',21,'add_link'),(62,'Can change Link',21,'change_link'),(63,'Can delete Link',21,'delete_link'),(64,'Can add Gallery',22,'add_gallery'),(65,'Can change Gallery',22,'change_gallery'),(66,'Can delete Gallery',22,'delete_gallery'),(67,'Can add Image',23,'add_galleryimage'),(68,'Can change Image',23,'change_galleryimage'),(69,'Can delete Image',23,'delete_galleryimage'),(70,'Can add Twitter query',24,'add_query'),(71,'Can change Twitter query',24,'change_query'),(72,'Can delete Twitter query',24,'delete_query'),(73,'Can add Tweet',25,'add_tweet'),(74,'Can change Tweet',25,'change_tweet'),(75,'Can delete Tweet',25,'delete_tweet'),(76,'Can add migration history',26,'add_migrationhistory'),(77,'Can change migration history',26,'change_migrationhistory'),(78,'Can delete migration history',26,'delete_migrationhistory'),(79,'Can add Navigation',27,'add_navigation'),(80,'Can change Navigation',27,'change_navigation'),(81,'Can delete Navigation',27,'delete_navigation'),(82,'Can add Navigation Item',28,'add_navigationitem'),(83,'Can change Navigation Item',28,'change_navigationitem'),(84,'Can delete Navigation Item',28,'delete_navigationitem'),(85,'Can add Bookmark',29,'add_bookmark'),(86,'Can change Bookmark',29,'change_bookmark'),(87,'Can delete Bookmark',29,'delete_bookmark'),(88,'Can add Bookmark Item',30,'add_bookmarkitem'),(89,'Can change Bookmark Item',30,'change_bookmarkitem'),(90,'Can delete Bookmark Item',30,'delete_bookmarkitem'),(91,'Can add Help',31,'add_help'),(92,'Can change Help',31,'change_help'),(93,'Can delete Help',31,'delete_help'),(94,'Can add Help Entry',32,'add_helpitem'),(95,'Can change Help Entry',32,'change_helpitem'),(96,'Can delete Help Entry',32,'delete_helpitem'),(97,'Can add log entry',33,'add_logentry'),(98,'Can change log entry',33,'change_logentry'),(99,'Can delete log entry',33,'delete_logentry'),(100,'Can add comment',34,'add_comment'),(101,'Can change comment',34,'change_comment'),(102,'Can delete comment',34,'delete_comment'),(103,'Can moderate comments',34,'can_moderate'),(104,'Can add comment flag',35,'add_commentflag'),(105,'Can change comment flag',35,'change_commentflag'),(106,'Can delete comment flag',35,'delete_commentflag');
/*!40000 ALTER TABLE `auth_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_group`
--

DROP TABLE IF EXISTS `auth_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auth_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_group`
--

LOCK TABLES `auth_group` WRITE;
/*!40000 ALTER TABLE `auth_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forms_formentry`
--

DROP TABLE IF EXISTS `forms_formentry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forms_formentry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) NOT NULL,
  `entry_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `forms_formentry_1d0aabf2` (`form_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forms_formentry`
--

LOCK TABLES `forms_formentry` WRITE;
/*!40000 ALTER TABLE `forms_formentry` DISABLE KEYS */;
/*!40000 ALTER TABLE `forms_formentry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_user_user_permissions`
--

DROP TABLE IF EXISTS `auth_user_user_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auth_user_user_permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`permission_id`),
  KEY `auth_user_user_permissions_403f60f` (`user_id`),
  KEY `auth_user_user_permissions_1e014c8f` (`permission_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_user_user_permissions`
--

LOCK TABLES `auth_user_user_permissions` WRITE;
/*!40000 ALTER TABLE `auth_user_user_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_user_user_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_blogpost`
--

DROP TABLE IF EXISTS `blog_blogpost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blog_blogpost` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comments_count` int(11) NOT NULL,
  `keywords_string` varchar(500) NOT NULL,
  `rating_count` int(11) NOT NULL,
  `rating_average` double NOT NULL,
  `site_id` int(11) NOT NULL,
  `title` varchar(500) NOT NULL,
  `slug` varchar(2000) DEFAULT NULL,
  `description` longtext NOT NULL,
  `gen_description` tinyint(1) NOT NULL,
  `status` int(11) NOT NULL,
  `publish_date` datetime DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL,
  `short_url` varchar(200) DEFAULT NULL,
  `content` longtext NOT NULL,
  `user_id` int(11) NOT NULL,
  `allow_comments` tinyint(1) NOT NULL,
  `featured_image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `blog_blogpost_6223029` (`site_id`),
  KEY `blog_blogpost_403f60f` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_blogpost`
--

LOCK TABLES `blog_blogpost` WRITE;
/*!40000 ALTER TABLE `blog_blogpost` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_blogpost` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_blogcategory`
--

DROP TABLE IF EXISTS `blog_blogcategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blog_blogcategory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL,
  `title` varchar(500) NOT NULL,
  `slug` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `blog_blogcategory_6223029` (`site_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_blogcategory`
--

LOCK TABLES `blog_blogcategory` WRITE;
/*!40000 ALTER TABLE `blog_blogcategory` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_blogcategory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `generic_rating`
--

DROP TABLE IF EXISTS `generic_rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `generic_rating` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` int(11) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `object_pk` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `generic_rating_1bb8f392` (`content_type_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `generic_rating`
--

LOCK TABLES `generic_rating` WRITE;
/*!40000 ALTER TABLE `generic_rating` DISABLE KEYS */;
/*!40000 ALTER TABLE `generic_rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_site`
--

DROP TABLE IF EXISTS `django_site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `django_site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `domain` varchar(100) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_site`
--

LOCK TABLES `django_site` WRITE;
/*!40000 ALTER TABLE `django_site` DISABLE KEYS */;
INSERT INTO `django_site` VALUES (1,'127.0.0.1:8000','Default');
/*!40000 ALTER TABLE `django_site` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_navigationitem`
--

DROP TABLE IF EXISTS `grappelli_safe_navigationitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_navigationitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `navigation_id` int(11) NOT NULL,
  `title` varchar(30) NOT NULL,
  `link` varchar(200) NOT NULL,
  `category` varchar(1) NOT NULL,
  `order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `grappelli_safe_navigationitem_71b5c9c` (`navigation_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_navigationitem`
--

LOCK TABLES `grappelli_safe_navigationitem` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_navigationitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_navigationitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `generic_assignedkeyword`
--

DROP TABLE IF EXISTS `generic_assignedkeyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `generic_assignedkeyword` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `_order` int(11) DEFAULT NULL,
  `keyword_id` int(11) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `object_pk` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `generic_assignedkeyword_59bcbf7e` (`keyword_id`),
  KEY `generic_assignedkeyword_1bb8f392` (`content_type_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `generic_assignedkeyword`
--

LOCK TABLES `generic_assignedkeyword` WRITE;
/*!40000 ALTER TABLE `generic_assignedkeyword` DISABLE KEYS */;
/*!40000 ALTER TABLE `generic_assignedkeyword` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_navigation`
--

DROP TABLE IF EXISTS `grappelli_safe_navigation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_navigation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(30) NOT NULL,
  `order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_navigation`
--

LOCK TABLES `grappelli_safe_navigation` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_navigation` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_navigation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_comment_flags`
--

DROP TABLE IF EXISTS `django_comment_flags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `django_comment_flags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `comment_id` int(11) NOT NULL,
  `flag` varchar(30) NOT NULL,
  `flag_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`comment_id`,`flag`),
  KEY `django_comment_flags_403f60f` (`user_id`),
  KEY `django_comment_flags_64c238ac` (`comment_id`),
  KEY `django_comment_flags_111c90f9` (`flag`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_comment_flags`
--

LOCK TABLES `django_comment_flags` WRITE;
/*!40000 ALTER TABLE `django_comment_flags` DISABLE KEYS */;
/*!40000 ALTER TABLE `django_comment_flags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_group_permissions`
--

DROP TABLE IF EXISTS `auth_group_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auth_group_permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_id` (`group_id`,`permission_id`),
  KEY `auth_group_permissions_425ae3c4` (`group_id`),
  KEY `auth_group_permissions_1e014c8f` (`permission_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_group_permissions`
--

LOCK TABLES `auth_group_permissions` WRITE;
/*!40000 ALTER TABLE `auth_group_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_group_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_session`
--

DROP TABLE IF EXISTS `django_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `django_session` (
  `session_key` varchar(40) NOT NULL,
  `session_data` longtext NOT NULL,
  `expire_date` datetime NOT NULL,
  PRIMARY KEY (`session_key`),
  KEY `django_session_3da3d3d8` (`expire_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_session`
--

LOCK TABLES `django_session` WRITE;
/*!40000 ALTER TABLE `django_session` DISABLE KEYS */;
INSERT INTO `django_session` VALUES ('96c4e091e58d54cde5311faec5f7ab5e','ODAxNDQ1MDUxN2U0MGIzODIxMDU2ZjY4YTI1MWM4OGM0Zjg0NzAzYzqAAn1xAS4=\n','2012-07-05 17:41:08'),('edf682a2961279e963cfc625370b3372','NzllMzFiMTRjZjQzNTBjYTZhM2M0ZjY4M2MwMzA0ODI1YWVhOTJkNjqAAn1xAVUKdGVzdGNvb2tp\nZXECVQZ3b3JrZWRxA3Mu\n','2012-07-05 17:47:41');
/*!40000 ALTER TABLE `django_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_user_groups`
--

DROP TABLE IF EXISTS `auth_user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auth_user_groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`group_id`),
  KEY `auth_user_groups_403f60f` (`user_id`),
  KEY `auth_user_groups_425ae3c4` (`group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_user_groups`
--

LOCK TABLES `auth_user_groups` WRITE;
/*!40000 ALTER TABLE `auth_user_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_user_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pages_link`
--

DROP TABLE IF EXISTS `pages_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pages_link` (
  `page_ptr_id` int(11) NOT NULL,
  PRIMARY KEY (`page_ptr_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pages_link`
--

LOCK TABLES `pages_link` WRITE;
/*!40000 ALTER TABLE `pages_link` DISABLE KEYS */;
INSERT INTO `pages_link` VALUES (3);
/*!40000 ALTER TABLE `pages_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pages_richtextpage`
--

DROP TABLE IF EXISTS `pages_richtextpage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pages_richtextpage` (
  `page_ptr_id` int(11) NOT NULL,
  `content` longtext NOT NULL,
  PRIMARY KEY (`page_ptr_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pages_richtextpage`
--

LOCK TABLES `pages_richtextpage` WRITE;
/*!40000 ALTER TABLE `pages_richtextpage` DISABLE KEYS */;
INSERT INTO `pages_richtextpage` VALUES (1,'<div>\n<h2>Bienvenido al portal del</h2>\n<h1>Proyecto Proteoma Humano (España)</h1>\n<p>El Proyecto Proteoma Humano (HPP) tiene como objetivo fundamental la identificación y cuantificación de todas las proteínas codificadas por los 20.300 genes contenidos en el genoma humano, en los tejidos normales y en los patológicos.</p>\n<p>Tiene por tanto una doble vertiente, básica o fundamental para una descripción completa del proteoma y obtener un conocimiento profundo de la biología del ser humano y una vertiente aplicada para lograr un mejor entendimiento de las enfermedades complejas prevalentes (cardiovasculares, cáncer, neurodegenerativas, diabetes, obesidad, osteoarticulares, infecciosas, et.).</p>\n<p>El proyecto plantea la identificación de todas las proteínas humanas y sus variantes (isoformas, modificaciones postraduccionales) así como la determinación de sus concentraciones en las aproximadamente 230 estirpes celulares que constituyen nuestro organismo.</p>\n<p>El conocimiento derivado del HPP será del dominio público, accesible de forma libre y gratuita a todas las instituciones científicas y al público en general.</p>\n</div>'),(2,'<p>Sobre SHPP</p>'),(4,'<h2>Bases de Datos en formato FASTA</h2>\n<table border=\"1\" cellpadding=\"1\" cellspacing=\"0\">\n<thead>\n<tr><th colspan=\"1\" rowspan=\"2\" scope=\"row\"> </th><th colspan=\"4\" rowspan=\"1\" scope=\"col\">Human All</th><th colspan=\"4\" rowspan=\"1\" scope=\"col\">Chromosome 16</th></tr>\n<tr><th scope=\"col\">#Entries</th><th scope=\"col\">Target</th><th scope=\"col\">Decoy</th><th scope=\"col\">Target+<br> Decoy</th><th scope=\"col\">#Entries</th><th scope=\"col\">Target</th><th scope=\"col\">Decoy</th><th scope=\"col\">Target+<br> Decoy</th></tr>\n</thead>\n<tbody>\n<tr><th scope=\"row\">neXtProt<br> (2012-05-07)</th>\n<td align=\"center\" valign=\"middle\">36.630</td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/nextprot_all.fasta\" target=\"_blank\" title=\"36.630 entries\"> <img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/nextprot_all.fasta.zip\" title=\"36,630 entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/nextprot_all_decoy.fasta\" target=\"_blank\" title=\"36,630 decoy entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/nextprot_all_decoy.fasta.zip\" title=\"36,630 decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/nextprot_all_target_decoy.fasta\" target=\"_blank\" title=\"73,260 target+decoy entries\"> <img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/nextprot_all_target_decoy.fasta.zip\" title=\"73,260 target+decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\">1.456</td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/nextprot_chromosome_16.fasta\" target=\"_blank\" title=\"1,456 entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/nextprot_chromosome_16.fasta.zip\" title=\"1,456 entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/nextprot_chromosome_16_decoy.fasta\" target=\"_blank\" title=\"1,456 decoy entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/nextprot_chromosome_16_decoy.fasta.zip\" title=\"1,456 decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/nextprot_chromosome_16_target_decoy.fasta\" target=\"_blank\" title=\"2,912 target+decoy entries\"> <img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/nextprot_chromosome_16_target_decoy.fasta.zip\" title=\"2,912 target+decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n</tr>\n<tr><th scope=\"row\">UniProt/<br> Swiss-Prot<br> (2012-06-13)</th>\n<td align=\"center\" valign=\"middle\">36.852</td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human.fasta\" target=\"_blank\" title=\"36,852 entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human.fasta.zip\" title=\"36,852 entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_decoy.fasta\" target=\"_blank\" title=\"36,852 decoy entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_decoy.fasta.zip\" title=\"36,852 decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_target_decoy.fasta\" target=\"_blank\" title=\"73,704 target+decoy entries\"> <img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_target_decoy.fasta.zip\" title=\"73,704 target+decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\">790*</td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_chr16.fasta\" target=\"_blank\" title=\"790 entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_chr16.fasta.zip\" title=\"790 entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_chr16_decoy.fasta\" target=\"_blank\" title=\"790 decoy entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_chr16_decoy.fasta.zip\" title=\"790 decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_chr16_target_decoy.fasta\" target=\"_blank\" title=\"1,580 target+decoy entries\"> <img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_sprot_human_chr16_target_decoy.fasta.zip\" title=\"1,580 target+decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n</tr>\n<tr><th scope=\"row\">UniProt/<br> TrEMBL<br> (2012-06-13)</th>\n<td align=\"center\" valign=\"middle\">50.023</td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human.fasta\" target=\"_blank\" title=\"50,023 entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human.fasta.zip\" title=\"50,023 entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_decoy.fasta\" target=\"_blank\" title=\"50,023 decoy entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_decoy.fasta.zip\" title=\"50,023 decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_target_decoy.fasta\" target=\"_blank\" title=\"100,046 target+decoy entries\"> <img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_target_decoy.fasta.zip\" title=\"100,046 target+decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\">923*</td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_chr16.fasta\" target=\"_blank\" title=\"923 entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_chr16.fasta.zip\" title=\"923 entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_chr16_decoy.fasta\" target=\"_blank\" title=\"923 decoy entries\"><img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_chr16_decoy.fasta.zip\" title=\"923 decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n<td align=\"center\" valign=\"middle\"><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_chr16_target_decoy.fasta\" target=\"_blank\" title=\"1,846 target+decoy entries\"> <img alt=\"Fasta\" border=\"0\" src=\"http://community.uv.es/hpp/images/fasta.png\"></a><a href=\"http://community.uv.es/hpp/downloads/uniprot_trembl_human_chr16_target_decoy.fasta.zip\" title=\"1,846 target+decoy entries\"><img alt=\"Zip\" border=\"0\" src=\"http://community.uv.es/hpp/images/zip.png\"></a></td>\n</tr>\n</tbody>\n</table>\n<p><em>* En proceso de validación</em></p>\n<p>Para más imformación, mirar el achivo <a href=\"http://community.uv.es/hpp/downloads/log.txt\" target=\"_blank\">log.txt</a></p>');
/*!40000 ALTER TABLE `pages_richtextpage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `galleries_galleryimage`
--

DROP TABLE IF EXISTS `galleries_galleryimage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `galleries_galleryimage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `_order` int(11) DEFAULT NULL,
  `gallery_id` int(11) NOT NULL,
  `file` varchar(200) NOT NULL,
  `description` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `galleries_galleryimage_34838cc3` (`gallery_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `galleries_galleryimage`
--

LOCK TABLES `galleries_galleryimage` WRITE;
/*!40000 ALTER TABLE `galleries_galleryimage` DISABLE KEYS */;
/*!40000 ALTER TABLE `galleries_galleryimage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conf_setting`
--

DROP TABLE IF EXISTS `conf_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conf_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `value` varchar(2000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `conf_setting_6223029` (`site_id`)
) ENGINE=MyISAM AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conf_setting`
--

LOCK TABLES `conf_setting` WRITE;
/*!40000 ALTER TABLE `conf_setting` DISABLE KEYS */;
INSERT INTO `conf_setting` VALUES (1,1,'BLOG_BITLY_USER',''),(2,1,'GOOGLE_ANALYTICS_ID',''),(3,1,'COMMENTS_UNAPPROVED_VISIBLE','True'),(4,1,'BLOG_BITLY_KEY',''),(5,1,'MAX_PAGING_LINKS','10'),(6,1,'SSL_ENABLED','False'),(7,1,'COMMENTS_DISQUS_SHORTNAME',''),(8,1,'SEARCH_PER_PAGE','10'),(9,1,'SITE_TAGLINE','Spanish Human Proteome Project'),(10,1,'COMMENTS_REMOVED_VISIBLE','True'),(11,1,'SSL_FORCE_HOST',''),(12,1,'COMMENTS_NUM_LATEST','5'),(13,1,'SITE_TITLE','SHPP'),(14,1,'TAG_CLOUD_SIZES','4'),(15,1,'BLOG_POST_PER_PAGE','5'),(16,1,'COMMENTS_DISQUS_API_PUBLIC_KEY',''),(17,1,'COMMENTS_DISQUS_API_SECRET_KEY',''),(18,1,'COMMENTS_ACCOUNT_REQUIRED','True'),(19,1,'AKISMET_API_KEY',''),(20,1,'RICHTEXT_FILTER_LEVEL','2'),(21,1,'COMMENTS_DEFAULT_APPROVED','True');
/*!40000 ALTER TABLE `conf_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `generic_keyword`
--

DROP TABLE IF EXISTS `generic_keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `generic_keyword` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL,
  `title` varchar(500) NOT NULL,
  `slug` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `generic_keyword_6223029` (`site_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `generic_keyword`
--

LOCK TABLES `generic_keyword` WRITE;
/*!40000 ALTER TABLE `generic_keyword` DISABLE KEYS */;
/*!40000 ALTER TABLE `generic_keyword` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `twitter_query`
--

DROP TABLE IF EXISTS `twitter_query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `twitter_query` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(10) NOT NULL,
  `value` varchar(140) NOT NULL,
  `interested` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `twitter_query`
--

LOCK TABLES `twitter_query` WRITE;
/*!40000 ALTER TABLE `twitter_query` DISABLE KEYS */;
INSERT INTO `twitter_query` VALUES (1,'user','@emblebi',1);
/*!40000 ALTER TABLE `twitter_query` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_bookmarkitem`
--

DROP TABLE IF EXISTS `grappelli_safe_bookmarkitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_bookmarkitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bookmark_id` int(11) NOT NULL,
  `title` varchar(80) NOT NULL,
  `link` varchar(200) NOT NULL,
  `order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `grappelli_safe_bookmarkitem_424927c4` (`bookmark_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_bookmarkitem`
--

LOCK TABLES `grappelli_safe_bookmarkitem` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_bookmarkitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_bookmarkitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_navigationitem_groups`
--

DROP TABLE IF EXISTS `grappelli_safe_navigationitem_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_navigationitem_groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `navigationitem_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `navigationitem_id` (`navigationitem_id`,`group_id`),
  KEY `grappelli_safe_navigationitem_groups_77063c1` (`navigationitem_id`),
  KEY `grappelli_safe_navigationitem_groups_425ae3c4` (`group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_navigationitem_groups`
--

LOCK TABLES `grappelli_safe_navigationitem_groups` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_navigationitem_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_navigationitem_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forms_field`
--

DROP TABLE IF EXISTS `forms_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forms_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `_order` int(11) DEFAULT NULL,
  `form_id` int(11) NOT NULL,
  `label` varchar(200) NOT NULL,
  `field_type` int(11) NOT NULL,
  `required` tinyint(1) NOT NULL,
  `visible` tinyint(1) NOT NULL,
  `choices` varchar(1000) NOT NULL,
  `default` varchar(2000) NOT NULL,
  `placeholder_text` varchar(100) NOT NULL,
  `help_text` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `forms_field_1d0aabf2` (`form_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forms_field`
--

LOCK TABLES `forms_field` WRITE;
/*!40000 ALTER TABLE `forms_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `forms_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_navigationitem_users`
--

DROP TABLE IF EXISTS `grappelli_safe_navigationitem_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_navigationitem_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `navigationitem_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `navigationitem_id` (`navigationitem_id`,`user_id`),
  KEY `grappelli_safe_navigationitem_users_77063c1` (`navigationitem_id`),
  KEY `grappelli_safe_navigationitem_users_403f60f` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_navigationitem_users`
--

LOCK TABLES `grappelli_safe_navigationitem_users` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_navigationitem_users` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_navigationitem_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_admin_log`
--

DROP TABLE IF EXISTS `django_admin_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `django_admin_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action_time` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `content_type_id` int(11) DEFAULT NULL,
  `object_id` longtext,
  `object_repr` varchar(200) NOT NULL,
  `action_flag` smallint(5) unsigned NOT NULL,
  `change_message` longtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `django_admin_log_403f60f` (`user_id`),
  KEY `django_admin_log_1bb8f392` (`content_type_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_admin_log`
--

LOCK TABLES `django_admin_log` WRITE;
/*!40000 ALTER TABLE `django_admin_log` DISABLE KEYS */;
INSERT INTO `django_admin_log` VALUES (1,'2012-06-21 17:33:42',1,20,'1','Página Principal',1,''),(2,'2012-06-21 17:36:21',1,20,'2','Sobre',1,''),(3,'2012-06-21 17:37:17',1,21,'3','Grupo de Bioinformática',1,''),(4,'2012-06-21 17:38:41',1,20,'4','Bases de Datos',1,'');
/*!40000 ALTER TABLE `django_admin_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_redirect`
--

DROP TABLE IF EXISTS `django_redirect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `django_redirect` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL,
  `old_path` varchar(200) NOT NULL,
  `new_path` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `site_id` (`site_id`,`old_path`),
  KEY `django_redirect_6223029` (`site_id`),
  KEY `django_redirect_516c23f0` (`old_path`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_redirect`
--

LOCK TABLES `django_redirect` WRITE;
/*!40000 ALTER TABLE `django_redirect` DISABLE KEYS */;
/*!40000 ALTER TABLE `django_redirect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_content_type`
--

DROP TABLE IF EXISTS `django_content_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `django_content_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `app_label` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_label` (`app_label`,`model`)
) ENGINE=MyISAM AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_content_type`
--

LOCK TABLES `django_content_type` WRITE;
/*!40000 ALTER TABLE `django_content_type` DISABLE KEYS */;
INSERT INTO `django_content_type` VALUES (1,'permission','auth','permission'),(2,'group','auth','group'),(3,'user','auth','user'),(4,'content type','contenttypes','contenttype'),(5,'redirect','redirects','redirect'),(6,'session','sessions','session'),(7,'site','sites','site'),(8,'Setting','conf','setting'),(9,'Comment','generic','threadedcomment'),(10,'Keyword','generic','keyword'),(11,'assigned keyword','generic','assignedkeyword'),(12,'Rating','generic','rating'),(13,'Blog post','blog','blogpost'),(14,'Blog Category','blog','blogcategory'),(15,'Form','forms','form'),(16,'Field','forms','field'),(17,'Form entry','forms','formentry'),(18,'Form field entry','forms','fieldentry'),(19,'Page','pages','page'),(20,'Rich text page','pages','richtextpage'),(21,'Link','pages','link'),(22,'Gallery','galleries','gallery'),(23,'Image','galleries','galleryimage'),(24,'Twitter query','twitter','query'),(25,'Tweet','twitter','tweet'),(26,'migration history','south','migrationhistory'),(27,'Navigation','grappelli_safe','navigation'),(28,'Navigation Item','grappelli_safe','navigationitem'),(29,'Bookmark','grappelli_safe','bookmark'),(30,'Bookmark Item','grappelli_safe','bookmarkitem'),(31,'Help','grappelli_safe','help'),(32,'Help Entry','grappelli_safe','helpitem'),(33,'log entry','admin','logentry'),(34,'comment','comments','comment'),(35,'comment flag','comments','commentflag');
/*!40000 ALTER TABLE `django_content_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forms_form`
--

DROP TABLE IF EXISTS `forms_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forms_form` (
  `page_ptr_id` int(11) NOT NULL,
  `content` longtext NOT NULL,
  `button_text` varchar(50) NOT NULL,
  `response` longtext NOT NULL,
  `send_email` tinyint(1) NOT NULL,
  `email_from` varchar(75) NOT NULL,
  `email_copies` varchar(200) NOT NULL,
  `email_subject` varchar(200) NOT NULL,
  `email_message` longtext NOT NULL,
  PRIMARY KEY (`page_ptr_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forms_form`
--

LOCK TABLES `forms_form` WRITE;
/*!40000 ALTER TABLE `forms_form` DISABLE KEYS */;
/*!40000 ALTER TABLE `forms_form` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `generic_threadedcomment`
--

DROP TABLE IF EXISTS `generic_threadedcomment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `generic_threadedcomment` (
  `comment_ptr_id` int(11) NOT NULL,
  `by_author` tinyint(1) NOT NULL,
  `replied_to_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`comment_ptr_id`),
  KEY `generic_threadedcomment_4fd8937d` (`replied_to_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `generic_threadedcomment`
--

LOCK TABLES `generic_threadedcomment` WRITE;
/*!40000 ALTER TABLE `generic_threadedcomment` DISABLE KEYS */;
/*!40000 ALTER TABLE `generic_threadedcomment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_blogpost_categories`
--

DROP TABLE IF EXISTS `blog_blogpost_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blog_blogpost_categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `blogpost_id` int(11) NOT NULL,
  `blogcategory_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `blogpost_id` (`blogpost_id`,`blogcategory_id`),
  KEY `blog_blogpost_categories_3b7e3252` (`blogpost_id`),
  KEY `blog_blogpost_categories_10df31a4` (`blogcategory_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_blogpost_categories`
--

LOCK TABLES `blog_blogpost_categories` WRITE;
/*!40000 ALTER TABLE `blog_blogpost_categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_blogpost_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pages_page`
--

DROP TABLE IF EXISTS `pages_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pages_page` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keywords_string` varchar(500) NOT NULL,
  `site_id` int(11) NOT NULL,
  `title` varchar(500) NOT NULL,
  `slug` varchar(2000) DEFAULT NULL,
  `description` longtext NOT NULL,
  `gen_description` tinyint(1) NOT NULL,
  `status` int(11) NOT NULL,
  `publish_date` datetime DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL,
  `short_url` varchar(200) DEFAULT NULL,
  `_order` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `in_navigation` tinyint(1) NOT NULL,
  `in_footer` tinyint(1) NOT NULL,
  `titles` varchar(1000) DEFAULT NULL,
  `content_model` varchar(50) DEFAULT NULL,
  `login_required` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pages_page_6223029` (`site_id`),
  KEY `pages_page_63f17a16` (`parent_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pages_page`
--

LOCK TABLES `pages_page` WRITE;
/*!40000 ALTER TABLE `pages_page` DISABLE KEYS */;
INSERT INTO `pages_page` VALUES (1,'',1,'Página Principal','/','\nBienvenido al portal del\nProyecto Proteoma Humano (España)\nEl Proyecto Proteoma Humano (HPP) tiene como objetivo fundamental la identificación y cuantificación de todas las proteínas codificadas por los 20.300 genes contenidos en el genoma humano, en los tejidos normales y en los patológicos.',1,2,'2012-06-21 17:33:42',NULL,NULL,0,NULL,0,0,'Página Principal','richtextpage',0),(2,'',1,'Sobre','sobre','Sobre SHPP',1,2,'2012-06-21 17:36:20',NULL,NULL,1,NULL,1,1,'Sobre','richtextpage',0),(3,'',1,'Grupo de Bioinformática','http://community.uv.es/mediawiki','Grupo de Bioinformática',1,2,'2012-06-21 17:37:17',NULL,NULL,2,NULL,1,1,'Grupo de Bioinformática','link',0),(4,'',1,'Bases de Datos','bases-de-datos','Bases de Datos en formato FASTA\n\n\n Human AllChromosome 16\n#EntriesTargetDecoyTarget+ Decoy#EntriesTargetDecoyTarget+ Decoy\n\n\nneXtProt (2012-05-07)\n36.630\n \n\n \n1.456\n\n\n \n\nUniProt/ Swiss-Prot (2012-06-13)\n36.852\n\n\n \n790*\n\n\n \n\nUniProt/ TrEMBL (2012-06-13)\n50.023\n\n\n \n923*\n\n\n \n\n\n\n* En proceso de validación',1,2,'2012-06-21 17:38:41',NULL,NULL,3,NULL,1,1,'Bases de Datos','richtextpage',0);
/*!40000 ALTER TABLE `pages_page` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_helpitem`
--

DROP TABLE IF EXISTS `grappelli_safe_helpitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_helpitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `help_id` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `link` varchar(200) NOT NULL,
  `body` longtext NOT NULL,
  `order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `grappelli_safe_helpitem_4b035541` (`help_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_helpitem`
--

LOCK TABLES `grappelli_safe_helpitem` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_helpitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_helpitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forms_fieldentry`
--

DROP TABLE IF EXISTS `forms_fieldentry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forms_fieldentry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `entry_id` int(11) NOT NULL,
  `field_id` int(11) NOT NULL,
  `value` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `forms_fieldentry_38a62041` (`entry_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forms_fieldentry`
--

LOCK TABLES `forms_fieldentry` WRITE;
/*!40000 ALTER TABLE `forms_fieldentry` DISABLE KEYS */;
/*!40000 ALTER TABLE `forms_fieldentry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `south_migrationhistory`
--

DROP TABLE IF EXISTS `south_migrationhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `south_migrationhistory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(255) NOT NULL,
  `migration` varchar(255) NOT NULL,
  `applied` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `south_migrationhistory`
--

LOCK TABLES `south_migrationhistory` WRITE;
/*!40000 ALTER TABLE `south_migrationhistory` DISABLE KEYS */;
INSERT INTO `south_migrationhistory` VALUES (1,'conf','0001_initial','2012-06-21 17:31:37'),(2,'conf','0002_auto__add_field_setting_site','2012-06-21 17:31:37'),(3,'conf','0003_update_site_setting','2012-06-21 17:31:37'),(4,'conf','0004_ssl_account_settings_rename','2012-06-21 17:31:37'),(5,'core','0001_initial','2012-06-21 17:31:37'),(6,'pages','0001_initial','2012-06-21 17:31:37'),(7,'pages','0002_auto__del_field_page__keywords__add_field_page_keywords_string__chg_fi','2012-06-21 17:31:37'),(8,'blog','0001_initial','2012-06-21 17:31:37'),(9,'blog','0002_auto','2012-06-21 17:31:37'),(10,'blog','0003_categories','2012-06-21 17:31:37'),(11,'blog','0004_auto__del_field_blogpost_category','2012-06-21 17:31:37'),(12,'blog','0005_auto__del_comment__add_field_blogpost_comments_count__chg_field_blogpo','2012-06-21 17:31:37'),(13,'blog','0006_auto__del_field_blogpost__keywords__add_field_blogpost_keywords_string','2012-06-21 17:31:37'),(14,'core','0002_auto__del_keyword','2012-06-21 17:31:37'),(15,'generic','0001_initial','2012-06-21 17:31:37'),(16,'generic','0002_auto__add_keyword__add_assignedkeyword','2012-06-21 17:31:37'),(17,'generic','0003_auto__add_rating','2012-06-21 17:31:37'),(18,'generic','0004_auto__chg_field_rating_object_pk__chg_field_assignedkeyword_object_pk','2012-06-21 17:31:37'),(19,'generic','0005_keyword_site','2012-06-21 17:31:37'),(20,'generic','0006_move_keywords','2012-06-21 17:31:37'),(21,'generic','0007_auto__add_field_assignedkeyword__order','2012-06-21 17:31:37'),(22,'generic','0008_set_keyword_order','2012-06-21 17:31:37'),(23,'generic','0009_auto__chg_field_keyword_title__chg_field_keyword_slug','2012-06-21 17:31:37'),(24,'generic','0009_auto__del_field_threadedcomment_email_hash','2012-06-21 17:31:37'),(25,'generic','0010_auto__chg_field_keyword_slug__chg_field_keyword_title','2012-06-21 17:31:37'),(26,'blog','0007_auto__add_field_blogpost_site','2012-06-21 17:31:37'),(27,'blog','0008_auto__add_field_blogpost_rating_average__add_field_blogpost_rating_cou','2012-06-21 17:31:37'),(28,'blog','0009_auto__chg_field_blogpost_content','2012-06-21 17:31:37'),(29,'blog','0010_category_site_allow_comments','2012-06-21 17:31:37'),(30,'blog','0011_comment_site_data','2012-06-21 17:31:37'),(31,'blog','0012_auto__add_field_blogpost_featured_image','2012-06-21 17:31:37'),(32,'blog','0013_auto__chg_field_blogpost_featured_image','2012-06-21 17:31:37'),(33,'blog','0014_auto__add_field_blogpost_gen_description','2012-06-21 17:31:37'),(34,'blog','0015_auto__chg_field_blogcategory_title__chg_field_blogcategory_slug__chg_f','2012-06-21 17:31:37'),(35,'forms','0001_initial','2012-06-21 17:31:37'),(36,'forms','0002_auto__add_field_field_placeholder_text','2012-06-21 17:31:37'),(37,'forms','0003_auto__chg_field_field_field_type','2012-06-21 17:31:37'),(38,'forms','0004_auto__chg_field_form_response__chg_field_form_content','2012-06-21 17:31:37'),(39,'forms','0005_auto__chg_field_fieldentry_value','2012-06-21 17:31:37'),(40,'pages','0003_auto__add_field_page_site','2012-06-21 17:31:37'),(41,'pages','0004_auto__del_contentpage__add_richtextpage','2012-06-21 17:31:37'),(42,'pages','0005_rename_contentpage','2012-06-21 17:31:37'),(43,'pages','0006_auto__add_field_page_gen_description','2012-06-21 17:31:37'),(44,'pages','0007_auto__chg_field_page_slug__chg_field_page_title','2012-06-21 17:31:37'),(45,'pages','0008_auto__add_link','2012-06-21 17:31:37'),(46,'galleries','0001_initial','2012-06-21 17:31:37'),(47,'twitter','0001_initial','2012-06-21 17:31:37'),(48,'twitter','0002_auto__chg_field_query_value','2012-06-21 17:31:37');
/*!40000 ALTER TABLE `south_migrationhistory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_comments`
--

DROP TABLE IF EXISTS `django_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `django_comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content_type_id` int(11) NOT NULL,
  `object_pk` longtext NOT NULL,
  `site_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(50) NOT NULL,
  `user_email` varchar(75) NOT NULL,
  `user_url` varchar(200) NOT NULL,
  `comment` longtext NOT NULL,
  `submit_date` datetime NOT NULL,
  `ip_address` char(15) DEFAULT NULL,
  `is_public` tinyint(1) NOT NULL,
  `is_removed` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `django_comments_1bb8f392` (`content_type_id`),
  KEY `django_comments_6223029` (`site_id`),
  KEY `django_comments_403f60f` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_comments`
--

LOCK TABLES `django_comments` WRITE;
/*!40000 ALTER TABLE `django_comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `django_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `galleries_gallery`
--

DROP TABLE IF EXISTS `galleries_gallery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `galleries_gallery` (
  `page_ptr_id` int(11) NOT NULL,
  `content` longtext NOT NULL,
  `zip_import` varchar(100) NOT NULL,
  PRIMARY KEY (`page_ptr_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `galleries_gallery`
--

LOCK TABLES `galleries_gallery` WRITE;
/*!40000 ALTER TABLE `galleries_gallery` DISABLE KEYS */;
/*!40000 ALTER TABLE `galleries_gallery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_help`
--

DROP TABLE IF EXISTS `grappelli_safe_help`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_help` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_help`
--

LOCK TABLES `grappelli_safe_help` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_help` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_help` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_user`
--

DROP TABLE IF EXISTS `auth_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auth_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `email` varchar(75) NOT NULL,
  `password` varchar(128) NOT NULL,
  `is_staff` tinyint(1) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `is_superuser` tinyint(1) NOT NULL,
  `last_login` datetime NOT NULL,
  `date_joined` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_user`
--

LOCK TABLES `auth_user` WRITE;
/*!40000 ALTER TABLE `auth_user` DISABLE KEYS */;
INSERT INTO `auth_user` VALUES (1,'ali3n','','','ogallardo@proteored.org','pbkdf2_sha256$10000$WGDVbwz87p2S$cIXd8f61YZW2E86ArSwMt2qCDSWPNJBAkH/jLXoxtHg=',1,1,1,'2012-06-21 17:32:06','2012-06-21 17:31:25');
/*!40000 ALTER TABLE `auth_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `twitter_tweet`
--

DROP TABLE IF EXISTS `twitter_tweet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `twitter_tweet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `remote_id` varchar(50) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `text` longtext,
  `profile_image_url` varchar(200) DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `retweeter_profile_image_url` varchar(200) DEFAULT NULL,
  `retweeter_user_name` varchar(100) DEFAULT NULL,
  `retweeter_full_name` varchar(100) DEFAULT NULL,
  `query_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `twitter_tweet_2e02fd9f` (`query_id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `twitter_tweet`
--

LOCK TABLES `twitter_tweet` WRITE;
/*!40000 ALTER TABLE `twitter_tweet` DISABLE KEYS */;
INSERT INTO `twitter_tweet` VALUES (1,'215556361503772673','2012-06-20 20:26:56','RT <a href=\"http://twitter.com/emblebies\">@emblebies</a>: Interpro release 38 data has been loaded into InterProScan 4 at <a href=\"http://t.co/dEQvvZ0J\">http://t.co/dEQvvZ0J</a>. Enjoy!','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(2,'215545771167186944','2012-06-20 19:44:51','Register now for the EBI half-day special-interest symposium \"Getting the most out of your data\" at <a href=\"http://twitter.com/search?q=%23EMBO2012\">#EMBO2012</a>: <a href=\"http://t.co/VTo3Tjx4\">http://t.co/VTo3Tjx4</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(3,'215376033900269568','2012-06-19 15:13:26','\"Story behind the paper\" on quality of computationally inferred gene ontology <a href=\"http://t.co/thuaaNAD\">http://t.co/thuaaNAD</a> <a href=\"http://twitter.com/phylogenomics\">@phylogenomics</a> <a href=\"http://twitter.com/iddux\">@iddux</a> <a href=\"http://twitter.com/larsjuhljensen\">@larsjuhljensen</a>','http://a0.twimg.com/profile_images/1467970873/photo_normal.png','cdessimoz','Christophe Dessimoz','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',1),(4,'215010938653380609','2012-06-19 08:19:37','RT <a href=\"http://twitter.com/ensembl\">@ensembl</a>: Heads up: BLAST / BLAT will be unavailable for a short time on 20 June - <a href=\"http://t.co/5A4GGDNE\">http://t.co/5A4GGDNE</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(5,'215010631106035715','2012-06-19 08:18:23','<a href=\"http://twitter.com/BioMickWatson\">@BioMickWatson</a> No, but you can search SRA by organism, library source or platform. Also see EBI domain-specific search: <a href=\"http://t.co/PxStGt3l\">http://t.co/PxStGt3l</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(6,'214851170802483200','2012-06-18 21:44:45','RT <a href=\"http://twitter.com/intact_project\">@intact_project</a>: IntAct 154 is out! 296,803 binary interactions from 5,489 publications. ftp://ftp.ebi.ac.uk/pub/databases/intact/current','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(7,'214850766089895936','2012-06-18 21:43:09','MT <a href=\"http://twitter.com/Chembl\">@Chembl</a>: Teach-Discover-Treat competition: fill gaps in drug discovery education &amp; treatments for neglected diseases <a href=\"http://t.co/1pAxgDKc\">http://t.co/1pAxgDKc</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(8,'214849525985193984','2012-06-18 21:38:13','Hands-on training: Apply by 6 July for the Joint EMBL-EBI - <a href=\"http://twitter.com/WTcourses\">@WTcourses</a> workshop on Proteomics Bioinformatics  - <a href=\"http://t.co/8IveV6QH\">http://t.co/8IveV6QH</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(9,'214663646675079168','2012-06-18 09:19:36','Congratulations to Director Janet Thornton for receiving a DBE in recognition of her contributions to bioinformatics! <a href=\"http://t.co/9WKWxK5m\">http://t.co/9WKWxK5m</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(10,'213623522482864129','2012-06-15 12:26:31','Release 112 of the EMBL Nucleotide Sequence Database.: Release 112 is now ready and available on the EBI public ... <a href=\"http://t.co/qk4KYafw\">http://t.co/qk4KYafw</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(11,'213545087345496065','2012-06-15 07:14:50','RT <a href=\"http://twitter.com/UKPMCUpdates\">@UKPMCUpdates</a>: <a href=\"http://twitter.com/search?q=%23Willetts\">#Willetts</a>: Crucial future developments will now come from handling large datasets. <a href=\"http://twitter.com/search?q=%23CheltSciFest\">#CheltSciFest</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(12,'213287847862599680','2012-06-14 14:12:40','RT <a href=\"http://twitter.com/BBSRC\">@BBSRC</a>: Work begins on groundbreaking for a new <a href=\"http://twitter.com/search?q=%23bioinformatics\">#bioinformatics</a> facility on the Genome Campus - <a href=\"http://t.co/AsAU80Cu\">http://t.co/AsAU80Cu</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(13,'213287278179663872','2012-06-14 14:06:42','UniProt 2012_06 is out. Get your 23,196,958 protein records today!','http://a0.twimg.com/profile_images/1831415557/uniprot-rgb_UP_version_v3_normal.png','uniprot','UniProt','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',1),(14,'213223520551960576','2012-06-14 09:57:03','RT <a href=\"http://twitter.com/jamesmalone\">@jamesmalone</a>: URIGen ensures unqiue URIs are created across mutliple ontology editors... <a href=\"http://t.co/HOfSRI6O\">http://t.co/HOfSRI6O</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(15,'213219634684497920','2012-06-14 09:41:36','MT <a href=\"http://twitter.com/ewanbirney\">@ewanbirney</a>: ENA/Genbank/DDBJ is one of the longest and largest global public <a href=\"http://twitter.com/search?q=%23DNA\">#DNA</a> sequence archives; supports all sorts of life science.','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(16,'213215865217359872','2012-06-14 09:26:38','RT <a href=\"http://twitter.com/jamesmalone\">@jamesmalone</a>: Common Ontology Questions <a href=\"http://twitter.com/search?q=%231\">#1</a>: What is it you do again? Some answers for colleagues and family - <a href=\"http://t.co/LJn0ooLW\">http://t.co/LJn0ooLW</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(17,'212971960941019136','2012-06-13 17:17:26','RT <a href=\"http://twitter.com/sysbiomed\">@sysbiomed</a>: Up for a systems biology challenge? New DREAM 7 challenges released: “<a href=\"http://twitter.com/DR_E_A_M\">@DR_E_A_M</a>: <a href=\"http://t.co/VmEpGP6p%E2%80%9D\">http://t.co/VmEpGP6p”</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(18,'212959588771831808','2012-06-13 16:28:17','New quick tour in Train online: Database of Genomic Variants archive, a public catalog of structural variation data - <a href=\"http://t.co/zPojnEEu\">http://t.co/zPojnEEu</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(19,'212959385331302400','2012-06-13 16:27:28','New quick tour in Train online: Reactome provides access to quality-assured human biological pathway diagrams - <a href=\"http://t.co/jypuyzKc\">http://t.co/jypuyzKc</a>','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1),(20,'212959266020139009','2012-06-13 16:27:00','Thanks to everyone who came to our ground-breaking ceremony today - esp. Oxford Archaeology East for showing us the history of the site!','http://a0.twimg.com/profile_images/1831046396/EBI_spot_logo_clear_background_normal.jpg','emblebi','EMBL-EBI',NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `twitter_tweet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grappelli_safe_bookmark`
--

DROP TABLE IF EXISTS `grappelli_safe_bookmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grappelli_safe_bookmark` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `grappelli_safe_bookmark_403f60f` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grappelli_safe_bookmark`
--

LOCK TABLES `grappelli_safe_bookmark` WRITE;
/*!40000 ALTER TABLE `grappelli_safe_bookmark` DISABLE KEYS */;
/*!40000 ALTER TABLE `grappelli_safe_bookmark` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-06-25 18:20:33
