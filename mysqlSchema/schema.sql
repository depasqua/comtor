-- MySQL dump 10.11
--
-- Host: localhost    Database: comtor_dev
-- ------------------------------------------------------
-- Server version	5.0.51a-3ubuntu5.1

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
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `assignmentEvents` (
  `assignmentEventId` int(11) NOT NULL auto_increment COMMENT 'Id of this assignment event.',
  `userEventId` int(11) NOT NULL COMMENT 'Id of the userEvent that this record corresponds to.',
  `assignmentId` int(11) NOT NULL COMMENT 'Id of the assignment that this record corresponds to.',
  PRIMARY KEY  (`assignmentEventId`),
  KEY `userEventId` (`userEventId`),
  KEY `assignmentId` (`assignmentId`),
  CONSTRAINT `assignmentEvents_ibfk_2` FOREIGN KEY (`userEventId`) REFERENCES `userEvents` (`userEventId`) ON DELETE CASCADE,
  CONSTRAINT `assignmentEvents_ibfk_1` FOREIGN KEY (`assignmentId`) REFERENCES `assignments` (`assignmentId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=278 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `assignmentGradeBreakdowns`
--

DROP TABLE IF EXISTS `assignmentGradeBreakdowns`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `assignmentGradeBreakdowns` (
  `assignmentId` int(11) NOT NULL,
  `docletGradeSectionId` int(11) NOT NULL,
  `maxGrade` int(11) NOT NULL,
  PRIMARY KEY  (`assignmentId`,`docletGradeSectionId`),
  KEY `docletGradeSectionId` (`docletGradeSectionId`),
  CONSTRAINT `assignmentGradeBreakdowns_ibfk_1` FOREIGN KEY (`assignmentId`) REFERENCES `assignments` (`assignmentId`) ON DELETE CASCADE,
  CONSTRAINT `assignmentGradeBreakdowns_ibfk_2` FOREIGN KEY (`docletGradeSectionId`) REFERENCES `docletGradeSections` (`docletGradeSectionId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `assignmentGradeBreakdownsView`
--

DROP TABLE IF EXISTS `assignmentGradeBreakdownsView`;
/*!50001 DROP VIEW IF EXISTS `assignmentGradeBreakdownsView`*/;
/*!50001 CREATE TABLE `assignmentGradeBreakdownsView` (
  `docletGradeSectionId` int(11),
  `assignmentId` int(11),
  `maxGrade` bigint(11)
) */;

--
-- Table structure for table `assignmentGradeParameters`
--

DROP TABLE IF EXISTS `assignmentGradeParameters`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `assignmentGradeParameters` (
  `assignmentId` int(11) NOT NULL,
  `docletGradeParameterId` int(11) NOT NULL,
  `param` varchar(1024) NOT NULL,
  PRIMARY KEY  (`assignmentId`,`docletGradeParameterId`),
  KEY `docletGradeParameterId` (`docletGradeParameterId`),
  CONSTRAINT `assignmentGradeParameters_ibfk_2` FOREIGN KEY (`docletGradeParameterId`) REFERENCES `docletGradeParameters` (`docletGradeParameterId`) ON DELETE CASCADE,
  CONSTRAINT `assignmentGradeParameters_ibfk_1` FOREIGN KEY (`assignmentId`) REFERENCES `assignments` (`assignmentId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `assignmentGradeParametersView`
--

DROP TABLE IF EXISTS `assignmentGradeParametersView`;
/*!50001 DROP VIEW IF EXISTS `assignmentGradeParametersView`*/;
/*!50001 CREATE TABLE `assignmentGradeParametersView` (
  `docletGradeParameterId` int(11),
  `assignmentId` int(11),
  `param` longtext
) */;

--
-- Table structure for table `assignments`
--

DROP TABLE IF EXISTS `assignments`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `assignments` (
  `assignmentId` int(11) NOT NULL auto_increment COMMENT 'Unique id for the assignment',
  `courseId` int(11) NOT NULL COMMENT 'Id of the course that the assignment is for (from courses table)',
  `name` varchar(255) NOT NULL COMMENT 'Name of the assignment',
  `openTime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `closeTime` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`assignmentId`),
  KEY `courseId` (`courseId`),
  CONSTRAINT `assignments_ibfk_1` FOREIGN KEY (`courseId`) REFERENCES `courses` (`courseId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `courseEvents`
--

DROP TABLE IF EXISTS `courseEvents`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `courseEvents` (
  `courseEventId` int(11) NOT NULL auto_increment COMMENT 'Id of this course event.',
  `userEventId` int(11) NOT NULL COMMENT 'Id of the userEvent that this record corresponds to.',
  `courseId` int(11) NOT NULL COMMENT 'Id of the course that this record corresponds to.',
  PRIMARY KEY  (`courseEventId`),
  KEY `courseId` (`courseId`),
  KEY `userEventId` (`userEventId`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `courses` (
  `courseId` int(11) NOT NULL auto_increment COMMENT 'Unique id for the course',
  `profId` int(11) NOT NULL COMMENT 'Id of the professor (from users table)',
  `section` varchar(20) NOT NULL COMMENT 'Section of course in format CSC 380-01 or similar',
  `name` varchar(255) NOT NULL COMMENT 'Name of the course',
  `semester` varchar(255) NOT NULL COMMENT 'Semester that the course is held',
  `comment` text COMMENT 'Additional comments that a professor might have for the course',
  `status` enum('enabled','disabled') NOT NULL default 'enabled',
  PRIMARY KEY  (`courseId`)
) ENGINE=InnoDB AUTO_INCREMENT=2422 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `cronjobs`
--

DROP TABLE IF EXISTS `cronjobs`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cronjobs` (
  `time` int(11) NOT NULL,
  PRIMARY KEY  (`time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

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
-- Table structure for table `docletEvents`
--

DROP TABLE IF EXISTS `docletEvents`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `docletEvents` (
  `docletEventId` int(11) NOT NULL auto_increment COMMENT 'Unique id of a doclet event associated with the userEvent',
  `userEventId` int(11) NOT NULL COMMENT 'Id of the user''s submission event (from userEvents table)',
  `docletId` int(11) NOT NULL COMMENT 'Id of the doclet used in this event (from doclets table)',
  `score` float NOT NULL,
  PRIMARY KEY  (`docletEventId`)
) ENGINE=InnoDB AUTO_INCREMENT=504 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `docletOutputItems`
--

DROP TABLE IF EXISTS `docletOutputItems`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `docletOutputItems` (
  `docletOutputItemId` int(11) NOT NULL auto_increment COMMENT 'Unique id for this doclet output entry',
  `docletEventId` int(11) NOT NULL COMMENT 'Id of the docletEvent that this entry corresponds to',
  `attribute` text NOT NULL COMMENT 'Name of the attribute that is being stored',
  `value` longtext NOT NULL COMMENT 'Value of the attribute being stored',
  PRIMARY KEY  (`docletOutputItemId`),
  KEY `docletEventId` (`docletEventId`)
) ENGINE=InnoDB AUTO_INCREMENT=21045 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

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
-- Table structure for table `enrollments`
--

DROP TABLE IF EXISTS `enrollments`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `enrollments` (
  `courseId` int(11) NOT NULL COMMENT 'Id of the course the student is in (from courses table)',
  `studentId` int(11) NOT NULL COMMENT 'Id of the student (from users table)',
  PRIMARY KEY  (`courseId`,`studentId`),
  KEY `studentId` (`studentId`),
  CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`studentId`) REFERENCES `users` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`courseId`) REFERENCES `courses` (`courseId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `enrollmentsView`
--

DROP TABLE IF EXISTS `enrollmentsView`;
/*!50001 DROP VIEW IF EXISTS `enrollmentsView`*/;
/*!50001 CREATE TABLE `enrollmentsView` (
  `courseId` int(11),
  `studentId` int(11),
  `userName` varchar(255),
  `courseName` varchar(255),
  `courseSection` varchar(20),
  `courseProfId` int(11)
) */;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `files`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `files` (
  `userEventId` int(11) NOT NULL COMMENT 'Id of the user''s submission from the userEvents table',
  `filename` varchar(255) NOT NULL COMMENT 'Name of the file',
  `contents` mediumtext NOT NULL COMMENT 'The actual file contents',
  KEY `reportId` (`userEventId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Holds the files that all users submit for their reports';
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `request_acct_change`
--

DROP TABLE IF EXISTS `request_acct_change`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `request_acct_change` (
  `req_id` int(11) NOT NULL auto_increment,
  `userId` int(11) NOT NULL,
  `acct_type` enum('student','professor','admin') NOT NULL,
  `req_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `status` enum('pending','accepted','rejected') NOT NULL default 'pending',
  `comment` text,
  PRIMARY KEY  (`req_id`),
  KEY `userId` (`userId`),
  CONSTRAINT `request_acct_change_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `request_acct_change_view`
--

DROP TABLE IF EXISTS `request_acct_change_view`;
/*!50001 DROP VIEW IF EXISTS `request_acct_change_view`*/;
/*!50001 CREATE TABLE `request_acct_change_view` (
  `req_id` int(11),
  `userId` int(11),
  `acct_type` enum('student','professor','admin'),
  `req_date` timestamp,
  `status` enum('pending','accepted','rejected'),
  `comment` text,
  `name` varchar(255)
) */;

--
-- Table structure for table `request_acct_deletions`
--

DROP TABLE IF EXISTS `request_acct_deletions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `request_acct_deletions` (
  `userId` int(11) NOT NULL,
  `req_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `status` enum('pending','accepted','rejected') NOT NULL default 'pending',
  PRIMARY KEY  (`userId`),
  CONSTRAINT `request_acct_deletions_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `request_acct_deletions_view`
--

DROP TABLE IF EXISTS `request_acct_deletions_view`;
/*!50001 DROP VIEW IF EXISTS `request_acct_deletions_view`*/;
/*!50001 CREATE TABLE `request_acct_deletions_view` (
  `userId` int(11),
  `req_date` timestamp,
  `status` enum('pending','accepted','rejected'),
  `name` varchar(255)
) */;

--
-- Table structure for table `schools`
--

DROP TABLE IF EXISTS `schools`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `schools` (
  `schoolId` int(11) NOT NULL auto_increment,
  `school` varchar(256) NOT NULL,
  PRIMARY KEY  (`schoolId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `userEventComments`
--

DROP TABLE IF EXISTS `userEventComments`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `userEventComments` (
  `userEventId` int(11) NOT NULL,
  `comment` text NOT NULL,
  PRIMARY KEY  (`userEventId`),
  CONSTRAINT `userEventComments_ibfk_1` FOREIGN KEY (`userEventId`) REFERENCES `userEvents` (`userEventId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `userEventCompilationErrors`
--

DROP TABLE IF EXISTS `userEventCompilationErrors`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `userEventCompilationErrors` (
  `userEventId` int(11) NOT NULL,
  `output` text NOT NULL COMMENT 'Compilation output from javac.',
  PRIMARY KEY  (`userEventId`),
  CONSTRAINT `userEventCompilationErrors_ibfk_2` FOREIGN KEY (`userEventId`) REFERENCES `userEvents` (`userEventId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `userEvents`
--

DROP TABLE IF EXISTS `userEvents`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `userEvents` (
  `userEventId` int(11) NOT NULL auto_increment COMMENT 'Unique id for the user''s submission',
  `userId` int(11) NOT NULL COMMENT 'Id of the user who requested the event (from users table)',
  `dateTime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'Time that the event took place',
  PRIMARY KEY  (`userEventId`),
  KEY `userId` (`userId`),
  CONSTRAINT `userEvents_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=369 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `userEventsAssignmentOptions`
--

DROP TABLE IF EXISTS `userEventsAssignmentOptions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `userEventsAssignmentOptions` (
  `assignmentId` int(11) NOT NULL,
  `docletId` int(11) NOT NULL,
  PRIMARY KEY  (`assignmentId`,`docletId`),
  KEY `docletId` (`docletId`),
  KEY `assignmentId` (`assignmentId`),
  CONSTRAINT `userEventsAssignmentOptions_ibfk_1` FOREIGN KEY (`assignmentId`) REFERENCES `assignments` (`assignmentId`) ON DELETE CASCADE,
  CONSTRAINT `userEventsAssignmentOptions_ibfk_2` FOREIGN KEY (`docletId`) REFERENCES `doclets` (`docletId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `userEventsView`
--

DROP TABLE IF EXISTS `userEventsView`;
/*!50001 DROP VIEW IF EXISTS `userEventsView`*/;
/*!50001 CREATE TABLE `userEventsView` (
  `userEventId` int(11),
  `userId` int(11),
  `dateTime` timestamp,
  `userName` varchar(255),
  `courseName` varchar(255),
  `courseSection` varchar(20),
  `profId` int(11),
  `courseId` int(11),
  `assignmentId` int(11),
  `assignmentName` varchar(255)
) */;

--
-- Temporary table structure for view `userEventsViewWErrors`
--

DROP TABLE IF EXISTS `userEventsViewWErrors`;
/*!50001 DROP VIEW IF EXISTS `userEventsViewWErrors`*/;
/*!50001 CREATE TABLE `userEventsViewWErrors` (
  `userEventId` int(11),
  `userId` int(11),
  `dateTime` timestamp,
  `userName` varchar(255),
  `courseName` varchar(255),
  `courseSection` varchar(20),
  `profId` int(11),
  `courseId` int(11),
  `assignmentId` int(11),
  `assignmentName` varchar(255),
  `compilationOutput` text,
  `compilationError` int(1)
) */;

--
-- Table structure for table `userNotificationOptions`
--

DROP TABLE IF EXISTS `userNotificationOptions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `userNotificationOptions` (
  `userId` int(11) NOT NULL,
  `notifications` int(11) NOT NULL default '0' COMMENT 'Bitwise combination of options defined in mysqlFunctions.php',
  `frequency` int(11) NOT NULL default '0' COMMENT 'Bitwise combination of options defined in mysqlFunctions.php',
  PRIMARY KEY  (`userId`),
  CONSTRAINT `userNotificationOptions_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `userNotifications`
--

DROP TABLE IF EXISTS `userNotifications`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `userNotifications` (
  `userNotificationId` int(11) NOT NULL auto_increment,
  `userId` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  `content` text NOT NULL,
  `subject` varchar(256) NOT NULL,
  PRIMARY KEY  (`userNotificationId`),
  KEY `userNotifications_ibfk_1` (`userId`),
  CONSTRAINT `userNotifications_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `users` (
  `userId` int(11) NOT NULL auto_increment COMMENT 'A unique key for each user.',
  `name` varchar(255) NOT NULL COMMENT 'The name of the user.',
  `email` varchar(255) NOT NULL COMMENT 'The email address of the user.',
  `password` varchar(255) NOT NULL COMMENT 'The user''s encryped password.',
  `dateTime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'The date and time the user''s account was created.',
  `validatedDT` timestamp NULL default NULL COMMENT 'The date and time the user''s account was validated.',
  `lastLogin` timestamp NULL default NULL COMMENT 'The date and time of the user''s last login.',
  `passwordChangeDT` timestamp NULL default NULL COMMENT 'The date and time the user changed their password.',
  `acctType` enum('student','admin','professor') NOT NULL COMMENT 'Account type of the user',
  `acctStatus` enum('enabled','disabled') NOT NULL COMMENT 'The user''s account status.',
  `schoolId` int(11) NOT NULL,
  PRIMARY KEY  (`userId`),
  KEY `schoolId` (`schoolId`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`schoolId`) REFERENCES `schools` (`schoolId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `users_view`
--

DROP TABLE IF EXISTS `users_view`;
/*!50001 DROP VIEW IF EXISTS `users_view`*/;
/*!50001 CREATE TABLE `users_view` (
  `userId` int(11),
  `name` varchar(255),
  `email` varchar(255),
  `password` varchar(255),
  `dateTime` timestamp,
  `validatedDT` timestamp,
  `lastLogin` timestamp,
  `passwordChangeDT` timestamp,
  `acctType` enum('student','admin','professor'),
  `acctStatus` enum('enabled','disabled'),
  `schoolId` int(11),
  `school` varchar(256)
) */;

--
-- Final view structure for view `assignmentGradeBreakdownsView`
--

/*!50001 DROP TABLE `assignmentGradeBreakdownsView`*/;
/*!50001 DROP VIEW IF EXISTS `assignmentGradeBreakdownsView`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `assignmentGradeBreakdownsView` AS select `dGB`.`docletGradeSectionId` AS `docletGradeSectionId`,`a`.`assignmentId` AS `assignmentId`,if(isnull(`aGB`.`maxGrade`),`dGB`.`maxGrade`,`aGB`.`maxGrade`) AS `maxGrade` from ((`assignments` `a` join `defaultGradeBreakdowns` `dGB`) left join `assignmentGradeBreakdowns` `aGB` on(((`dGB`.`docletGradeSectionId` = `aGB`.`docletGradeSectionId`) and (`a`.`assignmentId` = `aGB`.`assignmentId`)))) */;

--
-- Final view structure for view `assignmentGradeParametersView`
--

/*!50001 DROP TABLE `assignmentGradeParametersView`*/;
/*!50001 DROP VIEW IF EXISTS `assignmentGradeParametersView`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `assignmentGradeParametersView` AS select `dGP`.`docletGradeParameterId` AS `docletGradeParameterId`,`a`.`assignmentId` AS `assignmentId`,if(isnull(`aGP`.`param`),`dGP`.`param`,`aGP`.`param`) AS `param` from ((`assignments` `a` join `defaultGradeParameters` `dGP`) left join `assignmentGradeParameters` `aGP` on(((`dGP`.`docletGradeParameterId` = `aGP`.`docletGradeParameterId`) and (`a`.`assignmentId` = `aGP`.`assignmentId`)))) */;

--
-- Final view structure for view `enrollmentsView`
--

/*!50001 DROP TABLE `enrollmentsView`*/;
/*!50001 DROP VIEW IF EXISTS `enrollmentsView`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `enrollmentsView` AS select `e`.`courseId` AS `courseId`,`e`.`studentId` AS `studentId`,`users`.`name` AS `userName`,`c`.`name` AS `courseName`,`c`.`section` AS `courseSection`,`c`.`profId` AS `courseProfId` from ((`enrollments` `e` join `users` on((`e`.`studentId` = `users`.`userId`))) join `courses` `c` on((`c`.`courseId` = `e`.`courseId`))) */;

--
-- Final view structure for view `request_acct_change_view`
--

/*!50001 DROP TABLE `request_acct_change_view`*/;
/*!50001 DROP VIEW IF EXISTS `request_acct_change_view`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `request_acct_change_view` AS select `req`.`req_id` AS `req_id`,`req`.`userId` AS `userId`,`req`.`acct_type` AS `acct_type`,`req`.`req_date` AS `req_date`,`req`.`status` AS `status`,`req`.`comment` AS `comment`,`users`.`name` AS `name` from (`request_acct_change` `req` left join `users` on((`req`.`userId` = `users`.`userId`))) */;

--
-- Final view structure for view `request_acct_deletions_view`
--

/*!50001 DROP TABLE `request_acct_deletions_view`*/;
/*!50001 DROP VIEW IF EXISTS `request_acct_deletions_view`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `request_acct_deletions_view` AS select `req`.`userId` AS `userId`,`req`.`req_date` AS `req_date`,`req`.`status` AS `status`,`users`.`name` AS `name` from (`request_acct_deletions` `req` left join `users` on((`req`.`userId` = `users`.`userId`))) */;

--
-- Final view structure for view `userEventsView`
--

/*!50001 DROP TABLE `userEventsView`*/;
/*!50001 DROP VIEW IF EXISTS `userEventsView`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `userEventsView` AS select `uE`.`userEventId` AS `userEventId`,`uE`.`userId` AS `userId`,`uE`.`dateTime` AS `dateTime`,`users`.`name` AS `userName`,`c`.`name` AS `courseName`,`c`.`section` AS `courseSection`,`c`.`profId` AS `profId`,`c`.`courseId` AS `courseId`,`a`.`assignmentId` AS `assignmentId`,`a`.`name` AS `assignmentName` from (((`userEvents` `uE` join `users` on((`uE`.`userId` = `users`.`userId`))) left join `assignments` `a` on(`a`.`assignmentId` in (select `assignmentEvents`.`assignmentId` AS `assignmentId` from `assignmentEvents` where (`assignmentEvents`.`userEventId` = `uE`.`userEventId`)))) left join `courses` `c` on((`c`.`courseId` in (select `courseEvents`.`courseId` AS `courseId` from `courseEvents` where (`courseEvents`.`userEventId` = `uE`.`userEventId`)) or (`c`.`courseId` = `a`.`courseId`)))) */;

--
-- Final view structure for view `userEventsViewWErrors`
--

/*!50001 DROP TABLE `userEventsViewWErrors`*/;
/*!50001 DROP VIEW IF EXISTS `userEventsViewWErrors`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `userEventsViewWErrors` AS select `uEV`.`userEventId` AS `userEventId`,`uEV`.`userId` AS `userId`,`uEV`.`dateTime` AS `dateTime`,`uEV`.`userName` AS `userName`,`uEV`.`courseName` AS `courseName`,`uEV`.`courseSection` AS `courseSection`,`uEV`.`profId` AS `profId`,`uEV`.`courseId` AS `courseId`,`uEV`.`assignmentId` AS `assignmentId`,`uEV`.`assignmentName` AS `assignmentName`,`cE`.`output` AS `compilationOutput`,(`cE`.`output` is not null) AS `compilationError` from (`userEventsView` `uEV` left join `userEventCompilationErrors` `cE` on((`uEV`.`userEventId` = `cE`.`userEventId`))) */;

--
-- Final view structure for view `users_view`
--

/*!50001 DROP TABLE `users_view`*/;
/*!50001 DROP VIEW IF EXISTS `users_view`*/;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`comtor`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `users_view` AS select `u`.`userId` AS `userId`,`u`.`name` AS `name`,`u`.`email` AS `email`,`u`.`password` AS `password`,`u`.`dateTime` AS `dateTime`,`u`.`validatedDT` AS `validatedDT`,`u`.`lastLogin` AS `lastLogin`,`u`.`passwordChangeDT` AS `passwordChangeDT`,`u`.`acctType` AS `acctType`,`u`.`acctStatus` AS `acctStatus`,`u`.`schoolId` AS `schoolId`,`s`.`school` AS `school` from (`users` `u` join `schools` `s` on((`u`.`schoolId` = `s`.`schoolId`))) */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2008-11-05 22:31:47
