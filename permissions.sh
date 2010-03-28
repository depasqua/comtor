#!/bin/bash
#
#######################################################################
#                                                                     #
#Note on usage:                                                       #
#                                                                     #
#This script will unlock certain files (change permissions for the web#
#installer to write).                                                 #
#                                                                     #
#In order to run this script, it is probably necessary to have root   #
#access.                                                              #
#                                                                     #
#                                                                     #
#To run this file, use the command "bash packager.sh".		      #
#                                                                     #
#######################################################################

echo "Unlocking..."

#unlock /comtor_data/uploads
cd comtor_data
chmod go+w uploads
cd ..

#unlock /www/templates_c
cd www
chmod go+w templates_c

#unlock /www/scripts/javadoc.sh
cd scripts
chmod go+rx javadoc.sh

echo "Done."

