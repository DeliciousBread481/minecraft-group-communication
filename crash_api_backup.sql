-- MySQL dump 10.13  Distrib 8.4.5, for Win64 (x86_64)
--
-- Host: localhost    Database: crash_api
-- ------------------------------------------------------
-- Server version	8.4.5

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
-- Table structure for table `admin_applications`
--

DROP TABLE IF EXISTS `admin_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请记录ID',
  `user_id` bigint NOT NULL COMMENT '申请人ID',
  `status` enum('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
  `reason` text COMMENT '申请理由',
  `feedback` text COMMENT '处理反馈',
  `processor_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_apps_user` (`user_id`),
  KEY `idx_admin_apps_status` (`status`),
  KEY `idx_admin_apps_processor` (`processor_id`),
  CONSTRAINT `admin_applications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员权限申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_applications`
--

LOCK TABLES `admin_applications` WRITE;
/*!40000 ALTER TABLE `admin_applications` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement_categories`
--

DROP TABLE IF EXISTS `announcement_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement_categories` (
  `id` varchar(50) NOT NULL COMMENT '分类ID(如ann_crash)',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort_order` smallint NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ann_cats_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_categories`
--

LOCK TABLES `announcement_categories` WRITE;
/*!40000 ALTER TABLE `announcement_categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement_images`
--

DROP TABLE IF EXISTS `announcement_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement_images` (
  `item_id` int NOT NULL COMMENT '关联内容项ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `caption` varchar(255) DEFAULT NULL COMMENT '图片说明',
  PRIMARY KEY (`item_id`),
  KEY `idx_ann_images_item` (`item_id`),
  CONSTRAINT `announcement_images_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `announcement_items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_images`
--

LOCK TABLES `announcement_images` WRITE;
/*!40000 ALTER TABLE `announcement_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement_items`
--

DROP TABLE IF EXISTS `announcement_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` varchar(50) NOT NULL COMMENT '关联分类ID',
  `item_type` enum('TEXT','IMAGE') NOT NULL DEFAULT 'TEXT' COMMENT '内容类型',
  `sort_order` smallint NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updated_by` bigint NOT NULL COMMENT '最后更新者ID',
  PRIMARY KEY (`id`),
  KEY `last_updated_by` (`last_updated_by`),
  KEY `idx_ann_items_category` (`category_id`),
  KEY `idx_ann_items_order` (`sort_order`),
  CONSTRAINT `announcement_items_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `announcement_categories` (`id`) ON DELETE CASCADE,
  CONSTRAINT `announcement_items_ibfk_2` FOREIGN KEY (`last_updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告内容项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_items`
--

LOCK TABLES `announcement_items` WRITE;
/*!40000 ALTER TABLE `announcement_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement_texts`
--

DROP TABLE IF EXISTS `announcement_texts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement_texts` (
  `item_id` int NOT NULL COMMENT '关联内容项ID',
  `content` text NOT NULL COMMENT '文本内容',
  PRIMARY KEY (`item_id`),
  KEY `idx_ann_texts_item` (`item_id`),
  CONSTRAINT `announcement_texts_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `announcement_items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告文本内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_texts`
--

LOCK TABLES `announcement_texts` WRITE;
/*!40000 ALTER TABLE `announcement_texts` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement_texts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcements`
--

DROP TABLE IF EXISTS `announcements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcements` (
  `id` varchar(50) NOT NULL COMMENT '公告ID',
  `title` varchar(255) NOT NULL DEFAULT '群规及问题指南' COMMENT '公告标题',
  `last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `last_updated_by` bigint NOT NULL COMMENT '最后更新者ID',
  PRIMARY KEY (`id`),
  KEY `last_updated_by` (`last_updated_by`),
  CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`last_updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcements`
--

LOCK TABLES `announcements` WRITE;
/*!40000 ALTER TABLE `announcements` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` varchar(50) NOT NULL COMMENT '分类ID(如startup)',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标组件名',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `color` varchar(20) DEFAULT NULL COMMENT '主题颜色',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问题分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_USER',NULL),(2,'ROLE_ADMIN',NULL);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solution_images`
--

DROP TABLE IF EXISTS `solution_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solution_images` (
  `id` int NOT NULL AUTO_INCREMENT,
  `solution_id` varchar(50) NOT NULL COMMENT '关联方案ID',
  `image_order` tinyint unsigned NOT NULL COMMENT '图片序号',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  PRIMARY KEY (`id`),
  KEY `idx_images_solution` (`solution_id`),
  CONSTRAINT `solution_images_ibfk_1` FOREIGN KEY (`solution_id`) REFERENCES `solutions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='解决方案截图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution_images`
--

LOCK TABLES `solution_images` WRITE;
/*!40000 ALTER TABLE `solution_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `solution_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solution_steps`
--

DROP TABLE IF EXISTS `solution_steps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solution_steps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `solution_id` varchar(50) NOT NULL COMMENT '关联方案ID',
  `step_order` tinyint unsigned NOT NULL COMMENT '步骤序号(1起)',
  `content` text NOT NULL COMMENT '步骤内容',
  PRIMARY KEY (`id`),
  KEY `idx_steps_solution` (`solution_id`),
  CONSTRAINT `solution_steps_ibfk_1` FOREIGN KEY (`solution_id`) REFERENCES `solutions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='解决方案步骤表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution_steps`
--

LOCK TABLES `solution_steps` WRITE;
/*!40000 ALTER TABLE `solution_steps` DISABLE KEYS */;
/*!40000 ALTER TABLE `solution_steps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solutions`
--

DROP TABLE IF EXISTS `solutions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solutions` (
  `id` varchar(50) NOT NULL COMMENT '方案ID(如s1)',
  `category_id` varchar(50) NOT NULL COMMENT '关联分类ID',
  `title` varchar(255) NOT NULL COMMENT '解决方案标题',
  `difficulty` enum('简单','中等','困难') NOT NULL DEFAULT '中等' COMMENT '解决难度',
  `version` varchar(50) NOT NULL COMMENT '适用MC版本',
  `description` text NOT NULL COMMENT '问题描述',
  `notes` text COMMENT '补充说明',
  `status` enum('草稿','待审核','已发布') DEFAULT '待审核' COMMENT '审核状态',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `reviewed_by` bigint DEFAULT NULL COMMENT '审核者ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `reviewed_by` (`reviewed_by`),
  KEY `idx_solutions_status` (`status`),
  KEY `idx_solutions_category` (`category_id`),
  KEY `idx_solutions_search` (`category_id`,`status`,`updated_at`),
  CONSTRAINT `solutions_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `solutions_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `solutions_ibfk_3` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='解决方案主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solutions`
--

LOCK TABLES `solutions` WRITE;
/*!40000 ALTER TABLE `solutions` DISABLE KEYS */;
/*!40000 ALTER TABLE `solutions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `idx_user_roles_user` (`user_id`),
  KEY `idx_user_roles_role` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,1);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `refresh_token` varchar(255) DEFAULT NULL COMMENT '刷新令牌',
  `refresh_token_expiry` datetime DEFAULT NULL COMMENT '刷新令牌过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'DeliciousBread','3574467868@qq.com','$2a$10$eGZX82R31pdnOWn91XCtD.ZifRfphK9JEygVO2vK03buNev1idVji',NULL,'2025-07-16 08:05:32','2025-07-16 08:05:32',1,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEZWxpY2lvdXNCcmVhZCIsImlhdCI6MTc1Mjc0NjUxNywiZXhwIjoxNzUzMzUxMzE3fQ.pf6Zq3dZdgkeEGzADCHbeRjLM-U5wA8nbISlzexnqfM','2025-07-24 18:01:57');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-11 18:53:23
