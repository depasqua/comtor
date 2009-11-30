DROP TABLE IF EXISTS `schema_version`;
CREATE TABLE `schema_version` (`version` varchar(64)) ENGINE=InnoDB;
INSERT INTO `schema_version` VALUES ('1.2');
