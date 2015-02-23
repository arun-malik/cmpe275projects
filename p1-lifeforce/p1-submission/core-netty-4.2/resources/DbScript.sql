
CREATE DATABASE `cmpe275` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `mapper` (
  `idmapper` INT NOT NULL AUTO_INCREMENT,
  `nodeid` INT NOT NULL,
  `uuid` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`idmapper`));

  
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
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;


CREATE TABLE `clusterMapper` (
  `clusterId` int(11) NOT NULL,
  `leaderHostAddress` varchar(45) NOT NULL,
  `port` int(11) DEFAULT NULL,
  PRIMARY KEY (`clusterId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
