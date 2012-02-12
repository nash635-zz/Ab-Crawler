# MySQL-Front 5.1  (Build 4.2)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


# Host: localhost    Database: yamaloo
# ------------------------------------------------------
# Server version 5.1.52-community

#
# Source for table site
#

DROP TABLE IF EXISTS `site`;
CREATE TABLE `site` (
  `SiteID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `DirectoryName` varchar(300) NOT NULL,
  `Enabled` tinyint(1) NOT NULL,
  `Interval` int(11) NOT NULL,
  `LastRunTime` datetime DEFAULT '0000-00-00 00:00:00',
  `NextRunTime` datetime DEFAULT '0000-00-00 00:00:00',
  `Priority` int(11) NOT NULL,
  PRIMARY KEY (`SiteID`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;

#
# Dumping data for table site
#

LOCK TABLES `site` WRITE;
/*!40000 ALTER TABLE `site` DISABLE KEYS */;
INSERT INTO `site` VALUES (1,'Only','Only',0,1,'2011-09-08 02:44:04','2011-09-08 02:45:04',1000);
INSERT INTO `site` VALUES (2,'jackjones','jackjones',0,1,'2011-09-18 00:47:29','2011-09-18 00:48:29',1000);
INSERT INTO `site` VALUES (3,'fairwhale','fairwhale',0,1,'2011-09-18 01:48:26','2011-09-18 01:49:26',1000);
INSERT INTO `site` VALUES (4,'Gap','Gap',0,1,'2011-09-18 07:34:28','2011-09-18 07:35:28',1000);
INSERT INTO `site` VALUES (5,'yishion','yishion',0,1,'2011-09-18 02:24:22','2011-09-18 02:25:22',1000);
INSERT INTO `site` VALUES (6,'jeanswest','jeanswest',0,1,'2011-09-18 02:49:18','2011-09-18 02:50:18',1000);
INSERT INTO `site` VALUES (7,'espritpx','espritpx',0,1,'2011-09-18 04:14:55','2011-09-18 04:15:55',1000);
INSERT INTO `site` VALUES (8,'nauticabz','nauticabz',0,1,'2011-09-18 04:38:19','2011-09-18 04:39:19',1000);
INSERT INTO `site` VALUES (9,'uniqlo','uniqlo',0,1,'2011-09-18 04:47:59','2011-09-18 04:48:59',1000);
INSERT INTO `site` VALUES (10,'ochirly','ochirly',0,1,'2011-09-18 06:05:50','2011-09-18 06:06:50',1000);
INSERT INTO `site` VALUES (11,'veromoda','veromoda',0,1,'2011-09-18 22:12:04','2011-09-18 22:13:04',1000);
INSERT INTO `site` VALUES (12,'itokin','itokin',0,1,'2011-09-19 01:29:40','2011-09-19 01:30:40',1000);
INSERT INTO `site` VALUES (13,'disneyfashion','disneyfashion',0,1,'2011-09-19 01:59:34','2011-09-19 02:00:34',1000);
INSERT INTO `site` VALUES (14,'adidas','adidas',0,1,'2011-09-19 02:23:49','2011-09-19 02:24:49',1000);
INSERT INTO `site` VALUES (15,'kappa','kappa',0,1,'2011-09-19 04:19:35','2011-09-19 04:20:35',1000);
INSERT INTO `site` VALUES (16,'lining','lining',0,1,'2011-09-19 05:07:41','2011-09-19 05:08:41',1000);
INSERT INTO `site` VALUES (17,'reebok','reebok',0,1,'2011-09-19 07:40:20','2011-09-19 07:41:20',1000);
INSERT INTO `site` VALUES (18,'newbalance','newbalance',0,1,'2011-09-19 09:06:52','2011-09-19 09:07:52',1000);
INSERT INTO `site` VALUES (19,'camelsports','camelsports',0,1,'2011-09-19 09:06:52','2011-09-19 09:07:52',1000);
INSERT INTO `site` VALUES (20,'toread','toread',0,1,'2011-09-19 21:13:08','2011-09-19 21:14:08',1000);
INSERT INTO `site` VALUES (21,'ckjeansytc','ckjeansytc',0,1,'2011-10-20 05:00:10','2011-10-20 05:01:10',1000);
INSERT INTO `site` VALUES (22,'donoraticobyby','donoraticobyby',0,1,'2011-10-20 05:22:53','2011-10-20 05:23:53',1000);
INSERT INTO `site` VALUES (23,'jnby','jnby',0,1,'2011-10-20 05:39:06','2011-10-20 05:40:06',1000);
INSERT INTO `site` VALUES (24,'lapargay','lapargay',0,1,'2011-10-20 07:00:31','2011-10-20 07:01:31',1000);
INSERT INTO `site` VALUES (25,'levis','levis',0,1,'2011-10-20 06:43:41','2011-10-20 06:44:41',1000);
INSERT INTO `site` VALUES (26,'mango','mango',0,1,'2011-10-20 07:00:31','2011-10-20 07:01:31',1000);
INSERT INTO `site` VALUES (27,'metersbonwe','metersbonwe',0,1,'2011-10-21 07:19:12','2011-10-21 07:20:12',1000);
INSERT INTO `site` VALUES (28,'ninewest','ninewest',0,1,'2011-10-21 07:19:12','2011-10-21 07:20:12',1000);
INSERT INTO `site` VALUES (29,'victoriassecretsm','victoriassecretsm',0,1,'2011-10-23 03:51:46','2011-10-23 03:52:46',1000);
INSERT INTO `site` VALUES (30,'thethingtq','thethingtq',0,1,'2011-10-20 07:40:26','2011-10-20 07:41:26',1000);
INSERT INTO `site` VALUES (31,'giordano','giordano',0,1,'2011-10-23 07:29:40','2011-10-23 07:30:40',1000);
INSERT INTO `site` VALUES (32,'aiken','aiken',0,1,'2011-10-23 22:54:33','2011-10-23 22:55:33',1000);
INSERT INTO `site` VALUES (33,'aimer','aimer',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (34,'antszone','antszone',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (35,'ayilian','ayilian',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (36,'basichouse','basichouse',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (37,'bisalove','bisalove',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (38,'bisoubisou','bisoubisou',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (39,'busen','busen',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (40,'bosideng','bosideng',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (41,'broadcast','broadcast',0,1,'2011-10-23 22:55:53','2011-10-23 22:56:53',1000);
INSERT INTO `site` VALUES (42,'chaber','chaber',0,1,'2011-10-23 22:55:54','2011-10-23 22:56:54',1000);
INSERT INTO `site` VALUES (43,'cocoon','cocoon',0,1,'2011-10-23 23:00:13','2011-10-23 23:01:13',1000);
INSERT INTO `site` VALUES (44,'dwolvesfs','dwolvesfs',0,1,'2011-10-23 23:00:13','2011-10-23 23:01:13',1000);
INSERT INTO `site` VALUES (45,'daphne','daphne',0,1,'2011-10-23 23:00:13','2011-10-23 23:01:13',1000);
INSERT INTO `site` VALUES (46,'dearvene','dearvene',0,1,'2011-10-23 23:00:13','2011-10-23 23:01:13',1000);
INSERT INTO `site` VALUES (47,'dianes','dianes',0,1,'2011-10-23 23:00:13','2011-10-23 23:01:13',1000);
INSERT INTO `site` VALUES (48,'duocaish','duocaish',0,1,'2011-10-23 23:00:14','2011-10-23 23:01:14',1000);
INSERT INTO `site` VALUES (49,'dodococo','dodococo',0,1,'2011-10-24 02:53:26','2011-10-24 02:54:26',1000);
INSERT INTO `site` VALUES (50,'exronline','exronline',0,1,'2011-10-24 03:29:52','2011-10-24 03:30:52',1000);
INSERT INTO `site` VALUES (51,'fairyfair','fairyfair',0,1,'2011-10-24 04:09:42','2011-10-24 04:10:42',1000);
INSERT INTO `site` VALUES (52,'fiveplus','fiveplus',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (53,'imdavid','imdavid',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (54,'itisf4','itisf4',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (55,'jasonwood','jasonwood',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (56,'mindbridge','mindbridge',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (57,'inman','inman',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (58,'naturalelement','naturalelement',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (59,'othermix','othermix',0,1,'2011-10-24 08:45:44','2011-10-24 08:46:44',1000);
INSERT INTO `site` VALUES (60,'xhfsfs1','xhfsfs1',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (61,'sankuanz','sankuanz',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (62,'sasa','sasa',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (63,'semirfashion','semirfashion',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (64,'tonlion','tonlion',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (65,'turnsignal','turnsignal',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (66,'u1club','u1club',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (67,'yuhan','yuhan',0,1,'2011-10-24 08:45:45','2011-10-24 08:46:45',1000);
INSERT INTO `site` VALUES (68,'etam','etam',0,1,'2011-10-31 01:53:26','2011-10-31 01:54:26',1000);
INSERT INTO `site` VALUES (69,'quiksilver','quiksilver',0,1,'2011-10-31 04:42:34','2011-10-31 04:43:34',1000);
INSERT INTO `site` VALUES (70,'sephora','sephora',0,1,'2011-10-31 04:43:51','2011-10-31 04:44:51',1000);
INSERT INTO `site` VALUES (71,'zara','zara',0,1,'2011-11-02 03:24:55','2011-11-02 03:25:55',1000);
INSERT INTO `site` VALUES (72,'onet','onet',0,1,'2011-11-27 03:51:53','2011-11-27 03:52:53',1000);
INSERT INTO `site` VALUES (73,'d2c','d2c',0,1,'2011-11-27 04:32:50','2011-11-27 04:33:50',1000);
INSERT INTO `site` VALUES (74,'puma','puma',0,1,'2011-11-27 05:24:46','2011-11-27 05:25:46',1000);
INSERT INTO `site` VALUES (75,'benefit','benefit',0,1,'2011-11-27 06:00:08','2011-11-27 06:01:08',1000);
INSERT INTO `site` VALUES (76,'bossini','bossini',0,1,'2011-11-27 07:09:45','2011-11-27 07:10:45',1000);
INSERT INTO `site` VALUES (77,'honeys','honeys',0,1,'2011-11-27 08:56:52','2011-11-27 08:57:52',1000);
INSERT INTO `site` VALUES (78,'hotwind','hotwind',0,1,'2011-11-27 08:56:52','2011-11-27 08:57:52',1000);
INSERT INTO `site` VALUES (79,'masamaso','masamaso',0,1,'2011-11-27 08:56:52','2011-11-27 08:57:52',1000);
INSERT INTO `site` VALUES (80,'woo','woo',0,1,'2011-11-27 08:56:53','2011-11-27 08:57:53',1000);
INSERT INTO `site` VALUES (81,'vivian','vivian',0,1,'2011-11-27 08:56:53','2011-11-27 08:57:53',1000);
INSERT INTO `site` VALUES (82,'xiu','xiu',0,1,'2011-11-27 08:56:53','2011-11-27 08:57:53',1000);
INSERT INTO `site` VALUES (83,'novome','novome',0,1,'2011-11-28 03:32:33','2011-11-28 03:33:33',1000);
INSERT INTO `site` VALUES (84,'columbia','columbia',0,1,'2011-11-28 22:36:22','2011-11-28 22:37:22',1000);
INSERT INTO `site` VALUES (85,'colantotte','colantotte',0,1,'2011-11-29 00:49:36','2011-11-29 00:50:36',1000);
INSERT INTO `site` VALUES (86,'couzzle','couzzle',0,1,'2011-11-29 01:04:26','2011-11-29 01:05:26',1000);
INSERT INTO `site` VALUES (87,'teenieweenie','teenieweenie',1,1,'2011-11-30 07:21:27','2011-11-30 07:22:27',1000);
INSERT INTO `site` VALUES (88,'thenorthface','thenorthface',0,1,'2011-11-29 06:06:28','2011-11-29 06:07:28',1000);
/*!40000 ALTER TABLE `site` ENABLE KEYS */;
UNLOCK TABLES;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
