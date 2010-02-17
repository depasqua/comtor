#!bin/bash
#
#######################################################################
#                                                                     #
#Note on usage:                                                       #
#                                                                     #
#This script will build a tar.gz file of comtor for deployment. It    #
#assumes that the location of this script is in the main directory    #
#of where COMTOR was unpacked.                                        #
#                                                                     #
#In order to run this script, it is necessary to have root access.    #
#                                                                     #
#                                                                     #
#To run this file, use the command "bash packager.sh" without quotes. #
#                                                                     #
#######################################################################

if ["$1" == "unlock"]; then

echo "Unlocking..."

#unlock /comtor_data/uploads
cd ../comtor_data
chmod go+w uploads

#unlock /comtor_data/config/config.php and
#	/comtor_data/config/java.properties

cd config
chmod go+rw config.php java.properties
cd ../..

#unlock /www/config.php
cd www
chmod go+rw config.php

#unlock /www/templates_c
chmod go+w templates_c

#unlock /www/scripts/javadoc.sh and
#	/www/scripts/config.sh
cd scripts
chmod go+wx javadoc.sh config.sh
cd ../../install

echo "Done."

fi

if ["$1" == "lock"]; then

echo "Relocking..."

#relock /comtor_data/config/config.php and
#	/comtor_data/config/java.properties
cd ../comtor_data/config
chmod go-w config.php java.properties
cd ../..

#relock /www/config.php
cd www
chmod go-w config.php

#relock /www/scripts/javadoc.sh and
#	/www/scripts/config.sh
cd scripts
chmod go-w javadoc.sh config.sh
cd ../../install

echo "Done."

fi
