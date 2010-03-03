#!/bin/bash
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
#This script selects the correct files based on exclusion; therefore, #
#for future releases, it may be necessary to add directory or file    #
#exclusions. See below on how to do this.                             #
#                                                                     #
#To run this file, use the command "bash packager.sh" without quotes. #
#                                                                     #
#######################################################################

echo "Packaging a COMTOR release for deployment..."

echo "What release number is this?"

read REL

echo "Where would you like to place this package? (relative directory)"

read RDEST

DEST="$RDEST"

SRC='*'

NAME='comtor'
NAME+=$REL

CMD1='tar '

#######################################################################
#                                                                     #
# Listed below are the directories and files to exclude from release. #
# To add more, follow this format:                                    #
#                                                                     #
# CMD1+="--exclude=relative_directory_to_exclude "                    #
#                                                                     #
#######################################################################

CMD1+="--exclude=*.DS_Store "
CMD1+="--exclude=*.gitignore "
CMD1+="--exclude=designdocs "
CMD1+="--exclude=packager.sh "
CMD1+="--exclude=testingCode "
CMD1+="--exclude=unitTesting "

CMD1+="-czf "
CMD1+="$DEST"/"$NAME"'.tar.gz '"$SRC"

echo "Now packaging..."
echo $CMD1
$CMD1

#chmod to change group permissions
CMD2='chmod g=rw '
CMD2+="$DEST"/"$NAME"'.tar.gz'

$CMD2

echo "Script done."
#exit $?
