#!/bin/bash
#
#######################################################################
#                                                                     #
#Note on usage:                                                       #
#                                                                     #
#This script will unlock certain files (change permissions for the web#
#installer to write, and then relock MOST of the same files (some of  #
#the directories that are "unlocked" need to remain so).              #
#                                                                     #
#In order to run this script, it is probably necessary to have root   #
#access.                                                              #
#                                                                     #
#                                                                     #
#To run this file, use the command "bash packager.sh OPTION" without  #
#quotes, and replace OPTION with either "unlock" or "lock" (again,    #
#without quotes).
#                                                                     #
#######################################################################

if [ "$1" = "unlock" ] ; then

echo "Unlocking..."

#unlock /comtor_data/uploads
cd ../comtor_data
chmod go+w uploads

#unlock /comtor_data/config/config.php and
#	/comtor_data/config/java.properties

cd config
chmod go+rw config.php
chmod go+rw java.properties
cd ../..

#unlock /www/config.php
cd www
chmod go+rw config.php

#unlock /www/templates_c
chmod go+w templates_c

#unlock /www/scripts/javadoc.sh and
#	/www/scripts/config.sh
cd scripts
chmod go+wx javadoc.sh
chmod go+wx config.sh
cd ../../install

echo "Done."

fi

if [ "$1" = "lock" ] ; then

echo "Relocking..."

#relock /comtor_data/config/config.php and
#	/comtor_data/config/java.properties
cd ../comtor_data/config
chmod go-w config.php
chmod go-w java.properties
cd ../..

#relock /www/config.php
cd www
chmod go-w config.php

#relock /www/scripts/javadoc.sh and
#	/www/scripts/config.sh
cd scripts
chmod go-w javadoc.sh
chmod go-w config.sh
cd ../../install

echo "Done."

fi

