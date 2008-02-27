-- MySQL dump 10.10
--
-- Host: localhost    Database: comtor
-- ------------------------------------------------------
-- Server version	5.0.22-Debian_0ubuntu6.06.6-log

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
-- Table structure for table `assignments`
--

DROP TABLE IF EXISTS `assignments`;
CREATE TABLE `assignments` (
  `id` int(11) NOT NULL auto_increment COMMENT 'Unique id for the assignment',
  `courseId` int(11) NOT NULL COMMENT 'Id of the course that the assignment is for (from courses table)',
  `name` varchar(255) NOT NULL COMMENT 'Name of the assignment',
  `openTime` timestamp NULL default NULL COMMENT 'Starting time for submission',
  `closeTime` timestamp NULL default NULL COMMENT 'Ending time for submission',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `id` int(11) NOT NULL auto_increment COMMENT 'Unique id for the course',
  `profId` int(11) NOT NULL COMMENT 'Id of the professor (from users table)',
  `section` varchar(20) NOT NULL COMMENT 'Section of course in format CSC 380-01 or similar',
  `name` varchar(255) NOT NULL COMMENT 'Name of the course',
  `semester` varchar(255) NOT NULL COMMENT 'Semester that the course is held',
  `comment` text COMMENT 'Additional comments that a professor might have for the course',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `docletReports`
--

DROP TABLE IF EXISTS `docletReports`;
CREATE TABLE `docletReports` (
  `dataId` int(11) NOT NULL auto_increment COMMENT 'Unique id for this property entry',
  `reportId` int(11) NOT NULL COMMENT 'Subreport id that this property corresponds to',
  `attribute` text NOT NULL COMMENT 'Name of the attribute that is being stored',
  `value` longtext NOT NULL COMMENT 'Value of the property being stored',
  PRIMARY KEY  (`dataId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `masterDoclets`
--

DROP TABLE IF EXISTS `masterDoclets`;
CREATE TABLE `masterDoclets` (
  `id` int(11) NOT NULL auto_increment COMMENT 'Unique id of a subreport of the master report',
  `masterReportId` int(11) NOT NULL COMMENT 'Id of the user''s report (from masterReports table)',
  `docletReportId` int(11) NOT NULL COMMENT 'Id of the doclet used in this subreport (from reports table)',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `masterReports`
--

DROP TABLE IF EXISTS `masterReports`;
CREATE TABLE `masterReports` (
  `id` int(11) NOT NULL auto_increment COMMENT 'Unique id for the user''s report',
  `userId` int(11) NOT NULL COMMENT 'Id of the user who requested the report (from users table)',
  `dateTime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'Time that the report was run',
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `reportProperties`
--

DROP TABLE IF EXISTS `reportProperties`;
CREATE TABLE `reportProperties` (
  `reportId` int(11) NOT NULL COMMENT 'Id of a student''s report (from masterReports table)',
  `table` enum('courses','assignments') NOT NULL COMMENT 'Name of the table that the property refers to.',
  `propertyId` int(11) NOT NULL COMMENT 'Id that will in some way be used in the table indicated to retrieve a property of the user''s report'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports` (
  `reportID` int(11) NOT NULL COMMENT 'The Id of the report that was run.',
  `reportName` varchar(255) NOT NULL COMMENT 'The name of the report that was run.',
  `reportDescription` varchar(255) NOT NULL COMMENT 'The description of the report that was run.',
  `javaName` varchar(256) NOT NULL COMMENT 'The name of the java class',
  PRIMARY KEY  (`reportID`),
  UNIQUE KEY `reportName` (`reportName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `roster`
--

DROP TABLE IF EXISTS `roster`;
CREATE TABLE `roster` (
  `courseID` int(11) NOT NULL COMMENT 'Id of the course the student is in (from courses table)',
  `studentId` int(11) NOT NULL COMMENT 'Id of the student (from users table)'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `userID` int(11) NOT NULL auto_increment COMMENT 'A unique key for each user.',
  `name` varchar(255) NOT NULL COMMENT 'The name of the user.',
  `email` varchar(255) NOT NULL COMMENT 'The email address of the user.',
  `password` varchar(255) NOT NULL COMMENT 'The user''s encryped password.',
  `dateTime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'The date and time the user''s account was created.',
  `validatedDT` timestamp NULL default NULL COMMENT 'The date and time the user''s account was validated.',
  `lastLogin` timestamp NULL default NULL COMMENT 'The date and time of the user''s last login.',
  `passwordChangeDT` timestamp NULL default NULL COMMENT 'The date and time the user changed their password.',
  `school` varchar(255) NOT NULL COMMENT 'The user''s school.',
  `acctType` enum('basic','admin','professor') NOT NULL COMMENT 'Account type of the user',
  `acctStatus` varchar(255) NOT NULL COMMENT 'The user''s account status.',
  PRIMARY KEY  (`userID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

