CREATE DATABASE `lifeForce` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `blobStorage` (
  `blobStorageId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` varchar(100) NOT NULL,
  `caption` varchar(45) NOT NULL,
  `img` longblob NOT NULL,
  `contentLength` int(10) unsigned NOT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `lastModifiedBy` varchar(45) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`blobStorageId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
