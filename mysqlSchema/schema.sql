-- MySQL dump 10.11
--
-- Host: localhost    Database: comtor
-- ------------------------------------------------------
-- Server version	5.0.45-Debian_1ubuntu3.3-log

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
-- Table structure for table `assignmentEvents`
--

DROP TABLE IF EXISTS `assignmentEvents`;
CREATE TABLE `assignmentEvents` (
  `assignmentEventId` int(11) NOT NULL auto_increment COMMENT 'Id of this assignment event.',
  `userEventId` int(11) NOT NULL COMMENT 'Id of the userEvent that this record corresponds to.',
  `assignmentId` int(11) NOT NULL COMMENT 'Id of the assignment that this record corresponds to.',
  PRIMARY KEY  (`assignmentEventId`),
  KEY `userEventId` (`userEventId`),
  KEY `assignmentId` (`assignmentId`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Table structure for table `assignments`
--

DROP TABLE IF EXISTS `assignments`;
CREATE TABLE `assignments` (
  `assignmentId` int(11) NOT NULL auto_increment COMMENT 'Unique id for the assignment',
  `courseId` int(11) NOT NULL COMMENT 'Id of the course that the assignment is for (from courses table)',
  `name` varchar(255) NOT NULL COMMENT 'Name of the assignment',
  `openTime` timestamp NULL default NULL COMMENT 'Starting time for submission',
  `closeTime` timestamp NULL default NULL COMMENT 'Ending time for submission',
  PRIMARY KEY  (`assignmentId`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Table structure for table `courseEvents`
--

DROP TABLE IF EXISTS `courseEvents`;
CREATE TABLE `courseEvents` (
  `courseEventId` int(11) NOT NULL auto_increment COMMENT 'Id of this course event.',
  `userEventId` int(11) NOT NULL COMMENT 'Id of the userEvent that this record corresponds to.',
  `courseId` int(11) NOT NULL COMMENT 'Id of the course that this record corresponds to.',
  PRIMARY KEY  (`courseEventId`),
  KEY `courseId` (`courseId`),
  KEY `userEventId` (`userEventId`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `courseId` int(11) NOT NULL auto_increment COMMENT 'Unique id for the course',
  `profId` int(11) NOT NULL COMMENT 'Id of the professor (from users table)',
  `section` varchar(20) NOT NULL COMMENT 'Section of course in format CSC 380-01 or similar',
  `name` varchar(255) NOT NULL COMMENT 'Name of the course',
  `semester` varchar(255) NOT NULL COMMENT 'Semester that the course is held',
  `comment` text COMMENT 'Additional comments that a professor might have for the course',
  PRIMARY KEY  (`courseId`)
) ENGINE=MyISAM AUTO_INCREMENT=2416 DEFAULT CHARSET=latin1;

--
-- Table structure for table `docletEvents`
--

DROP TABLE IF EXISTS `docletEvents`;
CREATE TABLE `docletEvents` (
  `docletEventId` int(11) NOT NULL auto_increment COMMENT 'Unique id of a doclet event associated with the userEvent',
  `userEventId` int(11) NOT NULL COMMENT 'Id of the user''s submission event (from userEvents table)',
  `docletId` int(11) NOT NULL COMMENT 'Id of the doclet used in this event (from doclets table)',
  PRIMARY KEY  (`docletEventId`)
) ENGINE=MyISAM AUTO_INCREMENT=128 DEFAULT CHARSET=latin1;

--
-- Table structure for table `docletOutputItems`
--

DROP TABLE IF EXISTS `docletOutputItems`;
CREATE TABLE `docletOutputItems` (
  `docletOutputItemId` int(11) NOT NULL auto_increment COMMENT 'Unique id for this doclet output entry',
  `docletEventId` int(11) NOT NULL COMMENT 'Id of the docletEvent that this entry corresponds to',
  `attribute` text NOT NULL COMMENT 'Name of the attribute that is being stored',
  `value` longtext NOT NULL COMMENT 'Value of the attribute being stored',
  PRIMARY KEY  (`docletOutputItemId`),
  KEY `docletEventId` (`docletEventId`)
) ENGINE=MyISAM AUTO_INCREMENT=4194 DEFAULT CHARSET=latin1;

--
-- Table structure for table `doclets`
--

DROP TABLE IF EXISTS `doclets`;
CREATE TABLE `doclets` (
  `docletId` int(11) NOT NULL COMMENT 'The Id of the doclet',
  `docletName` varchar(255) NOT NULL COMMENT 'The name of the doclet',
  `docletDescription` varchar(255) NOT NULL COMMENT 'The description of the doclet',
  `javaName` varchar(256) NOT NULL COMMENT 'The name of the java class',
  PRIMARY KEY  (`docletId`),
  UNIQUE KEY `reportName` (`docletName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `enrollments`
--

DROP TABLE IF EXISTS `enrollments`;
CREATE TABLE `enrollments` (
  `courseId` int(11) NOT NULL COMMENT 'Id of the course the student is in (from courses table)',
  `studentId` int(11) NOT NULL COMMENT 'Id of the student (from users table)',
  PRIMARY KEY  (`courseId`,`studentId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `files`;
CREATE TABLE `files` (
  `userEventId` int(11) NOT NULL COMMENT 'Id of the user''s submission from the userEvents table',
  `filename` varchar(255) NOT NULL COMMENT 'Name of the file',
  `contents` mediumtext NOT NULL COMMENT 'The actual file contents',
  KEY `reportId` (`userEventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Holds the files that all users submit for their reports';

--
-- Table structure for table `userEvents`
--

DROP TABLE IF EXISTS `userEvents`;
CREATE TABLE `userEvents` (
  `userEventId` int(11) NOT NULL auto_increment COMMENT 'Unique id for the user''s submission',
  `userId` int(11) NOT NULL COMMENT 'Id of the user who requested the event (from users table)',
  `dateTime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'Time that the event took place',
  PRIMARY KEY  (`userEventId`),
  KEY `userId` (`userId`)
) ENGINE=MyISAM AUTO_INCREMENT=76 DEFAULT CHARSET=latin1;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `userId` int(11) NOT NULL auto_increment COMMENT 'A unique key for each user.',
  `name` varchar(255) NOT NULL COMMENT 'The name of the user.',
  `email` varchar(255) NOT NULL COMMENT 'The email address of the user.',
  `password` varchar(255) NOT NULL COMMENT 'The user''s encryped password.',
  `dateTime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'The date and time the user''s account was created.',
  `validatedDT` timestamp NULL default NULL COMMENT 'The date and time the user''s account was validated.',
  `lastLogin` timestamp NULL default NULL COMMENT 'The date and time of the user''s last login.',
  `passwordChangeDT` timestamp NULL default NULL COMMENT 'The date and time the user changed their password.',
  `school` varchar(255) NOT NULL COMMENT 'The user''s school.',
  `acctType` enum('student','admin','professor') NOT NULL COMMENT 'Account type of the user',
  `acctStatus` enum('enabled','disabled') NOT NULL COMMENT 'The user''s account status.',
  PRIMARY KEY  (`userId`)
) ENGINE=MyISAM AUTO_INCREMENT=101 DEFAULT CHARSET=latin1;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2008-05-05 22:23:25
