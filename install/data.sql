-- MySQL dump 10.11
--
-- Host: localhost    Database: comtor_dev
-- ------------------------------------------------------
-- Server version	5.0.51a-3ubuntu5.4

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
-- Table structure for table `doclets`
--

DROP TABLE IF EXISTS `doclets`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `doclets` (
  `docletId` int(11) NOT NULL auto_increment COMMENT 'The Id of the doclet',
  `docletName` varchar(255) NOT NULL COMMENT 'The name of the doclet',
  `docletDescription` varchar(255) NOT NULL COMMENT 'The description of the doclet',
  `javaName` varchar(256) NOT NULL COMMENT 'The name of the java class',
  PRIMARY KEY  (`docletId`),
  UNIQUE KEY `reportName` (`docletName`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `doclets`
--

/*!40000 ALTER TABLE `doclets` DISABLE KEYS */;
INSERT INTO `doclets` VALUES (1,'Check for Tags','Check for the proper use of returns, throw, and param tags.','comtor.analyzers.CheckForTags'),(2,'Comment Average Ratio','Calculate the length of each method\'s comments in a class. If you are not getting full credit, try increasing the length of your comments.','comtor.analyzers.CommentAvgRatio'),(3,'Percentage Methods','Calculate the percentage of commented methods per class.','comtor.analyzers.PercentageMethods'),(16,'Method Interactions','Creates a method call graph for each user defined method. This does not count towards the overall grade.','comtor.analyzers.Interactions'),(17,'Spell Checker','Checks the spelling in comments. (Note:  There may be some words that are spelled correctly, marked incorrect because they are not present in the dictionary.)','comtor.analyzers.SpellCheck');
/*!40000 ALTER TABLE `doclets` ENABLE KEYS */;

--
-- Table structure for table `docletGradeSections`
--

DROP TABLE IF EXISTS `docletGradeSections`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `docletGradeSections` (
  `docletGradeSectionId` int(11) NOT NULL auto_increment,
  `docletId` int(11) NOT NULL,
  `sectionName` varchar(64) NOT NULL,
  `sectionDesc` varchar(512) NOT NULL,
  PRIMARY KEY  (`docletGradeSectionId`),
  KEY `docletId` (`docletId`),
  CONSTRAINT `docletGradeSections_ibfk_1` FOREIGN KEY (`docletId`) REFERENCES `doclets` (`docletId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `docletGradeSections`
--

/*!40000 ALTER TABLE `docletGradeSections` DISABLE KEYS */;
INSERT INTO `docletGradeSections` VALUES (1,1,'Return','Checks that return tags are present and used properly.'),(2,1,'Param','Checks that param tags are present and used properly.'),(3,1,'Throws','Checks that throws tags are present and used properly.'),(4,2,'Average','Averages the length of comments preceding methods. If comments are not of sufficient length full credit will not be awarde.'),(5,3,'Percent','Percent of methods that contain comments.'),(18,17,'Spelling','Checks spelling.'),(21,1,'Author','Checks that author tags are used properly.'),(22,1,'Version','Checks that version tags are used properly.'),(23,1,'See','Checks that see tags are used properly.'),(24,1,'Since','Checks that since tags are used properly.');
/*!40000 ALTER TABLE `docletGradeSections` ENABLE KEYS */;

--
-- Table structure for table `docletGradeParameters`
--

DROP TABLE IF EXISTS `docletGradeParameters`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `docletGradeParameters` (
  `docletGradeParameterId` int(11) NOT NULL auto_increment,
  `docletId` int(11) NOT NULL,
  `parameterName` varchar(64) NOT NULL,
  `parameterDesc` varchar(512) NOT NULL,
  PRIMARY KEY  (`docletGradeParameterId`),
  KEY `docletId` (`docletId`),
  CONSTRAINT `docletGradeParameters_ibfk_1` FOREIGN KEY (`docletId`) REFERENCES `doclets` (`docletId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `docletGradeParameters`
--

/*!40000 ALTER TABLE `docletGradeParameters` DISABLE KEYS */;
INSERT INTO `docletGradeParameters` VALUES (1,2,'min_ratio','Minimum average number of words per comment in order to get full credit.'),(5,17,'Valid Words','A space separated list of words that are to be considered correctly spelled.'),(6,1,'Author','Space separated list of OVERVIEW and CLASS indicating were to require this tag. (Leave Blank if not required)'),(7,1,'Version','Space separated list of OVERVIEW and CLASS indicating were to require this tag. (Leave Blank if not required)'),(8,1,'See','Space separated list of OVERVIEW, CLASS, FIELD, and METHOD indicating were to require this tag. (Leave Blank if not required)'),(9,1,'Since','Space separated list of OVERVIEW, CLASS, FIELD, and METHOD indicating were to require this tag. (Leave Blank if not required)');
/*!40000 ALTER TABLE `docletGradeParameters` ENABLE KEYS */;

--
-- Table structure for table `defaultGradeBreakdowns`
--

DROP TABLE IF EXISTS `defaultGradeBreakdowns`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `defaultGradeBreakdowns` (
  `docletGradeSectionId` int(11) NOT NULL,
  `maxGrade` int(11) NOT NULL,
  KEY `docletGradeSectionId` (`docletGradeSectionId`),
  CONSTRAINT `defaultGradeBreakdowns_ibfk_1` FOREIGN KEY (`docletGradeSectionId`) REFERENCES `docletGradeSections` (`docletGradeSectionId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `defaultGradeBreakdowns`
--

/*!40000 ALTER TABLE `defaultGradeBreakdowns` DISABLE KEYS */;
INSERT INTO `defaultGradeBreakdowns` VALUES (1,3),(2,6),(3,1),(4,5),(5,5),(18,5),(21,0),(22,0),(23,0),(24,0);
/*!40000 ALTER TABLE `defaultGradeBreakdowns` ENABLE KEYS */;

--
-- Table structure for table `defaultGradeParameters`
--

DROP TABLE IF EXISTS `defaultGradeParameters`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `defaultGradeParameters` (
  `docletGradeParameterId` int(11) NOT NULL,
  `param` varchar(1024) NOT NULL,
  KEY `docletGradeParameterId` (`docletGradeParameterId`),
  CONSTRAINT `defaultGradeParameters_ibfk_1` FOREIGN KEY (`docletGradeParameterId`) REFERENCES `docletGradeParameters` (`docletGradeParameterId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `defaultGradeParameters`
--

/*!40000 ALTER TABLE `defaultGradeParameters` DISABLE KEYS */;
INSERT INTO `defaultGradeParameters` VALUES (1,'10'),(5,''),(6,''),(7,''),(8,''),(9,'');
/*!40000 ALTER TABLE `defaultGradeParameters` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-03-06 17:38:09
