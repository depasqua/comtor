#!bin/bash
#
#######################################################################
#                                                                     #
#Note on usage:                                                       #
#                                                                     #
#This file will call an ant build file to compile the COMTOR source   #
#code.                                                                #
#                                                                     #
#######################################################################

echo "Please ensure that you are running this script from the"
echo "absolute directory that COMTOR (and therefore, also this"
echo "file) is in. If you are not, please stop and re-run from"
echo "the proper directory. [Press ENTER to continue.]"

read PAUSE

RDIR=`pwd`

DIR=':'
DIR+=$RDIR+'comtor_data/code/stringtemplate-3.2.jar:'
DIR+=$RDIR+'comtor_data/code/antlr-3.1.1.jar:'
DIR+=$RDIR+'comtor_data/code/mysql-connector-java-5.1.6-bin.jar:'
DIR+=$RDIR+'comtor_data/code/comtor.jar:'
DIR+=$RDIR+'comtor_data/code/antlr-3.1.1-runtime.jar'

echo "Compiling COMTOR code files..."

cd code/
mkdir classes

CMD1='export set CLASSPATH=.:$CLASSPATH:.'+$DIR

$CMD1

echo "Now compiling..."

ant compile

echo "Done."

cd classes
jar -cf comtor.jar comtor

exit $?

