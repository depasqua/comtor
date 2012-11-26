-- WHEN EDITING THIS FILE --
-- When you are editing this file, please ONLY edit the YOURNAMEHERE and YOUREMAILHERE values.
--
-- Please replace YOURNAMEHERE with the name you wish the initial admin account to have.
-- Please replace YOUREMAILHERE with the email you wish to log in with for the initial admin account.
--
-- The value you specify for YOUREMAILHERE will be your initial log in for COMTOR.
-- The initial password is 'comtor', without the single quotes.

INSERT INTO `users` (`name`, `email`, `password`, `validatedDT`, `passwordChangeDT`, `acctType`, `acctStatus`, `schoolId`)
VALUES ('YOURNAMEHERE', 'YOUREMAILHERE', 'cmhXE1d/ItCiM', NOW(), NOW(), 'admin', 'enabled', 4);
